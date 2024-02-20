FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY build/libs/cake-manager-api-0.0.1-SNAPSHOT.jar cakes-api.jar

EXPOSE 8080

CMD ["java", "-jar", "cakes-api.jar"]
