FROM maven:3.8-openjdk-18-slim

ENV HOME=/home/app
RUN mkdir -p $HOME

ADD ./settings.xml /usr/share/maven/ref
ENV MVN_SETTINGS=/usr/share/maven/ref/settings.xml

RUN mkdir -p $HOME/common
ADD ./common $HOME/common
RUN mvn -f $HOME/common/pom.xml -s $MVN_SETTINGS clean package
RUN mvn org.apache.maven.plugins:maven-install-plugin:3.1.2:install-file -Dfile=$HOME/common/target/common-0.0.1.jar --debug

RUN mkdir -p $HOME/openai
ADD ./openai/pom.xml $HOME/openai
ADD ./openai/src $HOME/openai/src
RUN mvn -f $HOME/openai/pom.xml -s $MVN_SETTINGS clean package -DskipTests --debug

CMD java -jar $HOME/openai/target/openai-0.0.1.jar