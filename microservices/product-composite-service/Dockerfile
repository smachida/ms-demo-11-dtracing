FROM openjdk:17.0.2

EXPOSE 8080
ADD ./build/libs/product-composite-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

