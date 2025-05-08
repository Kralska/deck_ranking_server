FROM eclipse-temurin:24
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY target/classes/application.properties application.properties
ENTRYPOINT ["java","-jar","/app.jar"]

EXPOSE 8080