FROM node:18 AS frontend-build

WORKDIR /app/frontend

COPY frontend/package*.json ./
RUN npm install

COPY frontend/ ./
RUN npm run build

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY backend/pom.xml backend/pom.xml
COPY backend/.mvn backend/.mvn
COPY backend/mvnw backend/mvnw
COPY backend/src backend/src

COPY --from=frontend-build /app/frontend/dist/ backend/src/main/resources/static/

WORKDIR /app/backend

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/library-backend-0.0.1-SNAPSHOT.jar"]