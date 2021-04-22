# This is a docker make file for building this Maven app
# and putting it into a docker image...

FROM maven:3.8-jdk-11 as imagebuilder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package 

FROM adoptopenjdk/openjdk11:alpine-slim
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ARG JAR_FILE=/app/target/samples.cloudrun-gcesample-1.0-SNAPSHOT.jar
COPY --from=imagebuilder ${JAR_FILE} /samples.cloudrun-gcesample-1.0-SNAPSHOT.jar

CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","samples.cloudrun-gcesample-1.0-SNAPSHOT.jar","samples.cloudrun.GCESample.GCESample"]
EXPOSE 8080
EXPOSE 80