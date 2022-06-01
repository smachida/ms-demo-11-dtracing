FROM openjdk:12.0.2

EXPOSE 9999

ADD ./build/libs/authorization-server-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]