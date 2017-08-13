#!/bin/bash

set -e
set -o errexit
set -o pipefail
set -o nounset

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

function ensure_release_commit {
    local CHANGED_FILES=$(git diff-tree --no-commit-id --name-only -r HEAD)

    if [[ "$CHANGED_FILES" != *version.json* ]]; then
        echo "Version did not change on this commit. Ignoring"; exit 0;
    fi
}

function maven_publish {
    local PACKAGE=$1
    local VERSION=$2
    local PAYLOAD="{\"username\": \"${SONATYPE_USER}\", \"password\": \"${SONATYPE_KEY}\"}"
    echo "Publishing $PACKAGE..."
    RESULT=$(curl -s -X POST -u "$BINTRAY_USER:$BINTRAY_KEY" -H "Content-Type: application/json" --data "$PAYLOAD" "https://bintray.com/api/v1/maven_central_sync/http4k/maven/$PACKAGE/versions/$VERSION")

    if [[ ! "${RESULT}" =~ .*Successful.* ]]; then
       echo "Failed: ${RESULT}"
       exit 1
    fi
}

PACKAGE_NAME=${PACKAGE_NAME:-}

if [[ -z "$PACKAGE_NAME"  ]]; then
    echo "PACKAGE_NAME is missing."
    exit 1
fi

ensure_release_commit

LOCAL_VERSION=`jq -r .http4k.version $DIR/version.json`

echo "Making $LOCAL_VERSION available of $PACKAGE_NAME in Maven central..."

maven_publish ${PACKAGE_NAME} ${LOCAL_VERSION}