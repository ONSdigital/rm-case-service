FROM eclipse-temurin:17-jre-alpine

ARG JAR_FILE=case.jar

ENV JAVA_OPTS=""

COPY target/$JAR_FILE /opt/$JAR_FILE

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/case.jar" ]
