#!/usr/bin/env bash
set -euo pipefail

readonly SCRIPT_NAME="$(basename "$0")"

mvn_in_docker_options=()

usage() {
  cat <<EOF
Usage: $SCRIPT_NAME [options] [maven arguments...]

Runs the Maven build in a Docker container with a selectable Java version.
Defaults to 'mvn test'. Java and Maven defaults come from mvn-in-docker.sh.

Options:
  -j, --java VERSION     Java version to use, for example 8, 11, 17, or 21.
  -m, --maven VERSION    Maven Docker image version.
  -h, --help             Show this help.

Examples:
  $SCRIPT_NAME
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
      mvn_in_docker_options+=("$1" "$2")
      shift 2
      ;;
    --java=*)
      mvn_in_docker_options+=("$1")
      shift
      ;;
    -m|--maven)
      if [[ $# -lt 2 || "$2" == -* ]]; then
        echo "Missing value for $1." >&2
        usage >&2
        exit 2
      fi
      mvn_in_docker_options+=("$1" "$2")
      shift 2
      ;;
    --maven=*)
      mvn_in_docker_options+=("$1")
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

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd -P)"
mvn_in_docker="$repo_root/scripts/mvn-in-docker.sh"

if [[ $# -eq 0 ]]; then
  set -- test
fi

exec "$mvn_in_docker" "${mvn_in_docker_options[@]}" "$@"
