FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -pl event-service -am -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build \
    /app/event-service/target/*.jar \
    app.jar

EXPOSE 8082

ENTRYPOINT ["java","-jar","app.jar"]