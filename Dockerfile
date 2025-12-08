FROM openjdk:21-ea-21-slim
# กำหนด working directory
#WORKDIR /app
COPY ./target/demo.securebackend-0.0.1-SNAPSHOT.jar app.jar
# คัดลอก application.yaml ที่ต้องการแยกออกมา (สมมติว่าอยู่ใน /src/main/resources)
COPY src/main/resources/application.yaml /app/config/application.yaml
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

#docker build -t auth-api .
#docker run -p 8080:8080 -d --name auth-api-container auth-api -rm