FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /workspace

COPY gradle gradle
COPY gradlew build.gradle settings.gradle ./
COPY src src

RUN chmod +x gradlew && ./gradlew bootWar --no-daemon

FROM eclipse-temurin:17-jre-jammy

RUN groupadd --system spring && useradd --system --gid spring spring

WORKDIR /app

COPY --from=builder /workspace/build/libs/*.war app.war

ENV SPRING_PROFILES_ACTIVE=prd

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.war"]
