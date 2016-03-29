#!/bin/sh
# set -e
SCRIPTS_DIR=`dirname $0`
DATABASE_NAME="bahmni_pacs"
BAHMNI_PACS_DB_SERVER="localhost"

if [ -f /etc/bahmni-installer/bahmni.conf ]; then
. /etc/bahmni-installer/bahmni.conf
fi

if [ "$(psql -Upostgres -h $BAHMNI_PACS_DB_SERVER -lqt | cut -d \| -f 1 | grep -w $DATABASE_NAME | wc -l)" -eq 0 ]; then
    echo "Creating database : $DATABASE_NAME"
    psql -U postgres -h $BAHMNI_PACS_DB_SERVER -f $SCRIPTS_DIR/setupDB.sql
else
    echo "The database $DATABASE_NAME already exits"
fi