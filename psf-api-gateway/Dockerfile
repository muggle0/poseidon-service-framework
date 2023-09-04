FROM openjdk:8-jdk-alpine
ADD ./target/app.jar /app/
CMD ["java", "-version"]
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
