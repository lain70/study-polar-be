#!/usr/bin/env python3
"""Run local RabbitMQ and the backend for the same terminal session."""

import argparse
import fcntl
import hashlib
import os
import secrets
from pathlib import Path
import shutil
import signal
import socket
import subprocess
import sys
import tempfile


ROOT = Path(__file__).resolve().parent.parent
COMPOSE_ARGS = ["compose", "-p", "polar-be-local", "-f", str(ROOT / "compose.rabbitmq.yml")]
COLIMA_PROFILE = "polar-be-local"


def main():
    parser = argparse.ArgumentParser(description="로컬 RabbitMQ와 백엔드를 함께 실행·종료합니다.")
    parser.add_argument("--verify", action="store_true", help="실제 RabbitMQ·DB 연동 테스트 후 종료")
    arguments = parser.parse_args()
    if not shutil.which("docker"):
        print("Docker와 Docker Compose를 설치하고 Docker 엔진을 실행해 주세요.", file=sys.stderr)
        return 1
    lock_name = hashlib.sha256(str(ROOT).encode()).hexdigest()[:16]
    with open(Path(tempfile.gettempdir()) / f"polar-be-local-{lock_name}.lock", "a") as lock:
        try:
            fcntl.flock(lock, fcntl.LOCK_EX | fcntl.LOCK_NB)
        except BlockingIOError:
            print("이 프로젝트의 로컬 실행 명령이 이미 실행 중입니다.", file=sys.stderr)
            return 1
        return run(arguments.verify)


def local_environment():
    environment = dict(os.environ, SPRING_PROFILES_ACTIVE="loc", RABBITMQ_HOST="localhost",
                       RABBITMQ_PORT="5672")
    if not all(environment.get(key) for key in ("RABBITMQ_USERNAME", "RABBITMQ_PASSWORD")):
        directory = ROOT / ".local"
        directory.mkdir(mode=0o700, exist_ok=True)
        credentials = directory / "rabbitmq.env"
        if not credentials.exists():
            descriptor = os.open(credentials, os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600)
            with os.fdopen(descriptor, "w") as output:
                output.write(f"RABBITMQ_USERNAME={secrets.token_hex(8)}\n")
                output.write(f"RABBITMQ_PASSWORD={secrets.token_urlsafe(32)}\n")
        credentials.chmod(0o600)
        for line in credentials.read_text().splitlines():
            key, value = line.split("=", 1)
            if key not in ("RABBITMQ_USERNAME", "RABBITMQ_PASSWORD") or not value:
                raise ValueError("로컬 RabbitMQ 계정 파일 형식을 확인해 주세요.")
            if not environment.get(key):
                environment[key] = value
    if sys.platform == "darwin" and not environment.get("JAVA_HOME"):
        java_home = subprocess.check_output(["/usr/libexec/java_home", "-v", "17"], text=True).strip()
        environment["JAVA_HOME"] = java_home
    return environment


def run(verify=False):
    environment = local_environment()
    if verify:
        environment["RUN_RABBITMQ_INTEGRATION"] = "true"
    use_colima = bool(shutil.which("colima"))
    docker = ["docker", "--context", "colima-" + COLIMA_PROFILE] if use_colima else ["docker"]
    compose = docker + COMPOSE_ARGS
    runtime_started = False
    child = None
    broker_started = False
    result = 0

    def interrupted(signum, frame):
        raise KeyboardInterrupt

    for sig in (signal.SIGINT, signal.SIGTERM, signal.SIGHUP):
        signal.signal(sig, interrupted)

    def execute(command):
        nonlocal child
        child = subprocess.Popen(command, cwd=ROOT, env=environment, start_new_session=True)
        code = child.wait()
        if code:
            raise subprocess.CalledProcessError(code, command)
        child = None

    try:
        for port, name in ((3306, "MySQL"), (6379, "Redis")):
            try:
                with socket.create_connection(("127.0.0.1", port), timeout=2):
                    pass
            except OSError as error:
                raise RuntimeError(f"로컬 {name}({port})를 먼저 실행해 주세요.") from error
        execute(["docker", "compose", "version"])
        if use_colima:
            runtime_started = True
            execute(["colima", "start", COLIMA_PROFILE, "--activate=false", "--cpu", "2",
                     "--memory", "2", "--disk", "10", "--mount", "none", "--ssh-config=false"])
        execute(docker + ["info", "--format", "{{.ServerVersion}}"])
        broker_started = True
        print("RabbitMQ를 시작하고 준비 상태를 기다립니다.", flush=True)
        execute(compose + ["up", "-d", "--wait", "--wait-timeout", "120", "rabbitmq"])
        if verify:
            print("실제 RabbitMQ·로컬 DB 연동을 포함한 테스트를 실행합니다.", flush=True)
            execute(["sh", "gradlew", "--no-daemon", "test", "--rerun-tasks"])
        else:
            print("로컬 서버를 시작합니다. Ctrl+C로 서버와 RabbitMQ를 함께 종료합니다.", flush=True)
            execute(["sh", "gradlew", "--no-daemon", "bootRun"])
    except KeyboardInterrupt:
        result = 130
    except (OSError, RuntimeError, subprocess.CalledProcessError) as error:
        print(f"로컬 실행 실패: {error}", file=sys.stderr)
        result = 1
    finally:
        for sig in (signal.SIGINT, signal.SIGTERM, signal.SIGHUP):
            signal.signal(sig, signal.SIG_IGN)
        if child is not None:
            try:
                os.killpg(child.pid, signal.SIGTERM)
            except ProcessLookupError:
                pass  # The process group has already exited.
            try:
                child.wait(timeout=30)
            except subprocess.TimeoutExpired:
                print("서버 종료 제한 시간을 초과하여 실행 프로세스를 강제 종료합니다.", file=sys.stderr)
                try:
                    os.killpg(child.pid, signal.SIGKILL)
                except ProcessLookupError:
                    pass  # The process group exited before the signal.
                child.wait()
        if broker_started:
            print("RabbitMQ를 중지합니다. 큐 데이터는 보존합니다.", flush=True)
            try:
                stopped = subprocess.run(compose + ["stop", "-t", "30", "rabbitmq"],
                                         cwd=ROOT, env=environment, timeout=60)
                if stopped.returncode:
                    print("RabbitMQ 중지 실패: Docker 상태를 확인하고 수동 중지해 주세요.", file=sys.stderr)
                    result = 1
            except (OSError, subprocess.TimeoutExpired) as error:
                print(f"RabbitMQ 중지 실패: {error}", file=sys.stderr)
                result = 1
        if runtime_started:
            print("프로젝트 전용 Colima를 중지합니다.", flush=True)
            try:
                stopped = subprocess.run(["colima", "stop", COLIMA_PROFILE], timeout=90)
                if stopped.returncode:
                    print("Colima 중지 실패: colima stop polar-be-local을 실행해 주세요.", file=sys.stderr)
                    result = 1
            except (OSError, subprocess.TimeoutExpired) as error:
                print(f"Colima 중지 실패: {error}", file=sys.stderr)
                result = 1
    return result


if __name__ == "__main__":
    try:
        sys.exit(main())
    except (OSError, ValueError, subprocess.CalledProcessError) as error:
        print(f"로컬 실행 환경 준비 실패: {error}", file=sys.stderr)
        sys.exit(1)
