FROM openjdk:17-oracle
ARG JAR_FILE
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]