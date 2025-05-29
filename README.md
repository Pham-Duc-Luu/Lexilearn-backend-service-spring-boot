# 🚀 Introduction

Welcome to the official documentation for the **Main Spring Boot Backend**, a robust backend system built using **Spring
Boot**. This guide will help you set up, operate, and extend the application effectively. You can find the source
code [here](https://github.com/Pham-Duc-Luu/Lexilearn-backend-service-spring-boot), which is currently open-source.

## 🎯 Objectives

This documentation aims to:

* Explain the overall architecture of the system
* Guide you through setting up and running the application locally
* Present the main APIs and their functionalities
* Provide resources for developing and maintaining the system

## 🛠️ Technologies Used

This application uses the following technologies and libraries:

* **Java 21** with **Spring Boot**
* **REST API** / **GraphQL**
* **PostgreSQL** / **MySQL**
* **Docker** (optional)

## 📁 Document Structure

This documentation includes:

* **Setup & Running**: Guide to setting up your environment
* **API Reference**: Detailed endpoints and data structures
* **Project Structure**: Analysis of modules and how they connect
* **Frequently Asked Questions (FAQ)**

---

💡 *If you’re new, start with the [Setup and Running](./setup) section.*

# ⚙️ Setup and Run Lexilearn Backend

The guide below will help you install and launch the **Lexilearn Backend** on your local machine.

## 📦 System Requirements

Make sure you have the following installed:

* [Java 21](https://jdk.java.net/21/)
* [Maven 3.8+](https://maven.apache.org/download.cgi)
* [Docker](https://www.docker.com/) (for database container usage)
* IDE like [IntelliJ IDEA](https://www.jetbrains.com/idea/) or [VSCode](https://code.visualstudio.com/)

## 📁 Clone the Project

```bash
git clone https://github.com/Pham-Duc-Luu/Lexilearn-backend-service-spring-boot.git
cd Lexilearn-backend-service-spring-boot
```

## Docker Compose

You can set up the environment using this Docker Compose
file: [https://github.com/Pham-Duc-Luu/Lexilearn-CICI/blob/main/docker-compose-debezium.yml](https://github.com/Pham-Duc-Luu/Lexilearn-CICI/blob/main/docker-compose-debezium.yml)

## Install MySQL

The main Spring Boot project uses MySQL as the primary database, so you need to install MySQL during development.

You can install MySQL 8.0 from the [official website](https://dev.mysql.com/downloads/)

Or use Docker Compose:

```yml title="Set-up-mysql.yml"
services:
  mysql-db:
    image: mysql:8.0
    container_name: mysql-container
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: Phamluu2003.
      MYSQL_DATABASE: lexilearn
      MYSQL_USER: user_1
      MYSQL_PASSWORD: user_1
    ports:
      - '3306:3306'
    networks:
      - debezium-server
    volumes:
      - mysql-data:/var/lib/mysql
      - ./mysql-custom.cnf:/etc/mysql/conf.d/mysql-custom.cnf
```

```cnf title="mysql-custom.cnf"
server-id         = 223344
log_bin           = mysql-bin
binlog_format     = ROW
binlog_row_image  = FULL
binlog_expire_logs_seconds  = 864000
enforce_gtid_consistency = ON
gtid_mode = ON
binlog_rows_query_log_events=ON
```

This will enable MySQL logging for Change Data Capture. Learn more [here](Workflow/Data-capture)

```bash
sudo docker compose up -f Set-up-mysql.yml
```

## Install Elasticsearch

Install Elasticsearch from the [official site](https://www.elastic.co/downloads/elasticsearch)

Or use Docker Compose:

```yml title="Set-up-Es.yml"
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:7.10.0
    container_name: elasticsearch-container
    environment:
      - discovery.type=single-node
      - bootstrap.memory_lock=true
      - ES_JAVA_OPTS=-Xms1g -Xmx1g
    ulimits:
      memlock:
        soft: -1
        hard: -1
    ports:
      - '9200:9200'
      - '9300:9300'
    volumes:
      - es-data:/usr/share/elasticsearch/data
```

```bash
sudo docker compose up -f Set-up-Es.yml
```

## Install Zookeeper, Kafka and Debezium

```yml title="Set-up-Kafka.yml"
zookeeper:
  image: quay.io/debezium/zookeeper:3.0
  container_name: zookeeper
  ports:
    - 2181:2181
    - 2888:2888
    - 3888:3888
  networks:
    - debezium-server

kafka:
  image: quay.io/debezium/kafka:3.0
  container_name: kafka
  ports:
    - 9092:9092
  depends_on:
    - zookeeper
  environment:
    - ZOOKEEPER_CONNECT=zookeeper:2181
  networks:
    - debezium-server

kafka-ui:
  image: provectuslabs/kafka-ui:latest
  container_name: kafka-ui
  environment:
    DYNAMIC_CONFIG_ENABLED: true
    KAFKA_CLUSTERS_0_NAME: local
    KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:9092
  ports:
    - 9089:8080
  depends_on:
    - kafka
  networks:
    - debezium-server

connect-mysql:
  image: quay.io/debezium/connect:3.0
  container_name: connect-mysql
  ports:
    - 8083:8083
  depends_on:
    - kafka
  networks:
    - debezium-server
  environment:
    - BOOTSTRAP_SERVERS=kafka:9092
    - GROUP_ID=1
    - CONFIG_STORAGE_TOPIC=connect_mysql_configs
    - OFFSET_STORAGE_TOPIC=connect_mysql_offsets
    - STATUS_STORAGE_TOPIC=connect_mysql_statuses
    - REST_HOST_NAME=0.0.0.0

networks:
  debezium-server:
    external: true
```

```bash
sudo docker compose up -f Set-up-Kafka.yml
```

(...additional schema and setup information can be appended...)
