FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

COPY gradlew gradlew
COPY gradle gradle
COPY settings.gradle.kts settings.gradle.kts
COPY app/build.gradle.kts app/build.gradle.kts
COPY gradle.properties gradle.properties

RUN ./gradlew dependencies --no-daemon -q || true

COPY app/src app/src

RUN ./gradlew :app:clean :app:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /build/app/build/libs/*.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "app.jar"]