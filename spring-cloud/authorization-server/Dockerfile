FROM openjdk:11.0

EXPOSE 9999

ADD ./build/libs/authorization-server-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
