# 🚀 Giới thiệu

Chào mừng bạn đến với tài liệu chính thức cho **Main spring boot backend**, một hệ thống backend mạnh mẽ được xây dựng bằng **Spring Boot**. Tài liệu này sẽ hướng dẫn bạn cách thiết lập, vận hành và mở rộng ứng dụng một cách hiệu quả.
Bạn có thể tìm thấy source code [tại đây](https://github.com/Pham-Duc-Luu/Lexilearn-backend-service-spring-boot). Hiện tại đang là open source.

## 🎯 Mục tiêu

Tài liệu này nhằm:

- Giải thích kiến trúc tổng thể của hệ thống
- Hướng dẫn cách cài đặt và chạy ứng dụng cục bộ
- Trình bày các API và chức năng chính
- Cung cấp tài nguyên để phát triển và bảo trì hệ thống

## 🛠️ Công nghệ sử dụng

Ứng dụng này sử dụng các công nghệ và thư viện sau:

- **Java 21** với **Spring Boot**
- **REST API** / **GraphQL**
- **PostgreSQL** / **MySQL**
- **Docker** (tuỳ chọn)

## 📁 Cấu trúc tài liệu

Tài liệu này bao gồm:

- **Cài đặt & Khởi chạy**: Hướng dẫn thiết lập môi trường
- **API Reference**: Chi tiết các endpoint và cấu trúc dữ liệu
- **Cấu trúc dự án**: Phân tích các module và cách chúng kết nối
- **Câu hỏi thường gặp (FAQ)**

---

💡 _Nếu bạn là người mới, hãy bắt đầu với phần [Cài đặt và Khởi chạy](./setup)._

# ⚙️ Cài đặt và Khởi chạy Lexilearn Backend

Hướng dẫn dưới đây sẽ giúp bạn cài đặt và khởi động ứng dụng **Lexilearn Backend** trên máy tính của mình.

## 📦 Yêu cầu hệ thống

Trước khi bắt đầu, hãy đảm bảo rằng bạn đã cài:

- [Java 21](https://jdk.java.net/21/)
- [Maven 3.8+](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com/) (nếu sử dụng database container)
- IDE như [IntelliJ IDEA](https://www.jetbrains.com/idea/) hoặc [VSCode](https://code.visualstudio.com/)

## 📁 Clone dự án

```bash
git clone https://github.com/Pham-Duc-Luu/Lexilearn-backend-service-spring-boot.git
cd Lexilearn-backend-service-spring-boot
```

## Docker compose

bạn có thể set up bằng file docker compose https://github.com/Pham-Duc-Luu/Lexilearn-CICI/blob/main/docker-compose-debezium.yml

## Cài đặt mysql

Main spring boot dùng mysql như là cở sở dữ liệu chính của ứng dụng, vậy nên bạn cần cài đặt mysql trong quán trình phát triển.

Bạn có thể cài đặt mysql 8:0 ở trang web chính thức [ở đây](https://dev.mysql.com/downloads/)

hoặc dùng docker-compose :

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
      - ./mysql-custom.cnf:/etc/mysql/conf.d/mysql-custom.cnf # <-- mount your config file
```

```cnf title="mysql-custom.cnf"
server-id         = 223344 # Querying variable is called server_id, e.g. SELECT variable_value FROM information_schema.global_variables WHERE variable_name='server_id';
log_bin                     = mysql-bin
binlog_format               = ROW
binlog_row_image            = FULL
binlog_expire_logs_seconds  = 864000
enforce_gtid_consistency = ON
gtid_mode = ON
binlog_rows_query_log_events=ON
```

Cài này sẽ bật log của mysql để có thể thực hiện Change data capture, bạn có thể tìm hiểu thêm [ở đây](Workflow/Data-capture)

```bash
sudo docker compose up -f Set-up-mysql.yml
```

## Cài đặt Elasticsearch

Bạn có thể cài đặt Elasticsearch ở [trang web chính thức ](https://www.elastic.co/downloads/elasticsearch)

Hoặc dùng cài đặt với docker với file docker-compose dưới đây.

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

## Cài đặt Zookeeper, kafka và Debezium

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
    external: true # Ensure all containers are in the same network

```

```bash
sudo docker compose up -f Set-up-Kafka.yml
```

## 🛠️ Cài đặt & Cấu hình MySQL

### ✅ 1. Tạo Database

```sql
CREATE DATABASE lexilearn_backend_database;
USE lexilearn_backend_database;
```

### 👤 2. Bảng User

```sql
CREATE TABLE User (
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  user_name VARCHAR(255) NOT NULL,
  user_uuid CHAR(16) NOT NULL UNIQUE DEFAULT (UUID()),
  user_email VARCHAR(255) NOT NULL UNIQUE,
  user_password VARCHAR(255) NOT NULL,
  user_avatar TEXT,
  user_thumbnail TEXT,
  user_provider ENUM('GOOGLE', 'FACEBOOK', 'LOCAL'),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 🧑‍🎨 3. Bảng UserAvatar

```sql
CREATE TABLE UserAvatar (
  user_avatar_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL UNIQUE,
  sex ENUM('man', 'woman') NOT NULL DEFAULT 'man',
  face_color VARCHAR(7) NOT NULL DEFAULT '#ffffff',
  ear_size ENUM('small', 'big') NOT NULL DEFAULT 'small',
  hair_color VARCHAR(7) NOT NULL DEFAULT '#ffffff',
  hair_style ENUM('normal', 'thick', 'mohawk', 'womanLong', 'womanShort') NOT NULL DEFAULT 'normal',
  hat_color VARCHAR(7) NOT NULL DEFAULT '#ffffff',
  hat_style ENUM('beanie', 'turban', 'none') NOT NULL DEFAULT 'none',
  eye_brow ENUM('up', 'upWoman') NOT NULL DEFAULT 'up',
  eye_style ENUM('circle', 'oval', 'smile') NOT NULL DEFAULT 'circle',
  glasses_style ENUM('round', 'square', 'none') NOT NULL DEFAULT 'none',
  nose_style ENUM('short', 'long', 'round') NOT NULL DEFAULT 'short',
  mouth_style ENUM('laugh', 'smile', 'peace') NOT NULL DEFAULT 'smile',
  shirt_style ENUM('hoody', 'short', 'polo') NOT NULL DEFAULT 'short',
  shirt_color VARCHAR(7) NOT NULL DEFAULT '#ffffff',
  bg_color VARCHAR(7) NOT NULL DEFAULT '#ffffff',
  gradient_bg_color TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  user_avatar_url TEXT,
  FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE
);

```

### 🔐 4. Bảng User_Token

```sql
CREATE TABLE User_Token (
  UT_id INT PRIMARY KEY AUTO_INCREMENT,
  UT_type ENUM('REFRESH_TOKEN', 'OTP'),
  UT_expired_at TIMESTAMP,
  UT_text TEXT,
  UT_user_id INT,
  FOREIGN KEY (UT_user_id) REFERENCES User(user_id) ON DELETE CASCADE
);
```

### 🗂️ 5. Bảng Desk

```sql
CREATE TABLE Desk (
  desk_id INT PRIMARY KEY AUTO_INCREMENT,
  desk_description TEXT,
  desk_thumbnail TEXT,
  desk_icon TEXT,
  desk_status ENUM('PUBLISHED', 'DRAFTED', 'BIN'),
  desk_name VARCHAR(255),
  desk_is_public BOOLEAN,
  desk_owner_id INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (desk_owner_id) REFERENCES User(user_id) ON DELETE CASCADE
);

```

### 📚 6. Bảng Vocab và Vocab_Example

```sql
CREATE TABLE Vocab (
  vocab_id INT PRIMARY KEY AUTO_INCREMENT,
  vocab_language VARCHAR(255),
  vocab_meaning VARCHAR(255),
  vocab_image TEXT,
  vocab_text VARCHAR(255),
  vocab_desk_id INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY(vocab_desk_id) REFERENCES Desk(desk_id) ON DELETE CASCADE
);

CREATE TABLE Vocab_Example (
  VE_id INT PRIMARY KEY AUTO_INCREMENT,
  VE_text TEXT,
  VE_vocab_id INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (VE_vocab_id) REFERENCES Vocab(vocab_id) ON DELETE CASCADE
);

CREATE INDEX idx_user_email ON User(user_email);

```

### 🃏 7. Bảng Flashcard

```sql
CREATE TABLE Flashcard (
  flashcard_id INT PRIMARY KEY AUTO_INCREMENT,
  flashcard_front_image TEXT,
  flashcard_front_sound TEXT,
  flashcard_front_text TEXT,
  flashcard_back_image TEXT,
  flashcard_back_sound TEXT,
  flashcard_back_text TEXT,
  flashcard_vocab_id INT NULL,
  flashcard_desk_id INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  flashcard_desk_position INT NOT NULL DEFAULT 1,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (flashcard_vocab_id) REFERENCES Vocab(vocab_id) ON DELETE CASCADE,
  FOREIGN KEY (flashcard_desk_id) REFERENCES Desk(desk_id) ON DELETE CASCADE
);

ALTER TABLE Flashcard ADD CONSTRAINT unique_flashcard_position UNIQUE (flashcard_desk_id, flashcard_desk_position);

```

⏳ 8. Bảng Spaced_Repetition

```sql
CREATE TABLE Spaced_Repetition (
  spaced_repetition_id INT PRIMARY KEY AUTO_INCREMENT,
  spaced_repetition_name ENUM('SM-2'),
  spaced_repetition_count INT,
  spaced_repetition_easiness_factor FLOAT,
  spaced_repetition_interval FLOAT,
  spaced_repetition_next_day DATE,
  spaced_repetition_flashcard_id INT UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (spaced_repetition_flashcard_id) REFERENCES Flashcard(flashcard_id) ON DELETE CASCADE
);

```

import DataVisualizer from '@site/src/components/docs/database-visualizer/index.tsx';

<DataVisualizer />;

## ⚙️ Cấu hình môi trường

Trước khi có thể cài đặt được dự án, cần cấu hình biến môi trường cho ứng dụng.

### tạo một file .env.local ở thư mục root

```bash
touch .env.local
```

những thông tin tiếp theo là cực kì nhạy cảm, liên lạc với leader để có những biến môi trường

```bash
PORT=
API_KEY=
...
```

```bash
export $(cat local.env | xargs)
```

## Cài đặt ứng dụng

di chuyển đến thư mục root, và cài đặt

```bash
mvn clean install
```

Lệnh này sẽ:

- **Xoá thư mục `target`** (nếu có) để đảm bảo rằng mọi tệp cũ được làm sạch.
- **Tải về các phụ thuộc** được khai báo trong file `pom.xml`.
- **Build và đóng gói ứng dụng** thành file `.jar`.

:::warning[Cấu hình và sử dụng jOOQ với MySQL]

như các bạn có thể thấy, **jooq** sẽ cần truy cập đến **mysql database** để lấy các schema và tự động tạo các ORM class, vậy nên các bạn cần setup database trước khi có thể cài đặt dự án
:::

## Các bước chạy ứng dụng

Sau khi cài đặt xong các phụ thuộc và cấu hình cơ sở dữ liệu, bạn có thể chạy ứng dụng bằng lệnh sau:

```bash
mvn spring-boot:run
```

Lệnh này sẽ khởi động ứng dụng Spring Boot và bạn có thể truy cập vào ứng dụng qua `http://localhost:8080` (hoặc cổng mà bạn đã cấu hình).

## Các bước tiếp theo

- **Tùy chỉnh cấu hình**: Bạn có thể điều chỉnh các file cấu hình như `application.properties` hoặc `application.yml` để phù hợp với môi trường sản xuất.
- **Triển khai lên môi trường sản xuất**: Sau khi ứng dụng chạy ổn định, bạn có thể triển khai lên các môi trường như AWS, Google Cloud, hoặc máy chủ của bạn.
