# Smart Home Server

## Overview

The Smart Home Server is a Spring Boot application designed to manage and control smart home devices. It provides RESTful APIs for interacting with home devices, managing user authentication, and handling MQTT communication for real-time device control.

## Features

- User authentication and authorization using JWT tokens
- Home and device management
- Real-time device control via MQTT
- Configuration options for MongoDB and MQTT
- Virtual threads support for improved concurrency

## Technologies Used

- Java
- Spring Boot
- Spring Data MongoDB
- MQTT
- JWT
- Maven

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6.0 or higher
- MongoDB instance
- MQTT broker

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/HiepTonight/smarthome-server.git
    cd smarthome-server
    ```

2. Configure the application:
    - Update the `src/main/resources/application.yml` file with your MongoDB and MQTT broker details.

3. Build the project:
    ```sh
    mvn clean install
    ```

4. Run the application:
    ```sh
    mvn spring-boot:run
    ```

### Configuration

The application can be configured using the `application.yml` file. Below are the key configuration options:

```yaml
server:
  port: 8080
  servlet:
    context-path: /api/v1

spring:
  threads:
    virtual:
      enabled: true
  application:
    name: smarthome-server
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb+srv://<username>:<password>@<cluster-url>/<database>?retryWrites=true&w=majority}
      database: ${MONGODB_DATABASE:smarthome-iot}

mqtt:
  host: ${MQTT_HOST:<mqtt-host>}
  port: 8883
  username: ${MQTT_USERNAME:<mqtt-username>}
  password: ${MQTT_PASSWORD:<mqtt-password>}

jwt:
  secret: ${JWT_SECRET:<your-jwt-secret>}
  expire: ${JWT_EXPIRE:1209600000}

socket-server:
  port: 8081
  host: 0.0.0.0
  ```
### Usage

#### API Endpoints

- **User Authentication**
    - `POST /api/v1/auth/login` - User login
    - `POST /api/v1/auth/register` - User registration

- **Home Management**
    - `GET /api/v1/homes` - Get all homes for the logged-in user
    - `GET /api/v1/homes/{id}` - Get details of a specific home
    - `POST /api/v1/homes` - Create a new home
    - `PUT /api/v1/homes/{id}` - Update an existing home
    - `DELETE /api/v1/homes/{id}` - Delete a home

- **Device Management**
    - `GET /api/v1/devices` - Get all devices for the logged-in user
    - `GET /api/v1/devices/{id}` - Get details of a specific device
    - `POST /api/v1/devices` - Create a new device
    - `PUT /api/v1/devices/{id}` - Update an existing device
    - `DELETE /api/v1/devices/{id}` - Delete a device

### MQTT Topics

- `homePod/{homePodId}/controlDevice` - Topic for controlling devices in a home
- `homePod/{homePodId}/faceRecognize` - Topic for face recognition events


### Contributing

Contributions are welcome! Please fork the repository and create a pull request with your changes.

### License

This project is licensed under the MIT License. See the `LICENSE` file for details.

### Contact

For any questions or support, please contact [HiepTonight](https://github.com/HiepTonight).
```

This `README.md` file provides a comprehensive overview of your project, including installation instructions, configuration details, usage examples, and more.
  
  