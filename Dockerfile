ARG JAR_FILE=casesvc*.jar
FROM openjdk:8-jre

ARG JAR_FILE
COPY target/$JAR_FILE /opt/casesvc.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/casesvc.jar" ]

