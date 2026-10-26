# ==========================================
# Stage 1: Build the application with Gradle
# ==========================================
FROM eclipse-temurin:23-jdk AS builder

WORKDIR /app

# Copy gradle wrapper and definition files first to leverage Docker layer caching
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Fix line endings and permissions for gradlew in case created on Windows
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Download dependencies (fail-safe cache layer)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code and build the executable jar without running tests
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

# ==========================================
# Stage 2: Minimal Production Runtime
# ==========================================
FROM eclipse-temurin:23-jre

# Create non-root system user for security
RUN groupadd -r spring && useradd -r -g spring spring

WORKDIR /app

# Copy built artifact from builder stage
COPY --from=builder /app/build/libs/URLShortnerApplication-0.0.1-SNAPSHOT.jar app.jar

# Set ownership to non-root user
RUN chown spring:spring app.jar

USER spring:spring

# Expose the application port configured in application.properties
EXPOSE 8081

# JVM flags optimized for containerized environments
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
