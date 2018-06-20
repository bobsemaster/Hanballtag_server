FROM openjdk:8-jdk-alpine
COPY deploy/handballtag-service.jar .
CMD java -jar -Dspring.profiles.active=docker handballtag-service.jar

