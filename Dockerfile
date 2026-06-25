# 1단계: 빌드 스테이지
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

# 빌드 캐시 활용을 위해 pom.xml 먼저 복사
COPY pom.xml .
RUN mvn dependency:go-offline

# 소스 복사 및 빌드
COPY src ./src
RUN mvn clean package -DskipTests

# 2단계: 실행 스테이지
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN apt-get update && \
    apt-get install -y --no-install-recommends ca-certificates openssl && \
    update-ca-certificates -f && \
    rm -rf /var/lib/apt/lists/*

# 1단계에서 생성된 JAR 파일 복사
COPY --from=builder /build/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]