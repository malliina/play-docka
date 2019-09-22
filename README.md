# play-docka

This is a demo web app for AWS. Check [cfn](https://github.com/malliina/cfn) for AWS CloudFormation 
templates that use this app.

## Deployments

The buildspec files in the root of the repo can be used with AWS CodeBuild and CodePipeline as follows:

| Buildspec file | Deployment target
|----------------|------------------
| [buildspec.yml](buildspec.yml) | Single-container Elastic Beanstalk
| [buildspec-ecr.yml](buildspec-ecr.yml) | Multi-container Elastic Beanstalk
| [buildspec-ecs.yml](buildspec-ecs.yml) | ECS with Fargate

## Running locally

To run the app without docker, run:

    sbt run

To start a Docker container locally, install Docker on your machine, then run:

    sbt docker:stage
    docker build --tag=hello target/docker/stage
    docker run -p 9000:9000 hello

The app should now run at http://localhost:9000.
