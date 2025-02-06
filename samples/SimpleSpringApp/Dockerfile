# This is a docker make file for building this Maven app
# and putting it into a docker image...

FROM maven:3.9.9-eclipse-temurin-21-jammy AS imagebuilder
RUN apt-get update

WORKDIR /app
COPY SimpleSpringApp/src ./SimpleSpringApp/src
COPY SimpleSpringApp/pom.xml ./SimpleSpringApp/pom.xml
COPY pom.xml .

RUN cd SimpleSpringApp && mvn -q clean package

FROM eclipse-temurin:21.0.6_7-jre-jammy AS runner

ARG account=spring
RUN addgroup ${account} && adduser ${account} --ingroup ${account}

RUN mkdir /uploads
RUN chown -R ${account}:${account} /uploads
RUN chmod -R o+rwx /uploads

VOLUME /uploads

USER ${account}:${account}

ARG JAR_FILE=/app/SimpleSpringApp/target/samples.cloudrun-web8k-example-1.0-SNAPSHOT.jar
COPY --from=imagebuilder ${JAR_FILE} /samples.cloudrun-web8k-example-1.0-SNAPSHOT.jar

CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","samples.cloudrun-web8k-example-1.0-SNAPSHOT.jar","application.Webk8App"]
EXPOSE 80
EXPOSE 8080