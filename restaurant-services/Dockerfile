FROM amazoncorretto:17.0.16

WORKDIR /app

COPY build/libs/restaurant-service-1.0.0.jar app.jar

EXPOSE 8183

ENTRYPOINT ["java", "-jar", "app.jar"]

