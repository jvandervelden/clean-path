FROM maven:3-jdk-8-alpine AS jarBuild

COPY ./src/ /opt/cleanPath/src/
COPY ./pom.xml /opt/cleanPath/pom.xml

WORKDIR /opt/cleanPath

RUN mvn clean install

FROM java:8-jdk-alpine

COPY --from=jarBuild /opt/cleanPath/target/cleanPath.service-0.0.1-SNAPSHOT.jar /opt/cleanPath/

EXPOSE 8080

ENTRYPOINT [ "java" ]
CMD [ "-jar", "/opt/cleanPath/cleanPath.service-0.0.1-SNAPSHOT.jar" ]