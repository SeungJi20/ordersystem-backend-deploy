FROM openjdk:17-jdk-alpine as stage1

WORKDIR /app

# build 폴더는 필요없음 -> ec2에서 빌드할거라서
COPY gradle gradle
COPY src src
COPY build.gradle .
COPY gradlew .
RUN chmod +x gradlew
RUN ./gradlew bootJar

# 두번째 스테이지(FROM이 나오면 자동으로 2stage가 열리는것임)
# 이미지 경량화를 위해 스테이지 분리
FROM openjdk:17-jdk-alpine
WORKDIR /app
# stage1의 jar파일을 stage2로 copy
COPY --from=stage1 /app/build/libs/*.jar app.jar


# 실행 : CMD 또는 ENTRYPOINT를 통해 컨테이너 실행
# ENTRYPOINT ["java", "-jar", "/app/build/libs/xxx.jar"]
ENTRYPOINT ["java", "-jar", "app.jar"]

# jar만 필요하면 jar만 이미지로 만들면 되지 않을까?
# => 그래서 2stage 빌드 라는 게 등장

# 도커 이미지 빌드
# docker build -t ordersystem:v1.0 .
# 위치가 다를 경우 위치지정까지 해주는 밑에 코드로 해야 함
# docker build -t ordersystem:v1.0 -f 도커파일위치 빌드컨텍스트위치
# 도커 컨테이너 실행. 8080:xxx 내부 포트(xxx)는 application.yml에 있음(server.port)
# docker 내부에서 localhost를 찾는 설정은 루프백 문제 발생
# docker run --name my-ordersystem -d -p 8080:8080 ordersystem:v1.0 -> 루프백 문제 발생
# 도커컨테이너 실행시점에 docker.host.internal을 환경변수로 주입
# docker run --name my-ordersystem -d -p 8080:8080 -e SPRING_REDIS_HOST=host.docker.internal -e SPRING_DATASOURCE_URL=jdbc:mariadb://host.docker.internal:3307/ordersystem ordersystem:v1.0


