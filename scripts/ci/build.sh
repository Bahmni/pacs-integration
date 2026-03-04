# This Source Code Form is subject to the terms of the Mozilla Public License,
# v. 2.0. If a copy of the MPL was not distributed with this file, You can
# obtain one at https://www.bahmni.org/license/mplv2hd.
#
# Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
# graphic logo is a trademark of OpenMRS Inc.

#!/usr/bin/env bash
echo "Removing pacs integration from m2 repository."
rm -rf ~/.m2/repository/org/bahmni/module/pacsintegration
rm -rf ~/.m2/repository/org.ict4h
echo "Removed pacs integration from m2 repository."

echo "Building PACS"
/home/jss/apache-maven-3.0.5/bin/mvn clean install
echo "Done."