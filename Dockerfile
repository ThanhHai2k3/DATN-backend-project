# ---------- Build stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY . .

ARG SERVICE_PATH
RUN mvn -DskipTests -pl ${SERVICE_PATH} -am clean package

RUN sh -c 'JAR=$(ls -1 ${SERVICE_PATH}/target/*.jar | grep -vE "(sources|javadoc)\\.jar$" | head -n 1) && cp "$JAR" /workspace/app.jar'

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/app.jar /app/app.jar

ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
