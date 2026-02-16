FROM eclipse-temurin:25-jdk-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app
VOLUME /tmp

COPY applications/app-service/build/libs/*.jar /app/FranquiciasApi.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

USER appuser

ENTRYPOINT ["/bin/sh", "-c", "java $JAVA_OPTS -jar /app/FranquiciasApi.jar"]