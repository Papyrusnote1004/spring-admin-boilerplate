FROM eclipse-temurin:11-jdk-alpine AS build
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY src src
RUN chmod +x gradlew && ./gradlew bootJar -x test --no-daemon

FROM eclipse-temurin:11-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/spring-admin-boilerplate.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
