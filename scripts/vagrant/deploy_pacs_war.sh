#!/bin/sh -x

TEMP_LOCATION=/tmp/deploy_pacs
ELIS_WAR_LOCATION=/home/bahmni/apache-tomcat-8.0.12/webapps

sudo rm -rf $ELIS_WAR_LOCATION/pacs
sudo su - bahmni -c  "cp -f $TEMP_LOCATION/* $ELIS_WAR_LOCATION"
