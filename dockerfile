FROM openjdk:8
EXPOSE 8080
ADD target/Admin-WS.jar Admin-WS.jar
ENTRYPOINT ["java","-jar","/Admin-WS.jar"]