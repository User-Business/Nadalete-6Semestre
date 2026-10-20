FROM maven:3.9.11-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml lombok.config ./
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
RUN addgroup -S atlaz && adduser -S atlaz -G atlaz
COPY --from=build /app/target/AtlazDB-0.0.1-SNAPSHOT.jar app.jar
USER atlaz

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
