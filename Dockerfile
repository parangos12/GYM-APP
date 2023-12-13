FROM openjdk:17
LABEL description="This is the trainee Microservice using Springboot"
WORKDIR /usr/src/myapp/
COPY ./target/*.jar app.jar
EXPOSE 80
ENTRYPOINT ["java","-jar","app.jar"]

