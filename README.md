# Newsler Project

### *Newsler is about to make your mailing even more effective!*

## Table of Contents

- Technologies Used
- Getting Started
- Application Architecture
- API Documentation
- Contributing
- License

## Technologies Used

- Java 17
- Spring Boot 3.0.1
- Spring Data JPA
- Spring Security
- JWT
- H2 Database
- Gradle

## Getting Started

To run this application on your local machine, follow these steps:

1. Clone this repository to your local machine.
2. Open a terminal and navigate to the root directory of the project.
3. Before application start make sure you have `maildev` docker container running.
    1. If this is your first run run gradle `runMailDevContainer` task
    2. Or pull docker [`maildev`](https://hub.docker.com/r/maildev/maildev) image by running following commands:
    ```shell
    docker pull maildev/maildev
    ```  
    ```shell
    docker run -p 1080:1080 -p 1025:1025 maildev/maildev
    ```
4. Run the following command to start the application:

```shell
./gradlew bootRun
```

5. Open your web browser and go to http://localhost:8080.

## Application Configuration

The Newsletter App can be configured to run in two modes - `HTTP` or `HTTPS`, depending on the configuration specified
by
the user.
This allows for flexibility in terms of security and communication protocols, as the user can choose the appropriate
mode based on their needs and requirements.

By default, the application runs in `HTTP` mode, but it can be configured to run in `HTTPS` mode by specifying the
appropriate `CLI` argument or system environment. This ensures that the application can be tailored to meet the specific
security
needs of the user.
Additionally, the ability to switch between modes allows for greater compatibility with different environments and
systems, as the user can choose the appropriate mode based on the requirements of their specific use case.

**Configuration via configuration file will be available soon.**

## Application Architecture

In Newsler Project, the hexagonal architecture (also known as "port and adapter architecture") has been used.
The hexagonal architecture is a design pattern that allows for the separation of business logic from technical
implementations.

To find out more about hexagonal architecture visit
this [wiki page](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)).

To find out more about Newsler's architecture visit [ARCHITECTURE.md](ARCHITECTURE.md)

## API Documentation

#### *The API documentation will be available soon!*

## Contributing

Contributions to this project are welcome. If you notice any bugs or have suggestions for new features, please create an
issue or submit a pull request.

## License

This project is licensed under the GNU GPL v3 License. See the LICENSE file for details.
