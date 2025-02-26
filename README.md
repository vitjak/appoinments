# Appointment Scheduling Application

## Overview

The Appointment Scheduling Application is Spring Boot application for managing appointments, practitioners, patients, and time slots, fully Dockerized with a PostgreSQL database.

## Running the Application

### Clone the repository:

`git clone https://github.com/vitjak/appointments-app.git`  
`cd appointments-app`  

### Run the application using Docker Compose:

`docker-compose up -d`  

This will start PostgreSQL on port 5432 and Appointments Scheduling application on port 8080

### Docker Image

The application Docker image used in docker-compose file is hosted at:  
`ghcr.io/vitjak/appointments-app:latest`

## Testing the API

A Postman collection **postman.json** is available in the project root which you can import in Postman. Collection contains requests for testing all the endpoints.

##  API Documentation

When the application is running, API documentation is available at:  
`http://localhost:8080/swagger-ui/index.html`

## Technologies Used

* Spring Boot (3.x)
* PostgreSQL (15)
* Docker
* Lombok
* Spring Data JPA
* Swagger/OpenAPI for API documentation
* JUnit & Mockito for testing