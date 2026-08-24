FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY backend/pom.xml backend/pom.xml
COPY backend/.mvn backend/.mvn
COPY backend/mvnw backend/mvnw
COPY backend/src backend/src

WORKDIR /app/backend

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/library-backend-0.0.1-SNAPSHOT.jar"]