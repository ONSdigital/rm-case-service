FROM openjdk:8-jre-slim

ARG JAR_FILE=case.jar
RUN apt-get update

ENV JAVA_OPTS=""

COPY target/$JAR_FILE /opt/$JAR_FILE

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/case.jar" ]

