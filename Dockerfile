FROM --platform=linux/amd64 openjdk:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/app.jar"]
ARG DEBIAN_FRONTEND=noninteractive
ENV TZ=Asia/Seoul