FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -pl theater-service -am -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build \
    /app/theater-service/target/*.jar \
    app.jar

EXPOSE 8084

ENTRYPOINT ["java","-jar","app.jar"]