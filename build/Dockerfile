# Stage 1: Build the application
FROM maven:3.9.8 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and download the dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM openjdk:21-jdk-bookworm

WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar /app/pos-connector.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/pos-connector.jar"]
