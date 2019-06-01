# Server

Start Spring with mvn spring-boot:run

Expects a MongoDB database running on port 27017

start db with docker run -p 27017:27017 mongo:3.2

## Build

Docker build
./mvnw install dockerfile:build

## Start 

Start everything with 
docker-compose up

## Healthcheck
Browse to http://localhost:8000/rest/health

## Auth
Set the boolean in the main app to true; 

Add to request header:
X-Firebase-Auth: eyJhbGciOiJSUzI...OuybPkgAglA

## Kubernetes
Docker build 
./mvnw install dockerfile:build
Tag the containers
docker tag coffeeserver:latest eu.gcr.io/blackalert-207710/server:latest
docker tag mongo:3.2 eu.gcr.io/blackalert-207710/mongodb:latest
Push the containers to Google Cloud Registry
docker push eu.gcr.io/blackalert-207710/server:latest
docker push eu.gcr.io/blackalert-207710/mongodb:latest
Create deployments and services
