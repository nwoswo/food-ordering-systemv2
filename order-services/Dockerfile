FROM amazoncorretto:17.0.16
WORKDIR /app
COPY target/order-container-1.0-SNAPSHOT.jar app.jar
COPY target/classes/db/ db/
COPY target/classes/application.yml application.yml
EXPOSE 8181
ENTRYPOINT ["java", "-jar", "app.jar"]
