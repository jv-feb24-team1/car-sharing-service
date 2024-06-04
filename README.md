# 🚗 Car Sharing Service

Welcome to the Car Sharing Service project! This repository contains the backend for a car-sharing service application,
built using Java and Spring Boot. It includes features for user management, car booking, and payment processing,
providing a robust and scalable solution for managing car rentals.

## 🌟 Project Overview

The Car Sharing Service project aims to simplify car sharing by offering a platform that handles user registrations,
car bookings, and payment transactions. It demonstrates the implementation of a modern web application with security
and efficiency in mind.

## 🛠️ Technologies and Tools

- **Java 21**: Core programming language.
- **Spring Boot 3.3.0**: Framework for building the application.
- **Spring Security**: Manages authentication and authorization.
- **Spring Data JPA**: Manages database interactions.
- **MySQL**: Main database for the application.
- **Liquibase**: Manages database migrations.
- **Swagger**: API documentation and testing tool.
- **Stripe**: Payment processing.
- **Docker**: Containerization of the application.
- **JUnit & Mockito**: For unit testing and mocking.
- **Telegram Bot**: Used for sending notifications to administrators.

## 📋 Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Endpoints](#endpoints)

## 🚀 Features

- User Registration and Authentication
- Car Management
- Rental System
- Payment Integration
- User Profile Management
- Secure RESTful API Endpoints

## 📦 Installation

1. **Clone the repository**:
    ```sh
    git clone https://github.com/jv-feb24-team1/car-sharing-service.git
    cd car-sharing-service
    ```

2. **Configure the database**:
   Create a new MySQL database:
    ```sql
    CREATE DATABASE car_sharing;
    ```
   Update the `application.properties` file with your MySQL database credentials:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/car_sharing
    spring.datasource.username=your_db_username
    spring.datasource.password=your_db_password
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    ```

3. **Install dependencies and build the project**:
    ```sh
    mvn clean install
    ```

4. **Run the application**:
    ```sh
    mvn spring-boot:run
    ```
   The server will start on `http://localhost:8080`.

### Using Docker

1. **Build the Docker image**:
    ```sh
    docker build -t car-sharing-service .
    ```

2. **Run the Docker container**:
    ```sh
    docker run -d -p 8080:8080 --name car-sharing-service car-sharing-service
    ```

### Create and integrate TelegramBot

1. Open @BotFather - https://t.me/BotFather
2. Type command /newbot, follow instructions to create new bot 
![botcreation.gif](doc%2Fgif%2Fbotcreation.gif)
3. Copy bot name to set to environment variable BOT_NAME, and token to BOT_TOKEN 
   Example
   BOT_NAME = carservicetestbot243242424_bot
   BOT_TOKEN = 7268155088:AAEFMSqL9Tj8BUDbFXOQIoAy_6IaFYW3Q74
4. Open the chat with bot (in our example t.me/carservicetestbot243242424_bot)
5. Input command /addMe for registration 
![botconnected.gif](doc%2Fgif%2Fbotconnected.gif)

## 📝 Usage

- Use tools like Postman or cURL to interact with the API endpoints.

## 📖 API Documentation

Explore the detailed API documentation generated by Swagger to understand the various
endpoints and their functionalities.

## 🔌 Endpoints

### User Endpoints

- **Register a new user**: `POST /api/register`
    ```json
    {
      "email": "john.doe@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "password": "securePassword123",
      "confirmPassword": "securePassword123"
      
    }
    ```

- **Authenticate a user**: `POST /api/login`
    ```json
    {
      "email": "john.doe@example.com",
      "password": "securePassword123"
    }
    ```

### Car Endpoints

- **Add a new car**: `POST /cars`
- **Get a list of cars**: `GET /cars`
- **Get car's detailed information**: `GET /cars/{id}`
- **Update car (also manage inventory)**: `PUT/PATCH /cars/{id}`
- **Delete car**: `DELETE /cars/{id}`

### Rental Endpoints

- **Add a new rental (decrease car inventory by 1)**: `POST /rentals`
- **Get rentals by user ID and whether the rental is still active or not**: `GET /rentals?user_id={user_id}&is_active={is_active}`
- **Get specific rental**: `GET /rentals/{id}`
- **Set actual return date (increase car inventory by 1)**: `POST /rentals/{id}/return`

### Payment Endpoints

- **Get payments**: `GET /payments?user_id={user_id}`
- **Create payment session**: `POST /payments`
- **Check successful Stripe payments (Endpoint for stripe redirection)**: `GET /payments/success`
- **Return payment paused message (Endpoint for stripe redirection)**: `GET /payments/cancel`

### Notifications Service (Telegram)

- **Notifications about new rentals created, overdue rentals, and successful payments**.
- **Other services interact with it to send notifications to car sharing service administrators**.
- **Uses Telegram API, Telegram Chats, and Bots**.

## 🔧 CI/CD Configuration

### Checkstyle Configuration

Add Checkstyle to `pom.xml` and create a `checkstyle.xml` in the root directory.

### GitHub Actions

Create a `.github/workflows/ci.yml` file:
```yaml
name: Java CI

on: [push, pull_request]

jobs:
   build:
      runs-on: ubuntu-latest

      steps:
         - uses: actions/checkout@v2
         - name: Set up JDK 21
           uses: actions/setup-java@v2
           with:
              java-version: '21'
              distribution: 'adopt'
              cache: maven
         - name: Build with Maven
           run: mvn --batch-mode --update-snapshots verify
```
## 📄 Challenges and Solutions

- **Database Configuration**: Used MySQL for local development and H2 for CI/CD testing.
- **Security Implementation**: Implemented JWT authentication for secure access.
- **API Design**: Designed RESTful endpoints adhering to best practices.

## 👥 Contributors

- **Mykola Chopyk**
- **Yevhen Chernonog**
- **Oleksii Haioviy**
- **Nazar Klimovych**
- **Vladyslav Vyshynskyi**

For more details, visit the [GitHub repository](https://github.com/jv-feb24-team1/car-sharing-service).

---

