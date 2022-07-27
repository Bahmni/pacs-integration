FROM openjdk:8-alpine

ENV SERVER_PORT=8054
ENV BASE_DIR=/var/run/pacs-integration
ENV CONTEXT_PATH=/pacs-integration
ENV WAR_DIRECTORY=/var/run/pacs-integration/pacs-integration
ENV SERVER_OPTS="-Xms512m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=512m"
ENV DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n"

# Used by envsubst command for replacing environment values at runtime
RUN apk add gettext curl

COPY package/resources/core-1.0-SNAPSHOT.jar /opt/pacs-integration/lib/pacs-integration.jar
COPY pacs-integration-webapp/target/pacs-integration.war /etc/pacs-integration/pacs-integration.war

RUN mkdir -p ${WAR_DIRECTORY}
RUN cd ${WAR_DIRECTORY} && jar xvf /etc/pacs-integration/pacs-integration.war

COPY package/docker/templates/atomfeed.properties.template /etc/atomfeed.properties.template
COPY package/docker/templates/application.properties.template /etc/application.properties.template
RUN curl -o wait-for.sh 'https://raw.githubusercontent.com/eficode/wait-for/v2.2.3/wait-for'

COPY package/docker/scripts/start.sh start.sh
RUN chmod +x start.sh
RUN chmod +x wait-for.sh

CMD [ "./start.sh" ]