# 백엔드 작업 지침

이 파일은 `polar-be/` 저장소 전체에 적용됩니다. 코드를 수정하기 전에 `docs/CODING_CONVENTIONS.md`를 반드시 읽고 준수합니다.

## 핵심 규칙

- Java 17과 Spring Boot 2.6 호환성을 유지합니다.
- 요청받지 않은 Public API와 데이터베이스 스키마 변경을 하지 않습니다.
- 기존 Controller, Service, Mapper, Model 계층과 `com.polar.bear.api` 패키지 구조를 유지합니다.
- MyBatis Mapper 인터페이스와 XML의 namespace 및 statement ID를 일치시킵니다.
- 비밀값, 서비스 키, 계정 정보와 환경별 URL을 코드에 새로 하드코딩하지 않습니다.
- 예외를 무시하거나 빈 catch 블록으로 숨기지 않습니다.
- 기존 주석과 TODO/FIXME를 삭제하지 않습니다.
- 요청 범위 밖의 리팩터링이나 일괄 포맷 변경을 하지 않습니다.

## 검증

- 변경 범위에 맞는 테스트를 우선 실행합니다.
- 전체 백엔드 검증은 `./gradlew test`를 사용합니다.
- Gradle Wrapper에 실행 권한이 없는 환경에서는 `sh ./gradlew test`를 사용합니다.
- 테스트를 실행하지 못했거나 실패했다면 원인과 미검증 범위를 명확히 보고합니다.
