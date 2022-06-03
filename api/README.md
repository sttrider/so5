# Saks OFF 5TH Java Challenge
* João Vitor Moreira Oliveira

## Table of Contents
* [Requirements](#requirements)
* [Usage](#usage)
* [Common Questions](#common-questions)

## Requirements
The application requires the following to run:
* [Maven](https://maven.apache.org/install.html)
* [Java 17](https://jdk.java.net/17/)
* [Docker](https://docs.docker.com/engine/install/)

## Usage
You must use docker-compose to compile, package, and run the application. You need to be in the project's root folder to run it.

```bash
docker-compose up
```

With the application running, you can access the swagger endpoint to try out all the API's. `http://localhost:8080/swagger-ui/index.html`

Because of testcontainers I had to skip tests during compile phase. If you want to run all the tests, you should execute the following command:

```bash
mvn clean test
```

To compile it you can use the following command:
```bash
mvn clean package
```

## Common Questions
* In a high concurrently environment, I would use a Redis to improve our response time.
* I didn't handle null attributes in the save product request. Because of the `NOT NULL` database constraint, this will throw an exception. With more time, I would have used spring validation feature to do that.
