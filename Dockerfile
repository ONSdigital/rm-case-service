FROM openjdk:8-jre-slim

VOLUME /tmp
ARG JAR_FILE=casesvc*.jar
RUN apt-get update
RUN apt-get -yq clean

RUN groupadd --gid 999 casesvc && \
    useradd --create-home --system --uid 999 --gid casesvc casesvc
USER casesvc

COPY target/$JAR_FILE /opt/casesvc.jar

ENTRYPOINT [ "java", "-jar", "/opt/casesvc.jar" ]
