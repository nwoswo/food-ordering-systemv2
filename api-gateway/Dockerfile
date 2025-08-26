FROM amazoncorretto:17.0.16

WORKDIR /app

COPY api-gateway-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
