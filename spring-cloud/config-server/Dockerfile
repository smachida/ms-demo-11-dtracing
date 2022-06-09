FROM openjdk:17.0.2

EXPOSE 8080

ADD ./build/libs/config-server-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
