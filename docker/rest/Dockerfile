FROM openjdk:11-slim
COPY deploy/handballtag-service.jar .
CMD java -jar -Dspring.profiles.active=docker handballtag-service.jar
