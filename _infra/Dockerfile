FROM openjdk:8-jre-slim

RUN groupadd --gid 999 casesvc && \
    useradd --create-home --system --uid 999 --gid casesvc casesvc
USER casesvc

COPY casesvc /opt/casesvc.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/casesvc.jar" ]
