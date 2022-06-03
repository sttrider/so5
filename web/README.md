# Saks OFF 5TH Java Challenge
* João Vitor Moreira Oliveira

## Table of Contents
* [Requirements](#requirements)
* [Usage](#usage)

## Requirements
The application requires the following to run:
* [Node](https://nodejs.org/en/)

## Usage
You must use docker-compose to compile, package, and run the application. You need to be in the project's root folder to run it.

```bash
docker-compose up
```

To test it you need to execute following commands:

Linux/Mac environment
```bash
npm install
CI=true npm test
```

Windows
```bash
npm install
set CI=true&&npm test
```