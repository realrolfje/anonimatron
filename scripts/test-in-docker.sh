#!/usr/bin/env bash
set -euo pipefail

readonly SCRIPT_NAME="$(basename "$0")"
readonly DEFAULT_JAVA_VERSION="8"
readonly DEFAULT_MAVEN_VERSION="3.9.9"

java_version="$DEFAULT_JAVA_VERSION"
maven_version="$DEFAULT_MAVEN_VERSION"

usage() {
  cat <<EOF
Usage: $SCRIPT_NAME [options] [maven arguments...]

Runs the Maven build in a Docker container with a selectable Java version.
Defaults to Java $DEFAULT_JAVA_VERSION and 'mvn test'.

Options:
  -j, --java VERSION     Java version to use, for example 8, 11, 17, or 21.
  -m, --maven VERSION    Maven Docker image version. Default: $DEFAULT_MAVEN_VERSION.
  -h, --help             Show this help.

Examples:
  $SCRIPT_NAME
  $SCRIPT_NAME --java 8
  $SCRIPT_NAME --java 17 clean test
  $SCRIPT_NAME --java 21 -DskipTests package
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    -j|--java)
      if [[ $# -lt 2 || "$2" == -* ]]; then
        echo "Missing value for $1." >&2
        usage >&2
        exit 2
      fi
      java_version="$2"
      shift 2
      ;;
    --java=*)
      java_version="${1#*=}"
      shift
      ;;
    -m|--maven)
      if [[ $# -lt 2 || "$2" == -* ]]; then
        echo "Missing value for $1." >&2
        usage >&2
        exit 2
      fi
      maven_version="$2"
      shift 2
      ;;
    --maven=*)
      maven_version="${1#*=}"
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    --)
      shift
      break
      ;;
    -*)
      break
      ;;
    *)
      break
      ;;
  esac
done

if ! command -v docker >/dev/null 2>&1; then
  echo "Docker is not installed or not on PATH." >&2
  exit 127
fi

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd -P)"
image="maven:${maven_version}-eclipse-temurin-${java_version}"

if [[ $# -eq 0 ]]; then
  set -- test
fi

echo "Running: mvn $*"
echo "Docker image: $image"

exec docker run --rm \
  --user "$(id -u):$(id -g)" \
  -v "$repo_root":/workspace \
  -v "$HOME/.m2":/home/maven/.m2 \
  -e HOME=/home/maven \
  -e MAVEN_CONFIG=/home/maven/.m2 \
  -w /workspace \
  "$image" \
  mvn -Duser.home=/home/maven "$@"
