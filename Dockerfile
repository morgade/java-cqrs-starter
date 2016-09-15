FROM java:8-jdk

ENV JAVA_HOME              /usr/lib/jvm/java-8-openjdk-amd64
ENV JAVA_OPTS              ""
ENV PATH                   $PATH:$JAVA_HOME/bin

ENV SPRING_PROFILES_ACTIVE test

RUN echo "$TIME_ZONE" > /etc/timezone
RUN dpkg-reconfigure -f noninteractive tzdata

WORKDIR /app

EXPOSE 8080

COPY cqrs-starter-1.0.jar /app/cqrs-starter-1.0.jar

CMD ["/bin/sh", "-c", "java -jar /app/cqrs-starter-1.0.jar"]