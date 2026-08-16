# IoT Monitoring System

We are building an **IoT monitoring system** to collect, process, and analyze device data.  
The project has already been started, and we are looking for it to be completed.

---

## 📋 Project Tasks

A detailed project description and the remaining tasks can be found in the [TASKS.md](./TASKS.md) file located in the root directory.

---

## 🚀 Assignment Instructions

1. Clone this repository
2. Review the [TASKS.md](./TASKS.md) file.
3. Complete the remaining implementation tasks.
4. Ensure the project is working end-to-end.
5. Upload the finished project into your own **GitHub repository**.
5. Share the repository link with us.

---

## ⏰ Timeline

Please complete the assignment **within one week**.

---

## 📂 Project Structure (expected)

## 🏗️ Build

You can build the project and execute the unit tests with: 

`mvnw clean package`

You can build the project, and execute the unit **AND** integration tests with:

`mvnw clean verify`

If you would like to build an Eclipse-Temurin JRE21 based docker image you can do it with:

`mvnw clean verify -PbuildDockerImage`

## 🐳 Start local Docker environment

You can create a local docker environment with Docker Compose. Use the following command:

`docker compose up -d`

This will create a postgres container, a kafka container (with initialized topics), and the IoT monitoring app 
container in a common docker network. The app is accessible via the 8080 port, the database on the 5432 port, and the
kafka broker is accessible on the 9092 port.
If the database access and the kafka access from the host is not needed, the port bindings can be removed.