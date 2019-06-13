FROM openjdk:8-jre-slim

ARG JAR_FILE=casesvc*.jar
RUN apt-get update && \
    apt-get -yq install curl

EXPOSE 8171
ENV JAVA_OPTS=""

HEALTHCHECK --interval=1m30s --timeout=10s --retries=3 \
  CMD curl -f http://localhost:8171/info || exit 1

RUN groupadd --gid 999 casesvc && \
    useradd --create-home --system --uid 999 --gid casesvc casesvc
USER casesvc

COPY target/$JAR_FILE /opt/casesvc.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/casesvc.jar" ]

