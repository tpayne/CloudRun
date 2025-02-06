# This is a docker make file for building this Maven app
# and putting it into a docker image...

FROM maven:3.9.9-eclipse-temurin-21-jammy AS imagebuilder
RUN apt-get update

WORKDIR /app
COPY FileSampleApp/src ./FileSampleApp/src
COPY FileSampleApp/pom.xml ./FileSampleApp/pom.xml
COPY pom.xml .

RUN cd FileSampleApp && mvn -q clean package

FROM eclipse-temurin:21.0.6_7-jre-jammy AS runner

ARG account=spring
RUN addgroup ${account} && adduser ${account} --ingroup ${account}

RUN mkdir /uploads
RUN chown -R ${account}:${account} /uploads
RUN chmod -R o+rwx /uploads

VOLUME /uploads

USER ${account}:${account}

ARG JAR_FILE=/app/FileSampleApp/target/samples.cloudrun-fileapp-1.0-SNAPSHOT.jar
COPY --from=imagebuilder ${JAR_FILE} /samples.cloudrun-fileapp-1.0-SNAPSHOT.jar

# Used to provide optional debugging for container...
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","samples.cloudrun-fileapp-1.0-SNAPSHOT.jar","samples.cloudrun.FileApp.FileApp"]
EXPOSE 8080
EXPOSE 80