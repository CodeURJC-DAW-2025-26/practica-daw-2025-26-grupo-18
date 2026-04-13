#!/usr/bin/env bash
set -euo pipefail

if ! command -v docker >/dev/null 2>&1; then
  echo "Error: Docker no esta instalado o no esta en PATH." >&2
  exit 1
fi

if [ "$#" -lt 2 ] || [ "$#" -gt 3 ]; then
  echo "Uso: $0 <dockerhub_user> <local_image_name> [tag]" >&2
  echo "Ejemplo: $0 miusuario scam-g18 latest" >&2
  exit 1
fi

DOCKERHUB_USER="$1"
LOCAL_IMAGE_NAME="$2"
TAG="${3:-latest}"

SOURCE_IMAGE="$LOCAL_IMAGE_NAME:$TAG"
TARGET_IMAGE="$DOCKERHUB_USER/$LOCAL_IMAGE_NAME:$TAG"

LOCAL_IMAGE_ID="$(docker image inspect "$SOURCE_IMAGE" --format '{{.Id}}' 2>/dev/null || true)"
TARGET_IMAGE_ID="$(docker image inspect "$TARGET_IMAGE" --format '{{.Id}}' 2>/dev/null || true)"

if [ -z "$LOCAL_IMAGE_ID" ] && [ -z "$TARGET_IMAGE_ID" ]; then
  echo "Error: No existe ninguna imagen local con estos nombres:" >&2
  echo "  - $SOURCE_IMAGE" >&2
  echo "  - $TARGET_IMAGE" >&2
  echo "" >&2
  echo "Primero crea la imagen, por ejemplo:" >&2
  echo "  ./create_image.sh $LOCAL_IMAGE_NAME $TAG" >&2
  exit 1
fi

if [ -z "$LOCAL_IMAGE_ID" ] && [ -n "$TARGET_IMAGE_ID" ]; then
  echo "No existe $SOURCE_IMAGE, pero si existe $TARGET_IMAGE. Se publicara directamente esa imagen."
  SOURCE_IMAGE="$TARGET_IMAGE"
fi

echo "Etiquetando $SOURCE_IMAGE como $TARGET_IMAGE ..."
docker tag "$SOURCE_IMAGE" "$TARGET_IMAGE"

echo "Publicando $TARGET_IMAGE en DockerHub ..."
docker push "$TARGET_IMAGE"

echo "Imagen publicada: $TARGET_IMAGE"