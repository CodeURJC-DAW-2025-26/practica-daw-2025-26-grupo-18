#!/usr/bin/env bash
set -euo pipefail

if ! command -v docker >/dev/null 2>&1; then
  echo "Error: Docker no esta instalado o no esta en PATH." >&2
  exit 1
fi

if [ "$#" -lt 1 ] || [ "$#" -gt 2 ]; then
  echo "Uso: $0 <image_name> [tag]" >&2
  echo "Ejemplo: $0 scam-g18 latest" >&2
  exit 1
fi

IMAGE_NAME="$1"
TAG="${2:-latest}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

DOCKERFILE_PATH="$SCRIPT_DIR/DockerFile"
BUILD_CONTEXT="$SCRIPT_DIR/../.."

if [ ! -f "$DOCKERFILE_PATH" ]; then
  echo "Error: No se encontro $DOCKERFILE_PATH" >&2
  exit 1
fi

if [ ! -f "$BUILD_CONTEXT/backend/scam-g18/pom.xml" ]; then
  echo "Error: No se encontro backend/scam-g18/pom.xml en $BUILD_CONTEXT" >&2
  exit 1
fi

if [ ! -f "$BUILD_CONTEXT/frontend/scam-g18/package.json" ]; then
  echo "Error: No se encontro frontend/scam-g18/package.json en $BUILD_CONTEXT" >&2
  exit 1
fi

echo "Construyendo imagen $IMAGE_NAME:$TAG ..."
docker build -f "$DOCKERFILE_PATH" -t "$IMAGE_NAME:$TAG" "$BUILD_CONTEXT"

echo "Imagen creada: $IMAGE_NAME:$TAG"