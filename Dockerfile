FROM maven:3.9.8-eclipse-temurin-21 as build
WORKDIR ./app
COPY . .
RUN mvn clean package -DskipTests=true

FROM alpine:3.19

RUN adduser -D smarthome

RUN apk add openjdk21

WORKDIR /run
COPY --from=build /app/target/smarthome-server-0.0.1-SNAPSHOT.jar /run/smarthome-server-0.0.1-SNAPSHOT.jar
RUN chown -R smarthome:smarthome /run

USER smarthome

EXPOSE 8080

ENV JAVA_OPTIONS="-Xmx2048m -Xms256m"

ENTRYPOINT java -jar /run/smarthome-server-0.0.1-SNAPSHOT.jar