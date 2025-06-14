FROM maven AS build
WORKDIR /app

# Set up dependencies
COPY pom.xml .
RUN mvn dependency:go-offline
RUN mvn clean verify --fail-never

# Copy source files
COPY src ./src

RUN mvn package -DskipTests

FROM eclipse-temurin:24
WORKDIR /app
COPY --from=build /app/target/*.jar /app.jar
COPY --from=build /app/target/classes/application.properties application.properties
ENTRYPOINT ["java","-jar","/app.jar"]

EXPOSE 8080

# Enable debugging
EXPOSE 8000