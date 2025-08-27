FROM amazoncorretto:17.0.16

WORKDIR /app

COPY build/libs/payment-service-1.0.0.jar app.jar

EXPOSE 8182

ENTRYPOINT ["java", "-jar", "app.jar"]



