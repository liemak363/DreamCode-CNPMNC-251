# Sử dụng JDK image
FROM eclipse-temurin:17-jdk-alpine

# Đặt thư mục làm việc
WORKDIR /app

# Copy file jar
COPY target/*.jar app.jar

# Expose port (Railway sẽ override)
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
