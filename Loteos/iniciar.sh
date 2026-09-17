#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "==================================================="
echo "  INICIANDO SERVIDOR (base de datos intacta)"
echo "==================================================="
echo

echo "[1/2] Compilando proyecto con Maven..."
mvn clean package

echo
echo "[2/2] Lanzando aplicación en http://localhost:8080 ..."
echo
exec java -jar target/App-0.0.1-SNAPSHOT.jar
