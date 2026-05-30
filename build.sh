#!/bin/bash

# Exit immediately if a pipeline, list, or compound command fails
set -e

VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
BUILD_DATE=$(date -u +'%Y-%m-%dT%H:%M:%SZ')
GIT_COMMIT=$(git rev-parse --short HEAD 2>/dev/null || echo "unknown")

if docker buildx build \
  --load \
  --build-arg VERSION="$VERSION" \
  --build-arg BUILD_DATE="$BUILD_DATE" \
  --build-arg GIT_COMMIT="$GIT_COMMIT" \
  -t "homeassistant-musicbox:$VERSION" \
  -t "homeassistant-musicbox:latest" \
  . ; then
  
  echo "✓ Built image: homeassistant-musicbox:$VERSION"
else
  echo "✗ Build failed!"
  exit 1
fi