# 基于带 FFmpeg 的 JDK 镜像
FROM eclipse-temurin:21-jre-alpine

# 安装 FFmpeg
RUN apk add --no-cache ffmpeg

# 工作目录
WORKDIR /app

# 复制 jar 包
COPY target/*.jar app.jar

# 暴露端口
EXPOSE 8084

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
