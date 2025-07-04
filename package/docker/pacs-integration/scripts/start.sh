#!/bin/sh
set -e

echo "[INFO] Substituting Environment Variables"
#envsubst < /etc/atomfeed.properties.template > "${WAR_DIRECTORY}"/WEB-INF/classes/atomfeed.properties
#envsubst < /etc/application.properties.template > "${WAR_DIRECTORY}"/WEB-INF/classes/application.properties
envsubst < /etc/atomfeed.properties.template > /opt/pacs-integration/lib/atomfeed.properties
envsubst < /etc/application.properties.template > /opt/pacs-integration/lib/application.properties

echo "Waiting for ${DB_HOST}.."
sh wait-for.sh -t 300 "${DB_HOST}":"${DB_PORT}"

#./update_openmrs_host_port.sh

echo "Waiting for ${OPENMRS_HOST}.."
sh wait-for.sh -t 300 "${OPENMRS_HOST}":"${OPENMRS_PORT}"

echo "[INFO] Starting Application"
#java -jar $SERVER_OPTS $DEBUG_OPTS /opt/pacs-integration/lib/pacs-integration.jar
java -jar $SERVER_OPTS $DEBUG_OPTS /opt/pacs-integration/lib/pacs-integration.jar --spring.config.location=file:/opt/pacs-integration/lib/