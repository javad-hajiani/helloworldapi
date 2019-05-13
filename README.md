# Revolut DevOps Challenge

There is full automated pipeline to deploy app into aws ecs zero downtime.

## Architecture
![alt text](https://raw.githubusercontent.com/javad-hajiani/helloworldapi/master/architecture.png)


## Requirements

install aws cli and login 
```bash
pip install awscli
```

Setup ecs cluster with below cloud formation.

```bash
aws cloudformation deploy \
    --stack-name <Your Stack Name> \
    --template-file ./cloudformation/revolut-helloapi-stack.yml \
    --capabilities CAPABILITY_IAM \
    --parameter-overrides KeyName='<keypair_id>' \
    VpcId='<vpc_id>' \
    SubnetId='<subnet_id_1>,<subnet_id_2>' \
    ContainerPort=4567 \
    DesiredCapacity=2 \
    EcsImageUri='839431657321.dkr.ecr.us-east-2.amazonaws.com/revolut' \
    EcsImageVersion='latest' \
    InstanceType=t2.micro \
    MaxSize=3
```

## deploy new version
#### In this case just merge to master or push directly to master branch ( Not Recommend )

```bash
aws cloudformation deploy \
    --stack-name <Your Stack Name> \
    --template-file ./cloudformation/revolut-helloapi-stack.yml \
    --capabilities CAPABILITY_IAM \
    --parameter-overrides KeyName='<keypair_id>' \
    VpcId='<vpc_id>' \
    SubnetId='<subnet_id_1>,<subnet_id_2>' \
    ContainerPort=4567 \
    DesiredCapacity=2 \
    EcsImageUri='839431657321.dkr.ecr.us-east-2.amazonaws.com/revolut' \
    EcsImageVersion='latest' \
    InstanceType=t2.micro \
    MaxSize=3
```


