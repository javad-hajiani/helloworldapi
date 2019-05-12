FROM maven:3.6.1-jdk-8-alpine AS build
COPY src /app/src
COPY pom.xml /app
RUN mvn -f /app/pom.xml clean package

FROM openjdk:8u212-jre-alpine3.9
ENV USER_DATA_DIR="/opt/"
COPY --from=build /app/target/hello-world-api-1.0-SNAPSHOT-jar-with-dependencies.jar /app/hello-world.jar
EXPOSE 4567
ENTRYPOINT ["java","-jar","/app/hello-world.jar"]