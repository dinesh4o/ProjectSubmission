## Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
# When Render uses repo root as Docker build context, source lives under ./backend
COPY backend/pom.xml ./pom.xml
# Go offline to cache deps
RUN mvn -q -DskipTests dependency:go-offline
COPY backend/src ./src
RUN mvn -q -DskipTests package

## Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /workspace/target/project-submission.jar app.jar
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["java", "-jar", "app.jar"]


