FROM amazoncorretto:17.0.16
WORKDIR /app
ARG JAR_FILE
COPY target/${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
