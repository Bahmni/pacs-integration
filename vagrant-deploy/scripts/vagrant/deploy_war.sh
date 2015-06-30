#!/usr/bin/env bash
TEMP_LOCATION=/tmp/
USER=bahmni
#USER=jss
WEBAPP_LOCATION=/home/$USER/apache-tomcat-8.0.12/webapps

sudo su - $USER -c "cp -f $TEMP_LOCATION/pacs-integration.war $WEBAPP_LOCATION/"