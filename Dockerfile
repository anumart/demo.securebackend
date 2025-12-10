# Stage 1: Build the application
FROM maven:3.8-openjdk-17-slim AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies first (for better caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code
COPY src src

# Package the application (skip tests for faster builds in Docker)
RUN ./mvnw clean package

FROM openjdk:21-ea-21-slim
# กำหนด working directory
WORKDIR /app
COPY ./target/demo.securebackend-0.0.1-SNAPSHOT.jar app.jar
# คัดลอก application.yaml ที่ต้องการแยกออกมา (สมมติว่าอยู่ใน /src/main/resources)
COPY src/main/resources/application.yaml /app/config/application.yaml
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

#Create network
#docker network create my-network-1

#For db
#docker run --name mysql-container -p 3305:3306 --network my-network-1 -v D:/workspace/mysql-data:/var/lib/mysql -v D:/workspace/Demo/init-db:/docker-entrypoint-initdb.d -e MYSQL_ROOT_PASSWORD=P@ssw0rd -d mysql:latest

#docker build -t auth-api .
#docker run --name auth-api-container -d -p 8080:8080 --network my-network-1 -e SPRING_DATASOURCE_URL="jdbc:mysql://mysql-container:3306/secure_db" -e SPRING_DATASOURCE_USERNAME="root" -e SPRING_DATASOURCE_PASSWORD="P@ssw0rd" auth-api rm


#docker run --name auth-api-container -d -p 8080:8080 --network my-network-1 -e SPRING_DATASOURCE_URL="jdbc:mysql://mysql-container:3306/secure_db" -e SPRING_DATASOURCE_USERNAME="root" -e SPRING_DATASOURCE_PASSWORD="P@ssw0rd" auth-api  rm

#docker run -d -p 8080:8080 --name auth-api-container auth-api --network my-network-1 -e SPRING_DATASOURCE_URL="jdbc:mysql://mysql-container:3306/secure_db" -e SPRING_DATASOURCE_USERNAME="root" -e SPRING_DATASOURCE_PASSWORD="P@ssw0rd" rm

#docker run --name mysql-container -p 3306:3306 --network my-network-1 -v /home/ubuntu/mysql-data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=P@ssw0rd -d mysql:8.0