FROM adoptopenjdk/openjdk11-openj9:jre-11.0.12_7_openj9-0.27.0-alpine

EXPOSE 8080:8080
RUN mkdir /app
COPY ./build/libs/*.jar /app/app.jar
WORKDIR /app
ENTRYPOINT ["java","-jar","/app.jar"]
