FROM openjdk:21
EXPOSE 8080
COPY target/cinema-0.0.1-SNAPSHOT.jar cinema.jar
ENTRYPOINT ["java","-jar","/cinema.jar"]
