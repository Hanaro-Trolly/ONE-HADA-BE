# Java 17 이미지를 기반으로 설정 (Spring Boot는 Java 기반이므로)
FROM openjdk:17-jdk-slim

# JAR 파일을 컨테이너에 복사할 경로 설정
WORKDIR /app

# Gradle을 사용하여 빌드된 JAR 파일을 컨테이너로 복사
COPY build/libs/*.jar app.jar

# Spring Boot 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]
