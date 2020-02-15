FROM openjdk:8-jre-slim

COPY casesvc.jar /opt/casesvc.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/casesvc.jar" ]
