# Maven
FROM maven:3.8.5-amazoncorretto-17 AS builder
WORKDIR /so5
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# java
FROM amazoncorretto:17-al2-full
WORKDIR /so5
COPY --from=builder /so5/target/api-0.0.1-SNAPSHOT.jar api-0.0.1-SNAPSHOT.jar
RUN ["yum","-y","install","ImageMagick"]
EXPOSE 8080
ENTRYPOINT ["java","-jar","api-0.0.1-SNAPSHOT.jar","--spring.profiles.active=docker"]