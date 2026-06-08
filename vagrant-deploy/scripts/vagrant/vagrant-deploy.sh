#!/bin/bash -x -e

PATH_OF_CURRENT_SCRIPT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $PATH_OF_CURRENT_SCRIPT/vagrant_functions.sh
#All config is here
WEBAPP_FOLDER=/home/bahmni/apache-tomcat-8.0.12/webapps
MODULE_DEPLOYMENT_FOLDER=/tmp/
CWD=$1
VERSION=$2
PROJECT_BASE=$PATH_OF_CURRENT_SCRIPT/../../..
SCRIPTS_DIR=$CWD/scripts/vagrant


# Deploy pacs
scp_to_vagrant $PROJECT_BASE/pacs-integration-webapp/target/pacs-integration.war $MODULE_DEPLOYMENT_FOLDER/pacs-integration.war

run_in_vagrant -f "$SCRIPTS_DIR/deploy_war.sh"