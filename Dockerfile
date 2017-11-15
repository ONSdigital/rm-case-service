FROM openjdk:8-jre

COPY target/casesvc*.jar /opt/casesvc.jar

ENTRYPOINT [ "java", "-jar", "/opt/casesvc.jar" ]

