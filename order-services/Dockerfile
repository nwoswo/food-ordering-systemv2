FROM amazoncorretto:17.0.16

WORKDIR /app

COPY build/libs/order-service-1.0.0.jar app.jar

EXPOSE 8181

ENTRYPOINT ["java", "-jar", "app.jar"]

