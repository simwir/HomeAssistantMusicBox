#!/bin/bash
set -e

echo "Parsing local project version..."

# 1. Dynamically extract project version from pom.xml
VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

# 2. Define the image tags for your local Docker daemon
IMAGE_NAME="homeassistant-musicbox"
TAG_VERSION="-t $IMAGE_NAME:$VERSION"
TAG_LATEST="-t $IMAGE_NAME:latest"

# 3. Invoke build.sh
VERSION="$VERSION" \
./build.sh --load $TAG_VERSION $TAG_LATEST "$@"

echo "✓ Local build completed successfully!"