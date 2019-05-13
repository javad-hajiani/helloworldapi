#!/bin/bash -e

TIMESTAMP=$(date '+%Y%m%d%H%M%S')
VERSION="${TIMESTAMP}-${TRAVIS_COMMIT}"
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

aws cloudformation deploy \
        --stack-name revolut-helloworld \
--template-file ./cloudformation/revolut-helloapi-stack.yml \
--capabilities CAPABILITY_IAM \
--parameter-overrides KeyName='Javad' \
    VpcId='vpc-0ecf160febbdbc87d' \
        SubnetId='subnet-da7822b2, subnet-e356c699' \
            ContainerPort=4567 \
            DesiredCapacity=2 \
            EcsImageUri="${TARGET_IMAGE}" \
            EcsImageVersion='latest' \
            InstanceType=t2.micro \
            MaxSize=3