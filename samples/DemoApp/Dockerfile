# This is a docker make file for building this Maven app
# and putting it into a docker image...

FROM maven:3.9.9-eclipse-temurin-21-jammy AS imagebuilder
RUN apt-get update

WORKDIR /app
COPY src ./src
COPY pom.xml .

RUN mvn -q clean package

FROM eclipse-temurin:21.0.5_11-jre-jammy AS runner

ARG account=spring
RUN addgroup ${account} && adduser ${account} --ingroup ${account}

RUN mkdir /uploads
RUN chown -R ${account}:${account} /uploads
RUN chmod -R o+rwx /uploads

VOLUME /uploads

USER ${account}:${account}

ARG JAR_FILE=/app/target/cloudrun-demoapp-1.0-SNAPSHOT.jar
COPY --from=imagebuilder ${JAR_FILE} /cloudrun-demoapp-1.0-SNAPSHOT.jar

CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","cloudrun-demoapp-1.0-SNAPSHOT.jar","application.DemoApp"]
EXPOSE 80
EXPOSE 8080