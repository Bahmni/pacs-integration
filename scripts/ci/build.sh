#!/usr/bin/env bash
echo "Removing pacs integration from m2 repository."
rm -rf ~/.m2/repository/org/bahmni/module/pacsintegration
rm -rf ~/.m2/repository/org.ict4h
echo "Removed pacs integration from m2 repository."

echo "Building PACS"
/home/jss/apache-maven-3.0.5/bin/mvn clean install
echo "Done."