FROM amazoncorretto:17.0.16
WORKDIR /app
ARG JAR_FILE
COPY build/libs/${JAR_FILE} app.jar
EXPOSE 8185
ENTRYPOINT ["java", "-jar", "app.jar"]
