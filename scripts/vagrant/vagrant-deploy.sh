#!/bin/bash -x

PATH_OF_CURRENT_SCRIPT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $PATH_OF_CURRENT_SCRIPT/vagrant_functions.sh

#All config is here
TEMP_PACS_WAR_FOLDER=/tmp/deploy_pacs
CWD=$1
SCRIPTS_DIR=$CWD/scripts/vagrant

# Setup environment
run_in_vagrant -f "$SCRIPTS_DIR/setup_environment.sh"

# Copy PACS War file to Vagrant tmp
scp_to_vagrant $2 $TEMP_PACS_WAR_FOLDER

#Deploy them from Vagrant /tmp to appropriate location
run_in_vagrant -f "$SCRIPTS_DIR/deploy_pacs_war.sh"
