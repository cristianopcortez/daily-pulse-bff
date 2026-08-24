FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /home/gradle/project
COPY . .
RUN chmod +x ./gradlew && ./gradlew buildFatJar --no-daemon -x test

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /home/gradle/project/build/libs/*-all.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-jar", "app.jar"]
