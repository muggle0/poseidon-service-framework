FROM openjdk:8-jdk-alpine
ADD ./target/app.jar /app/
ENTRYPOINT ["nohup","java", "-jar", "/app/app.jar","&"]