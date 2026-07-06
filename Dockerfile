# Etapa 1: Construcción (Build)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Corregir BOM y saltos de línea
RUN apk add --no-cache dos2unix \
    && find src -name "*.java" -type f -exec sed -i '1s/^\xEF\xBB\xBF//' {} + \
    && find src -name "*.java" -type f -exec dos2unix {} +

# Añadir opciones para evitar errores SSL y usar un mirror rápido
RUN mvn clean package -DskipTests \
    -Dmaven.wagon.http.ssl.insecure=true \
    -Dmaven.wagon.http.ssl.allowall=true \
    -Dmaven.wagon.http.ssl.ignore.validity.dates=true \
    -Dmaven.repo.remote=https://repo.maven.apache.org/maven2/

# Etapa 2: Ejecución
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]