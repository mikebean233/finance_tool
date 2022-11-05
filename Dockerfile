FROM openjdk:17-oracle

EXPOSE 8080

RUN mkdir /app

COPY build/libs/*.jar /app/finance-tool.jar

ENTRYPOINT ["java","-jar","/app/finance-tool.jar"]