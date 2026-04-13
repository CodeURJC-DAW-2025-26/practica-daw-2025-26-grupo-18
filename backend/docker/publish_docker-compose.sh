#!/usr/bin/env bash
set -euo pipefail

if ! command -v docker >/dev/null 2>&1; then
  echo "Error: Docker no esta instalado o no esta en PATH." >&2
  exit 1
fi

if [ "$#" -lt 1 ] || [ "$#" -gt 3 ]; then
  echo "Uso: $0 <dockerhub_user> [compose_repo_name] [tag]" >&2
  echo "Ejemplo: $0 miusuario scam-g18-compose latest" >&2
  exit 1
fi

DOCKERHUB_USER="$1"
COMPOSE_REPO_NAME="${2:-scam-g18-compose}"
TAG="${3:-latest}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="$SCRIPT_DIR/docker-compose.yml"
TEMP_DIR="$(mktemp -d)"

cleanup() {
  rm -rf "$TEMP_DIR"
}
trap cleanup EXIT

if [ ! -f "$COMPOSE_FILE" ]; then
  echo "Error: No se encontro $COMPOSE_FILE" >&2
  exit 1
fi

cp "$COMPOSE_FILE" "$TEMP_DIR/docker-compose.yml"
cat > "$TEMP_DIR/Dockerfile" <<'EOF'
FROM scratch
COPY docker-compose.yml /docker-compose.yml
EOF

TARGET_IMAGE="$DOCKERHUB_USER/$COMPOSE_REPO_NAME:$TAG"

echo "Construyendo artefacto OCI con docker-compose.yml como $TARGET_IMAGE ..."
docker build -t "$TARGET_IMAGE" "$TEMP_DIR"

echo "Publicando $TARGET_IMAGE en DockerHub ..."
docker push "$TARGET_IMAGE"

echo "docker-compose.yml publicado como artefacto OCI: $TARGET_IMAGE"