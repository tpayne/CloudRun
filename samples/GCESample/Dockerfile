# This is a docker make file for building this Maven app
# and putting it into a docker image...

FROM maven:3.9.9-eclipse-temurin-21-jammy AS imagebuilder
RUN apt-get update

WORKDIR /app
COPY GCESample/src ./GCESample/src
COPY GCESample/pom.xml ./GCESample/pom.xml
COPY pom.xml .

RUN cd GCESample && mvn -q clean package

FROM eclipse-temurin:21.0.6_7-jre-jammy AS runner

ARG account=spring
RUN addgroup ${account} && adduser ${account} --ingroup ${account}

RUN mkdir /uploads
RUN chown -R ${account}:${account} /uploads
RUN chmod -R o+rwx /uploads

VOLUME /uploads

USER ${account}:${account}

ARG JAR_FILE=/app/GCESample/target/samples.cloudrun-gcesample-1.0-SNAPSHOT.jar
COPY --from=imagebuilder ${JAR_FILE} /samples.cloudrun-gcesample-1.0-SNAPSHOT.jar

# Used to provide optional debugging for container...
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","samples.cloudrun-gcesample-1.0-SNAPSHOT.jar","samples.cloudrun.GCESample.GCESample"]
EXPOSE 8080
EXPOSE 80