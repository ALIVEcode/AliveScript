FROM openjdk:16

COPY . /src/java

WORKDIR /src/java

RUN ["javac", "ServerAS/src/server.Server.java"]

ENTRYPOINT ["java", "server.Server"]


