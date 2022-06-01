FROM amazoncorretto:17-al2-full
WORKDIR /so5
COPY target/api-0.0.1-SNAPSHOT.jar api-0.0.1-SNAPSHOT.jar
RUN ["yum","-y","install","ImageMagick"]

ENTRYPOINT ["java","-jar","api-0.0.1-SNAPSHOT.jar","--spring.profiles.active=docker"]