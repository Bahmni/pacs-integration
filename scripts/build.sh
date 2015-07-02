echo "Removing pacs integration from m2 repository."
rm -rf /Users/hemanths/.m2/repository/org/bahmni/module/pacsintegration
rm -rf /Users/hemanths/.m2/repository/org.ict4h
echo "Removed pacs integration from m2 repository."

echo "Building PACS"
mvn clean install -PIT
echo "Done."