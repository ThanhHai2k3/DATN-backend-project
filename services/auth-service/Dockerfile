# ---------- Build stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Multi-module => phải copy từ root để Maven thấy parent pom + modules
COPY . .

# Build riêng module auth-service
RUN mvn -DskipTests -pl services/auth-service -am clean package

# Copy jar ra đường dẫn cố định
RUN sh -c 'JAR=$(ls -1 services/auth-service/target/*.jar | grep -vE "(sources|javadoc)\\.jar$" | head -n 1) && cp "$JAR" /workspace/app.jar'

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

ENV JAVA_OPTS=""
COPY --from=build /workspace/app.jar /app/app.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
