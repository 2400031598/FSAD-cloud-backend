# =========================
# Stage 1: Build the JAR
# =========================
FROM maven:3.9.11-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build JAR file
RUN mvn clean package -DskipTests


# =========================
# Stage 2: Run the JAR
# =========================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run application
CMD ["java", "-jar", "app.jar"]