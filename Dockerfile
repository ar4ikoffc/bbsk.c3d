FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

RUN apk add --no-cache bash

COPY back/gradlew back/settings.gradle.kts back/build.gradle.kts ./
COPY back/gradle ./gradle

RUN sed -i 's/\r$//' ./gradlew && chmod +x ./gradlew

RUN ./gradlew dependencies --no-daemon || true

COPY back/src ./src

RUN ./gradlew bootJar --no-daemon -x test

RUN find build/libs -name "*.jar" ! -name "*-plain.jar" -exec cp {} app.jar \;

FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /build/app.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
