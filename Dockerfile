# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy the pom.xml and local.env file
COPY pom.xml .
COPY local.env .

# Export environment variables from local.env
RUN export $(cat local.env | xargs) 
RUN mvn clean install

# Copy the source code
COPY src ./src

# Stage 2: Run the application
FROM eclipse-temurin:21-alpine
WORKDIR /app

# Copy the built application from the first stage
COPY --from=build /app/target/Lexilearn-0.0.1-SNAPSHOT.jar ./application.jar

# Expose the application port
EXPOSE 5001

# Run the application
CMD ["java", "-jar", "application.jar"]
