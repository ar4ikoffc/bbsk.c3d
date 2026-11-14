# ==============================================================================
# Сборочный образ: компиляция исходного кода и сборка JAR-архива
# ==============================================================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Установка необходимых утилит для выполнения скриптов сборки
RUN apk add --no-cache bash

# Копирование конфигурационных файлов Gradle и обертки
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle

# Нормализация символов перевода строк и выдача прав на выполнение
RUN sed -i 's/\r$//' ./gradlew && chmod +x ./gradlew

# Предварительная загрузка зависимостей для эффективного кеширования слоев
RUN ./gradlew dependencies --no-daemon || true

# Копирование исходного кода приложения
COPY src ./src

# Компиляция и сборка исполняемого Spring Boot JAR-файла без прогона тестов
RUN ./gradlew bootJar --no-daemon -x test

# Поиск целевого исполняемого файла и сохранение под фиксированным именем
RUN find build/libs -name "*.jar" ! -name "*-plain.jar" -exec cp {} app.jar \;

# ==============================================================================
# Финальный образ: запуск приложения в легковесной среде JRE
# ==============================================================================
FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app

# Создание системной группы и непривилегированного пользователя для безопасности
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Копирование скомпилированного артефакта из сборочного образа
COPY --from=builder /build/app.jar app.jar

# Назначение владельца директории приложения
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
