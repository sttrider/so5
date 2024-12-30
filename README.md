## Table of Contents
* [Requirements](#requirements)
* [Usage](#usage)
* [Common Questions](#common-questions)
* [Documentation](#documentation)

## Requirements
The application requires the following to run:
* [Docker](https://docs.docker.com/engine/install/)

## Usage
You should use docker-compose to compile, package and run the application.

```bash
docker-compose up
```

With the application running, you can access the swagger endpoint to try out all the API's. `http://localhost:8080/swagger-ui/index.html`

Executing `curl --location --request POST 'http://localhost:8080/data/generate'` will create some fake products with image.

To test the One Click Button you need to login. We have two test users:
| username      | password |
|---------------|----------|
| user1@so5.com | user1    |
| user2@so5.com | user2    |

User `user1` has address and credit card information, so he can buy products.
User `user2` doesn't have address and credit card information, so will not be able to purchase products.

## Common Questions
* I used Keycloak as an Authorization and Authentication server.

## Documentation
### Purchase flow
![Purchase flow](doc/purchase.png?raw=true "Purchase flow")

## Add cart flow
![Add cart flow](doc/request_product.png?raw=true "Add cart flow")

### Save product flow
![Save product flow](doc/save_product.png?raw=true "Save product flow")
