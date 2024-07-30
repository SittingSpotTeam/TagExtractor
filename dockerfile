FROM maven:3.9.1-amazoncorretto-17 AS build

ENV MAVEN_HOME /share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

COPY ./pom.xml /pom.xml
RUN mvn -f /pom.xml verify

COPY ./src /src

RUN mvn -f pom.xml -DskipTests clean package 
RUN mv target/tagextractor-*.jar /docker-image.jar

FROM amazoncorretto:17.0.7-alpine3.17

COPY src/main/java/com/sittingspot/tagextractor/controller/AnalyzeSentiment.py /AnalyzeSentiment.py
COPY --from=build /docker-image.jar /docker-image.jar

RUN sh -c 'touch /docker-image.jar' && apk update && apk add tzdata && apk add --no-cache python3 py3-pip
RUN pip3 install vaderSentiment

ENV JAVA_OPTS="-Xmx4g"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /docker-image.jar" ]