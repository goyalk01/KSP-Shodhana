# Stage 1: Build JAR package
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Production JRE Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=builder /app/target/shodhana-*.jar app.jar

EXPOSE 8080
ENV PORT 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
