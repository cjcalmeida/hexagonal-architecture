echo "Moving to Deployment Folder..."
cd /app
echo "Moving App to base Folder..."
cp target/hexagonal-architecture.jar app.jar
echo "Clean Build Artifacts..."
rm -rf src
rm -rf /target
echo "All tasks executed with success"
