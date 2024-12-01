# Running the app

Run the DB with:
> ```docker-compose up db```

Put OMDb API key into ```src/main/resources/application-local.yml```

Run the App with:
> ```./mvnw spring-boot:run -Dspring-boot.run.profiles=local```

Run tests with:
> ```./mvnw test```

# Using the app

Access Swagger:

> http://localhost:8080/api/swagger-ui/index.html

There are 2 test users:

1. ```user/password``` with role ```USER```
2. ```admin/admin``` with roles ```USER``` and ```ADMIN```

```ADMIN``` role has access to all endpoints, ```USER``` role to all except the ones for creating/updating showtimes and
deleting reviews

# Technical decisions

- created as a single Spring Boot service over PostgreSQL
- good starting point for splitting into microservices (extract reviews? add ticket/reservation
  service?) or simple modularization (depending on the real life business needs)
- basic security for the purpose of the task

# List of TODOs

1. Rethink the need for JPA (Spring Data JDBC? Exposed?)
2. Fully dockerize the app with multistage build
3. Introduce profiles
4. Introduce CI pipelines
5. Improve test coverage, add integration tests
6. Add real authentication (i.e. OAuth)
7. Add resilience patterns to OMDb calls (retry, timeout, circuit breaker...)
8. Modularize the app?
9. Add health checks
