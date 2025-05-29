FROM maven AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:24
WORKDIR /app
COPY --from=build /app/target/*.jar /app.jar
COPY --from=build /app/target/classes/application.properties application.properties
ENTRYPOINT ["java","-jar","/app.jar"]

EXPOSE 8080

# Enable debuggin
EXPOSE 8000