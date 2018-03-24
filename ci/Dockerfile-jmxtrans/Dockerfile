FROM openjdk:8-jdk

RUN echo 'debconf debconf/frontend select Noninteractive' | debconf-set-selections
RUN wget http://central.maven.org/maven2/org/jmxtrans/jmxtrans/259/jmxtrans-259.deb
RUN dpkg -i jmxtrans-259.deb

COPY run-jmxtrans.sh /run-jmxtrans.sh
RUN chmod 755 /run-jmxtrans.sh

CMD /run-jmxtrans.sh
