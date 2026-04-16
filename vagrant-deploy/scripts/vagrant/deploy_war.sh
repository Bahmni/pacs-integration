# This Source Code Form is subject to the terms of the Mozilla Public License,
# v. 2.0. If a copy of the MPL was not distributed with this file, You can
# obtain one at https://www.bahmni.org/license/mplv2hd.
#
# Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
# graphic logo is a trademark of OpenMRS Inc.

#!/usr/bin/env bash
TEMP_LOCATION=/tmp
USER=bahmni
#USER=jss
WEBAPP_LOCATION=/home/$USER/apache-tomcat-8.0.12/webapps

sudo su - $USER -c "cp -f $TEMP_LOCATION/pacs-integration.war $WEBAPP_LOCATION/"