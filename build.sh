#!/bin/bash
set -e

# Fallbacks for local development if variables aren't passed
VERSION="${VERSION:-local}"
BUILD_DATE="${BUILD_DATE:-$(date -u +'%Y-%m-%dT%H:%M:%SZ')}"
GIT_COMMIT="${GIT_COMMIT:-$(git rev-parse --short HEAD 2>/dev/null || echo "unknown")}"

echo "Building image with Version: $VERSION, Commit: $GIT_COMMIT"

docker buildx build \
  --build-arg VERSION="$VERSION" \
  --build-arg BUILD_DATE="$BUILD_DATE" \
  --build-arg GIT_COMMIT="$GIT_COMMIT" \
  "$@" .