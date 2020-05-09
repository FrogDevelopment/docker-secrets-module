[![](https://jitci.com/gh/FrogDevelopment/docker-secrets-module/svg)](https://jitci.com/gh/FrogDevelopment/docker-secrets-module)
[![Release](https://jitpack.io/v/com.frog-development/docker-secrets-module.svg)](https://jitpack.io/#com.frog-development/docker-secrets-module)

### How to use the docker-secrets-module on your project
##### Step 1. Add the JitPack repository to your build file

Add it in your root `build.gradle` at the end of repositories:
```groovy
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
```
##### Step 2. Add the dependency
```groovy
	dependencies {
	        implementation 'com.frog-development:docker-secrets-module:1.0.0'
	}
```
##### Step 3. Use the Docker Secrets defined for your service.
For each secret file found on the secrets directory, a property will be constructed as `docker-secrets.{file-name}`.
Here, for example, we have 2 secrets `my_user` and `my_password`, leading to this configuration: 
```yaml
docker-secrets:
    path: /run/secrets # by default, can be omitted

spring:
  security:
    user:
      name: ${docker-secrets.my_user}
      password: ${docker-secrets.my_password}
```
