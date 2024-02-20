## Overview

### API Documentation
[Swagger UI](http://localhost:8080/swagger-ui/index.html)

### Endpoints 
Cake-Manager-api service have the following API's:
1. Adding a new cake

curl -X PUT -H 'Content-Type: application/json' -d '{"flavour": "New Chocolate","icing": "New Chocolate Cream"},"image": "example.image"}' -u "admin:password" http://localhost:8080/cakes/createCake

2. Adding multiple cakes using json file

curl -v -X POST -H "Content-Type: application/json" -u "admin:password" -d @cake.json http://localhost:8080/cakes/createCakesFromJsonArray

3. Update an existing cake

curl -X PUT -H 'Content-Type: application/json' -d '{"flavour": "New Chocolate","icing": "New Chocolate Cream"},"image": "example.image"}' -u "admin:password" http://localhost:8080/cakes/1

4. Delete an existing cake

curl -X DELETE -u "admin:password" http://localhost:8080/cakes/<ID>

5. Get an existing cake

http://localhost:8080/cakes/<ID>

6. Get all cakes

http://localhost:8080/cakes

Application uses InMemory database. Data updates will be cleared when the application is restarted.

### User access
Application uses InMemoryUserDetail for application authentication and authorisation.

1.  Below user can only access get APIs (Get All Cakes and Get Cake By Id)
```
    username: 'user',
    password: 'password', 
    roles: 'USER'
```
2.  Admin user have access to get, create, update, delete APIs
```
    username: 'admin', 
    password: 'password', 
    roles: ('USER', 'ADMIN')
```

### Running the application

1. Run application locally in terminal

```./gradlew bootRun```

2. Run the application in a docker container locally

```docker-compose up -d```

3. Build the application using Jenkinsfile. Currently there are 3 stages setup in Jenkinsfile Build, Test and Deploy.
Docker registry details and user credentials for docker registry needs updatating in Jenkinsfile to execute the deploy stage.
