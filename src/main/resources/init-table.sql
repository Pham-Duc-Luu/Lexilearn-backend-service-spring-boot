-- create database
CREATE DATABASE lexilearn_backend_database;

-- use dataase
USE lexilearn_backend_database;
-- Table: User
CREATE TABLE User (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(255) NOT NULL,
    user_email VARCHAR(255) NOT NULL,
    user_password VARCHAR(255) NOT NULL,
    user_provider  ENUM('GOOGLE', "FACEBOOK"),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table: User_Token
CREATE TABLE User_Token (
    UT_id INT PRIMARY KEY AUTO_INCREMENT,
    UT_type VARCHAR(255),
    UT_expired_at TIMESTAMP,
    UT_text TEXT,
    UT_user_id INT,
    FOREIGN KEY (UT_user_id) REFERENCES User(user_id) ON DELETE CASCADE
);

-- Table: Desk
CREATE TABLE Desk (
    desk_id INT PRIMARY KEY AUTO_INCREMENT,
    desk_description TEXT,
    desk_thumbnail TEXT,
    desk_icon TEXT,
    desk_is_public BOOLEAN,
    desk_owner_id INT,
    FOREIGN KEY (desk_owner_id) REFERENCES User(user_id) ON DELETE CASCADE
);

-- Table: Vocab
CREATE TABLE Vocab (
    vocab_id INT PRIMARY KEY AUTO_INCREMENT,
    vocab_language VARCHAR(255),
    vocab_meaning VARCHAR(255),
    vocab_image TEXT
);

-- Table: Flashcard
CREATE TABLE Flashcard (
    flashcard_id INT PRIMARY KEY AUTO_INCREMENT,
    flashcard_front_image TEXT,
    flashcard_front_sound TEXT,
    flashcard_front_text TEXT,
    flashcard_back_image TEXT,
    flashcard_back_sound TEXT,
    flashcard_back_text TEXT
);

-- Table: Desk_Vocab_Flashcard
CREATE TABLE Desk_Vocab_Flashcard (
    DVF_desk_id INT,
    DVF_vocab_id INT,
    DVF_flashcard_id INT,
    FOREIGN KEY (DVF_desk_id) REFERENCES Desk(desk_id) ON DELETE CASCADE,
    FOREIGN KEY (DVF_vocab_id) REFERENCES Vocab(vocab_id) ON DELETE CASCADE,
    FOREIGN KEY (DVF_flashcard_id) REFERENCES Flashcard(flashcard_id) ON DELETE CASCADE,
    PRIMARY KEY (DVF_desk_id, DVF_vocab_id, DVF_flashcard_id)
);
