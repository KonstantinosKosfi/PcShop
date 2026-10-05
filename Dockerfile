# ---------- Frontend build ----------
FROM node:22-alpine AS frontend

WORKDIR /frontend

COPY src/main/client/pcShop/package*.json ./

RUN npm ci

COPY src/main/client/pcShop/ ./

RUN npm run build


# ---------- Backend build ----------
FROM eclipse-temurin:21-jdk AS backend

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY src src

RUN chmod +x gradlew

RUN rm -rf src/main/resources/static \
    && mkdir -p src/main/resources/static

COPY --from=frontend /frontend/dist/ src/main/resources/static/

RUN ./gradlew clean bootJar -x test


# ---------- Runtime ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=backend /app/build/libs/PcShop-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]