# Stage 1: to build the application
FROM eclipse-temurin:25-jdk-jammy AS builder

# Set the working directory inside the container
WORKDIR /app

# Install the Apache Maven build tool
RUN apt-get update && apt-get install -y --no-install-recommends maven && rm -rf /var/lib/apt/lists/*

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2 is to build a production ready image and run
FROM eclipse-temurin:25-jre-jammy

WORKDIR /app

# copy the final executable JAR file fro the 'builder' stage's target directory
COPY --from=builder /app/target/*.jar app.jar

# backend port
EXPOSE 8090

# define the command to run the application when the container start
ENTRYPOINT ["java", "-jar", "app.jar"]