FROM openjdk:17-jdk-slim AS build
WORKDIR /
COPY pom.xml .
COPY mvnw ./
RUN chmod +x mvnw
COPY .mvn/wrapper .mvn/wrapper
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw clean install -DskipTests
EXPOSE 8080

FROM openjdk:17-jdk-slim
WORKDIR /
COPY --from=build target/appointments-app-0.0.1-SNAPSHOT.jar appointments-app.jar
ENTRYPOINT ["java", "-jar", "appointments-app.jar"]