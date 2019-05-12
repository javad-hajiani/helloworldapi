#!/bin/bash -e

TIMESTAMP=$(date '+%Y%m%d%H%M%S')
VERSION="${TIMESTAMP}-${TRAVIS_COMMIT}"
ZIP="${VERSION}.zip"
REGISTRY_URL=${AWS_ACCOUNT_ID}.dkr.ecr.${EB_REGION}.amazonaws.com
SOURCE_IMAGE="${DOCKER_REPO}"
TARGET_IMAGE="${REGISTRY_URL}/${DOCKER_REPO}"
TARGET_IMAGE_LATEST="${TARGET_IMAGE}:latest"
TARGET_IMAGE_VERSIONED="${TARGET_IMAGE}:${VERSION}"


# Push image to ECR
###################

$(aws ecr get-login --no-include-email)

# update latest version
docker tag ${SOURCE_IMAGE} ${TARGET_IMAGE_LATEST}
docker push ${TARGET_IMAGE_LATEST}

# push new version
docker tag ${SOURCE_IMAGE} ${TARGET_IMAGE_VERSIONED}
docker push ${TARGET_IMAGE_VERSIONED}
