# ─────────────────────────────────────────────────────────────
# Very small runtime image (OpenJDK 17 slim, no Eclipse/Temurin branding)
# ─────────────────────────────────────────────────────────────
FROM openjdk:17-jdk-slim

WORKDIR /app

# copy the pre-built Spring Boot jar into the container
COPY target/authServiceApplication-0.0.1-SNAPSHOT.jar app.jar
#   ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
#   replace this with the exact jar name you get from Maven
#   e.g., authentication_service-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
