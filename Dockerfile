FROM java:8-jre
MAINTAINER Max Syachin <maxsyachin@gmail.com>

ADD ./target/botling.jar /app/

CMD ["java", "-jar", "/app/botling.jar"]

EXPOSE 8080