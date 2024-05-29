#
# Build stage
#
FROM eclipse-temurin:22-alpine AS build

ARG APP_NAME=DiscoveryServerApplication
LABEL authors="Maksym Myna"
LABEL github="@mmaksymko"

ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME
RUN --mount=type=cache,target=/root/.m2 ./mvnw -f $HOME/pom.xml clean package

#
# Package stage
#
FROM eclipse-temurin:22-alpine
ARG JAR_FILE=/usr/app/target/*.jar
COPY --from=build $JAR_FILE /app/${APP_NAME}.jar
EXPOSE 8080
ENTRYPOINT java -jar /app/${APP_NAME}.jar