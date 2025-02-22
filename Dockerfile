# Use the official OpenJDK 23 image
FROM openjdk:21-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/tennisCH-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 for the application
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
