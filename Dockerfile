FROM openjdk:8-jre 
MAINTAINER Kieran Wardle <kieran.wardle@ons.gov.uk>
ARG jar
VOLUME /tmp
COPY $jar casesvc.jar
RUN sh -c 'touch /casesvc.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java -jar /casesvc.jar" ]

