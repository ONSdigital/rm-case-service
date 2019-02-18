FROM openjdk:8-jre-slim

VOLUME /tmp
ARG JAR_FILE=casesvc*.jar
RUN apt-get update
RUN apt-get -yq clean

RUN groupadd -g 999 casesvc && \
    useradd -r -u 999 -g casesvc casesvc
USER casesvc

COPY target/$JAR_FILE /opt/casesvc.jar

ENTRYPOINT [ "java", "-jar", "/opt/casesvc.jar" ]
