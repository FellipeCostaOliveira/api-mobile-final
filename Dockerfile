# --- build stage ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn -B clean package -DskipTests

# --- run stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/clyvovet.jar app.jar
EXPOSE 8080
# Flags de memoria ajustadas para o plano Free do Render (512 MB no container inteiro):
# - UseSerialGC: coletor de lixo mais simples, com bem menos overhead de memoria
#   que o G1 (padrao), ideal para 1 core/0.1 vCPU.
# - MaxRAMPercentage=70: limita o heap a ~70% da memoria do container, deixando
#   espaco pra Metaspace, threads e as libs nativas do Firebase Admin SDK (gRPC/Netty).
# - MaxMetaspaceSize: evita que o carregamento de classes (Spring+Hibernate+Firebase)
#   cresça sem limite.
ENTRYPOINT ["java", \
    "-XX:+UseSerialGC", \
    "-XX:MaxRAMPercentage=70.0", \
    "-XX:MaxMetaspaceSize=160m", \
    "-Xss512k", \
    "-jar", "app.jar"]
