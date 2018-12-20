# play-docka

This is a demo web app for AWS.

## Deployments

The buildspec files in the root of the repo can be used with AWS CodeBuild and CodePipeline as follows:

| Buildspec file | Deployment target
|----------------|------------------
| [buildspec.yml](buildspec.yml) | Single-container Elastic Beanstalk
| [buildspec-ecr.yml](buildspec-ecr.yml) | Multi-container Elastic Beanstalk
| [buildspec-ecs.yml](buildspec-ecs.yml) | ECS
