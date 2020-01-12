FROM anapsix/alpine-java

COPY target/trader-0.0.1-SNAPSHOT.jar /trader/server/trader.jar
COPY start-trader-server.sh /trader/server/start-trader-server.sh

EXPOSE 8080

ENTRYPOINT ["sh", "/trader/server/start-trader-server.sh"]