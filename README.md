# Newsler Project

### *Newsler is about to make your mailing even more effective!*
#### Newsler Project aggregates different email sending solutions into one centralised and standardised solution.
##### Currently supported solutions:
- [EmailLabs.io](https://emaillabs.io/)

##### Solutions that will be supported:
- [Elastic Email](https://elasticemail.com/)
- [Mailchimp](https://mailchimp.com/)
- [MailerLite](https://www.mailerlite.com/)
- [Sender](https://www.sender.net/)
- [Buttondown](https://buttondown.email/) :soon: to be decided

## Table of Contents

- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
    - [EmailLabs](#email-labs-account-settings)
    - [Maildev container](#smtp-emulation)
    - [H2 Database setup](#h2-database-setup)
    - [Newsler Project run](#run-newsler-project)
- [Application Configuration](#application-architecture)
- [Application Architecture](#application-architecture)
- [Application Security](#application-security)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)
- [License](#license)

## Technologies Used

- Java 17
- Spring Boot 3.0.1
- Spring Security
- Spring Data JPA
- JWT
- H2 Database
- Gradle
- Docker

## Getting Started

### Preconditions

#### Email Labs Account Settings

In order to have Newsler Project fully working you have to provide correct, and above all true, Email Labs account
details such as `SMTP account`, `App Key`, `Secret Key` and Email Labs authorised `email address`.

#### SMTP emulation

To emulate SMTP activity, the development version uses the docker [`maildev`](https://hub.docker.com/r/maildev/maildev)
image.

#### H2 Database setup

In order to enter the correct, and above all true, Email Labs account details such as `SMTP account`, `App Key`, 
`Secret Key` and Email Labs authorised `email address` you should enter these details in one of the supported ways

- via environment variables (names below):
    - NEWSLER_APP_KEY
    - NEWSLER_SECRET_KEY
    - NEWSLER_SMTP
    - NEWSLER_EMAIL
- via the keystore file - *available soon*

#### Run Newsler Project

1. If this is your first ever run gradle `runMailDevContainer` task
2. Or pull and run docker [`maildev`](https://hub.docker.com/r/maildev/maildev) image by providing following commands:
   ```shell
   docker pull maildev/maildev
   ```  
   ```shell
   docker run -p 1080:1080 -p 1025:1025 maildev/maildev
   ```

To run this application on your local machine, follow these steps:

1. Before application start make sure you have [`maildev`](https://hub.docker.com/r/maildev/maildev) docker container
   running.
2. Clone this repository to your local machine.
   ```shell
    git clone https://github.com/Pumbakos/Newsler.git
   ```
3. Open a terminal and navigate to the root directory of the project.
4. Run the following command to start the application:

    ```shell
    ./gradlew bootRun
    ```

5. Open your web browser and go to http://localhost:8080.

## Application Configuration

Newsler Project can be configured to run in two modes - `HTTP` or `HTTPS`, depending on the configuration specified
by the user.
This allows for flexibility in terms of security and communication protocols, as the user can choose the appropriate
mode based on their needs and requirements.

By default, the application runs in `HTTP` mode, but it can be configured to run in `HTTPS` mode by specifying the
appropriate `CLI` argument or environmental variable. This ensures that the application can be tailored to meet the
specific
security needs of the user.
Additionally, the ability to switch between modes allows for greater compatibility with different environments and
systems, as the user can choose the appropriate mode based on the requirements of their specific use case.

To set the application into `HTTPS` mode, properties must be provided in at least one way: (keep in mind that CLI
arguments carry higher priority than environmental variables).
Properties to provide:

- `newsler.ssl.keystore.file` path to keystore file
- `newsler.ssl.keystore.password` password to keystore file
- `newsler.ssl.keystore.type` keystore type, preferably JKS
- `newsler.ssl.keystore.alias` keystore alias

**Configuration via configuration file will be available soon.**

## Application Architecture

In Newsler Project, the hexagonal architecture (also known as "port and adapter architecture") has been used.
The hexagonal architecture is a design pattern that allows for the separation of business logic from technical
implementations.

To find out more about hexagonal architecture visit
this [wiki page](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)).

To find out more about Newsler's architecture visit [ARCHITECTURE](ARCHITECTURE.md) file

## Application Security

Newsler Project uses Spring Security and JWT (JSON Web Tokens) for authentication and authorization. Spring Security is
a powerful and customizable framework that provides a wide range of security features, while JWT is a widely-used
standard for securely transmitting information between parties as a JSON object.

To find out more about Newsler's security visit [SECURITY](SECURITY.md) file

## API Documentation

#### *The API documentation will be available soon!*

## Contributing

Contributions to this project are welcome. If you notice any bugs or have suggestions for new features, please create an
issue or submit a pull request.

## License

This project is licensed under the [GPL-3.0 license](LICENSE). See the [LICENSE](LICENSE) file for details.
