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

Runs Maven in Docker. The project is mounted into /workspace, Maven settings
are mounted read-only from \$HOME/.m2/settings.xml, and the writable Maven
repository is kept under target/docker-maven.

When GPG_TTY is set in the host environment, an isolated copy of \$HOME/.gnupg
is mounted into the container and GPG_TTY is set to the container tty before
Maven starts. This is used by the release script for Maven artifact signing.

Options:
  -j, --java VERSION     Java version to use. Default: $DEFAULT_JAVA_VERSION.
  -m, --maven VERSION    Maven Docker image version. Default: $DEFAULT_MAVEN_VERSION.
  -h, --help             Show this help.

Examples:
  $SCRIPT_NAME clean test
  $SCRIPT_NAME --java 8 clean deploy -P release
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

if [[ $# -eq 0 ]]; then
  usage >&2
  exit 2
fi

if ! command -v docker >/dev/null 2>&1; then
  echo "Docker is not installed or not on PATH." >&2
  exit 127
fi

readonly repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd -P)"
readonly docker_maven_dir="$repo_root/target/docker-maven"
readonly host_settings="$HOME/.m2/settings.xml"
readonly host_gnupg="$HOME/.gnupg"
readonly image="maven:${maven_version}-eclipse-temurin-${java_version}"
container_gnupg_home=""

cleanup() {
  if [[ -n "$container_gnupg_home" && -d "$container_gnupg_home" ]]; then
    rm -rf "$container_gnupg_home"
  fi
}
trap cleanup EXIT

mkdir -p "$docker_maven_dir"

echo "Running: mvn $*"
echo "Docker image: $image"
echo "Docker Maven directory: $docker_maven_dir"

docker_args=(
  --rm
  --user "$(id -u):$(id -g)"
  -v "$repo_root":/workspace
  -v "$docker_maven_dir":/home/maven/.m2
  -e HOME=/home/maven
  -e MAVEN_CONFIG=/home/maven/.m2
  -w /workspace
)

if [[ -f "$host_settings" ]]; then
  docker_args+=(-v "$host_settings":/home/maven/.m2/settings.xml:ro)
else
  echo "Maven settings file not found: $host_settings"
  echo "Continuing without host Maven settings. Release deploys may need repository credentials."
fi

if [[ -n "${GPG_TTY:-}" ]]; then
  if [[ ! -d "$host_gnupg" ]]; then
    echo "GPG_TTY is set, but GPG home was not found: $host_gnupg" >&2
    exit 1
  fi
  if ! command -v rsync >/dev/null 2>&1; then
    echo "GPG signing requires rsync to copy $host_gnupg without live sockets or locks." >&2
    exit 127
  fi

  container_gnupg_home="$(mktemp -d "${TMPDIR:-/tmp}/anonimatron-docker-gnupg.XXXXXX")"
  rsync -a --delete \
    --exclude 'S.*' \
    --exclude '*.lock' \
    "$host_gnupg"/ "$container_gnupg_home"/
  chmod 700 "$container_gnupg_home"
  find "$container_gnupg_home" -type d -exec chmod 700 {} +
  find "$container_gnupg_home" -type f -exec chmod 600 {} +

  docker_args+=(
    -v "$container_gnupg_home":/tmp/host-gnupg:ro
    -e ANONIMATRON_DOCKER_GNUPG_SOURCE=/tmp/host-gnupg
    -e GNUPGHOME=/tmp/gnupg
  )

  if [[ -t 0 && -t 1 ]]; then
    docker_args+=(-it)
  else
    echo "GPG_TTY is set, but this shell is not interactive; GPG pinentry may fail." >&2
  fi
fi

docker run "${docker_args[@]}" \
  "$image" \
  sh -c '
    if [ -n "${ANONIMATRON_DOCKER_GNUPG_SOURCE:-}" ]; then
      rm -rf "$GNUPGHOME"
      mkdir -p "$GNUPGHOME"
      cp -R "$ANONIMATRON_DOCKER_GNUPG_SOURCE"/. "$GNUPGHOME"/
      chmod 700 "$GNUPGHOME"
      find "$GNUPGHOME" -type d -exec chmod 700 {} +
      find "$GNUPGHOME" -type f -exec chmod 600 {} +
    fi
    if tty -s; then export GPG_TTY="$(tty)"; fi
    exec mvn -Duser.home=/home/maven "$@"
  ' sh "$@"
