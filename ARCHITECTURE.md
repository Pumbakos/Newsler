# Newsler Project Architecture

Newsler Project is based on the hexagonal architecture (also known as "port and adapter architecture"),
which allows for the separation of business logic from technical implementations.
This architecture has been implemented in the Spring Boot framework, using:

- Java 17
- Spring Boot 3.0.1
- Spring Security
- Spring Data JPA.
- H2 database

## Components

Newsler Project is composed of three main components:

### Adapters

Adapters are responsible for interacting with external systems and devices, and provide interfaces that allow the
application to communicate with the outside world. In Newsler Project, adapters include:

- Controllers - which receive and handle `HTTP` requests from clients.
- Repository interfaces - which provide an abstraction over the database, allowing the application to store and retrieve
  data.

### Ports

Ports are abstractions that allow communication between adapters and business logic. They provide the necessary
interfaces that allow the application to exchange information between these two layers.

In Newsler Project, ports include:

- Service interfaces - which define the operations that the business layer can perform.
- DTOs (Data Transfer Objects) - which represent the data that is passed between adapters and the business layer.
  Newsler Project uses `UseCase` nomenclature so that every package that should contain dto-containing models
  in fact contains `usecase` package.

### Business Layer

The business layer contains the application's core logic, which operates on data passed through ports.
It is responsible for implementing the business rules and processes that define the behavior of the application.
In Newsler Project, the business layer includes:

Service implementations - which contain the application's core business logic.
Domain objects - which represent the core entities and concepts that the application works with.
See `pl.newsler.commons.model`

## Flow

In Newsler Project, the flow of data and control starts with the adapters, which receive input from external systems
and devices. This input is then passed through ports to the business layer, which applies the appropriate business rules
and processes. The results are then passed back through the ports and returned to the adapters, which present the output
to the external systems and devices.

## Benefits

The hexagonal architecture provides several benefits for Newsler Project, including:

- Separation of concerns - by separating the application's core logic from its technical implementations, it is easier
  to maintain, test, and modify the application.
- Flexibility - by using ports and adapters, the application can be easily adapted to different environments and
  systems.
- Scalability - by separating the application's core logic from its technical implementations, it is easier to scale the
  application by adding or modifying adapters or ports.
- Security - by using Spring Security, the application can be secured against unauthorized access and attacks.
- Overall, the hexagonal architecture provides a robust and flexible foundation for Newsler Project, allowing it to
  adapt and evolve as the needs of its users change over time.