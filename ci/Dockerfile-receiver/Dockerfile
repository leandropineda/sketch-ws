FROM openjdk:8-jdk

ADD sketchws-1.0-SNAPSHOT.jar /data/sketchws-1.0-SNAPSHOT.jar
ADD config.yml /data/config.yml
RUN cat /data/config.yml

ENV JMX_HOST=receiver
ENV JMX_PORT=4000

CMD java \
  -Djava.rmi.server.hostname=$JMX_HOST \
  -Dcom.sun.management.jmxremote \
  -Dcom.sun.management.jmxremote.port=$JMX_PORT \
  -Dcom.sun.management.jmxremote.rmi.port=$JMX_PORT \
  -Dcom.sun.management.jmxremote.local.only=false \
  -Dcom.sun.management.jmxremote.authenticate=false \
  -Dcom.sun.management.jmxremote.ssl=false \
  -jar /data/sketchws-1.0-SNAPSHOT.jar server /data/config.yml

EXPOSE 8080
