FROM openjdk:latest
ADD target/gpsApiData-0.0.1-SNAPSHOT.jar gpsApiData
ENTRYPOINT ["java", "-jar", "gpsApiData"]
EXPOSE 3000
