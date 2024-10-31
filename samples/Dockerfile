# This is a docker make file for building this Maven app

FROM maven:3.9.9-eclipse-temurin-21-jammy AS imagebuilder
RUN apt-get update

WORKDIR /build
COPY pom.xml .
COPY FileSampleApp ./FileSampleApp
COPY GCESample ./GCESample
COPY SimpleSpringApp ./SimpleSpringApp
COPY StorageGCSSample ./StorageGCSSample

RUN mvn -q clean package 
