# This is a docker make file for building this Maven app
# and putting it into a docker image...

FROM maven:3.8-jdk-11 as imagebuilder
RUN apt-get update

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn -q package

FROM adoptopenjdk/openjdk11:alpine-slim
RUN apk add --upgrade apk-tools && apk upgrade --available

ARG account=spring
RUN addgroup -S ${account} && adduser -S ${account} -G ${account}

RUN mkdir /uploads
RUN chown -R ${account}:${account} /uploads
RUN chmod -R o+rwx /uploads

VOLUME /uploads

USER ${account}:${account}

ARG JAR_FILE=/app/target/samples.cloudrun-fileapp-1.0-SNAPSHOT.jar
COPY --from=imagebuilder ${JAR_FILE} /samples.cloudrun-fileapp-1.0-SNAPSHOT.jar

# Used to provide optional debugging for container...
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","samples.cloudrun-fileapp-1.0-SNAPSHOT.jar","samples.cloudrun.FileApp.FileApp"]
EXPOSE 8080
EXPOSE 80