    -- create database
    CREATE DATABASE lexilearn_backend_database;

    -- use dataase
    USE lexilearn_backend_database;
    -- Table: User
    CREATE TABLE User (
        user_id INT PRIMARY KEY AUTO_INCREMENT ,
        user_name VARCHAR(255) NOT NULL,
        user_email VARCHAR(255) NOT NULL UNIQUE,
        user_password VARCHAR(255) NOT NULL,
         user_avatar TEXT,  -- Avatar URL stored as TEXT
         user_thumbnail TEXT,  -- Thumbnail URL stored as TEXT
        user_provider  ENUM('GOOGLE', "FACEBOOK", "LOCAL"),
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
        desk_name VARCHAR(255),
        desk_is_public BOOLEAN,
        desk_owner_id INT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
             update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (desk_owner_id) REFERENCES User(user_id) ON DELETE CASCADE

    );

    -- Table: Vocab
    CREATE TABLE Vocab (
        vocab_id INT PRIMARY KEY AUTO_INCREMENT,
        vocab_language VARCHAR(255),
        vocab_meaning VARCHAR(255),
        vocab_image TEXT,
        vocab_text VARCHAR(255)
        vocab_desk_id INT,created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY(vocab_desk_id) REFERENCES Desk(desk_id)  ON DELETE CASCADE
    );

    -- Table: Vocab_Example
    CREATE TABLE Vocab_Example (
        VE_id INT PRIMARY KEY AUTO_INCREMENT,
        VE_text TEXT,
        VE_vocab_id INT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
             update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (VE_vocab_id) REFERENCES Vocab(vocab_id) ON DELETE CASCADE
    );

    CREATE INDEX idx_user_email ON User(user_email);
    -- Table: Flashcard
    CREATE TABLE Flashcard (
        flashcard_id INT PRIMARY KEY AUTO_INCREMENT,
        flashcard_front_image TEXT,
        flashcard_front_sound TEXT,
        flashcard_front_text TEXT,
        flashcard_back_image TEXT,
        flashcard_back_sound TEXT,
        flashcard_back_text TEXT
        flashcard_vocab_id INT,
        flashcard_desk_id INT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
             update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (flashcard_vocab_id) REFERENCES Vocab(vocab_id) ON DELETE CASCADE
        FOREIGN KEY(flashcard_desk_id) REFERENCES Desk(desk_id)  ON DELETE CASCADE
    );

  -- Table: Spaced_Repetition
  CREATE TABLE Spaced_Repetition (
      spaced_repetition_id INT PRIMARY KEY AUTO_INCREMENT, -- Primary key
      spaced_repetition_name ENUM('SM-0'),                -- Name of the repetition set with ENUM type
      spaced_repetition_count INT,                        -- Review count
      spaced_repetition_easiness_factor FLOAT,            -- Easiness factor (1 to 5)
      spaced_repetition_interval FLOAT,                   -- Interval in days
      spaced_repetition_next_day DATE,                    -- Next review date
      spaced_repetition_flashcard_id INT UNIQUE,          -- Reference to Flashcard
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,     -- Creation timestamp
      update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Update timestamp
      FOREIGN KEY (spaced_repetition_flashcard_id) REFERENCES Flashcard(flashcard_id) ON DELETE CASCADE -- Foreign key
  );

