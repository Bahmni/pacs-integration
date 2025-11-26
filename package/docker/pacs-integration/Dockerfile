FROM amazoncorretto:17

ENV SERVER_PORT=8054
ENV BASE_DIR=/var/run/pacs-integration
ENV CONTEXT_PATH=/pacs-integration
ENV WAR_DIRECTORY=/var/run/pacs-integration/pacs-integration
ENV SERVER_OPTS="-Xms512m -Xmx1024m"
ENV DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n"

# Used by envsubst command for replacing environment values at runtime
RUN yum install -y gettext nc
RUN yum install -y postgresql

COPY pacs-integration-webapp/target/pacs-integration.jar /opt/pacs-integration/lib/pacs-integration.jar

COPY package/docker/pacs-integration/templates/atomfeed.properties.template /etc/atomfeed.properties.template
COPY package/docker/pacs-integration/templates/application.properties.template /etc/application.properties.template
RUN curl -o wait-for.sh 'https://raw.githubusercontent.com/eficode/wait-for/v2.2.3/wait-for'

COPY package/docker/pacs-integration/scripts/start.sh start.sh
RUN chmod +x start.sh
RUN chmod +x wait-for.sh

CMD [ "./start.sh" ]
