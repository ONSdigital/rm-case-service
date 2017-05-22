FROM openjdk 
ARG jar
VOLUME /tmp
ADD $jar casesvc.jar
RUN sh -c 'touch /casesvc.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java -jar /casesvc.jar" ]

