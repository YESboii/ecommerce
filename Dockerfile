FROM eclipse-temurin:21.0.2_13-jdk-jammy as build_stage
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21.0.2_13-jre-jammy

WORKDIR /app

COPY --from=build_stage /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]