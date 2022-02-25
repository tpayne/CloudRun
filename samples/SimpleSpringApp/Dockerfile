# This is a docker make file for building this Maven app
# and putting it into a docker image...

FROM maven:3.8-jdk-11 as imagebuilder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package 

FROM adoptopenjdk/openjdk11:alpine-slim
ARG account=spring
RUN addgroup -S ${account} && adduser -S ${account} -G ${account}
USER ${account}:${account}

ARG JAR_FILE=/app/target/web8k-example-1.0-SNAPSHOT.jar
COPY --from=imagebuilder ${JAR_FILE} /web8k-example-1.0-SNAPSHOT.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","web8k-example-1.0-SNAPSHOT.jar","application.Webk8App"]
EXPOSE 8080
EXPOSE 80