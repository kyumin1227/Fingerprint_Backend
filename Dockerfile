FROM openjdk:17-jdk-slim

# 실행 시 사용할 환경 변수 세팅
ENV GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}
ENV DB_USER=${DB_USER}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV DB_URL=${DB_URL}
ENV JWT_SECRET=${JWT_SECRET}
ENV JWT_EXPIRATION=${JWT_EXPIRATION}
ENV LINE_ACCESS_TOKEN=${LINE_ACCESS_TOKEN}

# 도커에 /app 폴더를 생성
WORKDIR /app

# 자바 빌드 파일을 app.jar로 도커에 복사
COPY /build/libs/fingerprint_backend-0.0.1-SNAPSHOT.jar app.jar

# 포트 지정
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
