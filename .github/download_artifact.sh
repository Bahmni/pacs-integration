# This Source Code Form is subject to the terms of the Mozilla Public License,
# v. 2.0. If a copy of the MPL was not distributed with this file, You can
# obtain one at https://www.bahmni.org/license/mplv2hd.
#
# Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
# graphic logo is a trademark of OpenMRS Inc.

#!/bin/bash
set -e

#Parameters (repository_name,artifact_name,github_pat)

if [ $# -ne 3 ]
then
echo "Invalid Arguments. Need repository_name, artifact_name, github_pat"
exit 2
fi

curl -s https://api.github.com/repos/Bahmni/$1/actions/artifacts | \
    jq '[.artifacts[] | select (.name == '\"$2\"')]' | jq -r '.[0] | .archive_download_url' | \
    xargs curl -L -o $2.zip -H "Authorization: token $3"
unzip -d package/resources/ $2.zip && rm $2.zip
