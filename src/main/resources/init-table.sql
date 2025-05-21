    -- create database
    CREATE DATABASE lexilearn_backend_database;

    -- use dataase
    USE lexilearn_backend_database;

    -- Table: User
    CREATE TABLE User (
        user_id INT PRIMARY KEY AUTO_INCREMENT ,
        user_name VARCHAR(255) NOT NULL,
        user_uuid CHAR(16) NOT NULL UNIQUE DEFAULT (UUID()), -- Add UUID
        user_email VARCHAR(255) NOT NULL UNIQUE,
        user_password VARCHAR(255) NOT NULL,
        user_avatar TEXT,  -- Avatar URL stored as TEXT
        user_thumbnail TEXT,  -- Thumbnail URL stored as TEXT
        user_provider  ENUM('GOOGLE', "FACEBOOK", "LOCAL"),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

    CREATE TABLE UserAvatar (
        user_avatar_id INT PRIMARY KEY AUTO_INCREMENT,
        user_id INT NOT NULL UNIQUE, -- Foreign key to associate with a user
        sex ENUM('man', 'woman') NOT NULL DEFAULT 'man',
        face_color VARCHAR(7) NOT NULL DEFAULT '#ffffff', -- Default skin tone
        ear_size ENUM('small', 'big') NOT NULL DEFAULT 'small',
        hair_color VARCHAR(7) NOT NULL DEFAULT '#ffffff', -- Default hair color (black)
        hair_style ENUM('normal', 'thick', 'mohawk', 'womanLong', 'womanShort') NOT NULL DEFAULT 'normal',
        hat_color VARCHAR(7) NOT NULL DEFAULT '#ffffff', -- Default hat color (black)
        hat_style ENUM('beanie', 'turban', 'none') NOT NULL DEFAULT 'none',
        eye_brow ENUM('up', 'upWoman') NOT NULL DEFAULT 'up',
        eye_style ENUM('circle', 'oval', 'smile') NOT NULL DEFAULT 'circle',
        glasses_style ENUM('round', 'square', 'none') NOT NULL DEFAULT 'none',
        nose_style ENUM('short', 'long', 'round') NOT NULL DEFAULT 'short',
        mouth_style ENUM('laugh', 'smile', 'peace') NOT NULL DEFAULT 'smile',
        shirt_style ENUM('hoody', 'short', 'polo') NOT NULL DEFAULT 'short',
        shirt_color VARCHAR(7)  NOT NULL DEFAULT '#ffffff', -- Default shirt color
        bg_color VARCHAR(7) NOT NULL DEFAULT '#ffffff', -- Default background color
        gradient_bg_color TEXT, -- Default gradient
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        user_avatar_url TEXT,
        FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE
    );



    -- Table: User_Token
    CREATE TABLE User_Token (
        UT_id INT PRIMARY KEY AUTO_INCREMENT,
        UT_type ENUM('REFRESH_TOKEN','OTP'),
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
        desk_status ENUM("PUBLISHED", "DRAFTED", "BIN"),
        desk_name VARCHAR(255),
        desk_is_public BOOLEAN,
        desk_owner_id INT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (desk_owner_id) REFERENCES User(user_id) ON DELETE CASCADE
    );

    -- Table: Vocab
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
        flashcard_back_text TEXT,
        flashcard_vocab_id INT NULL,
        flashcard_desk_id INT NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        flashcard_desk_position  INT NOT NULL DEFAULT 1,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (flashcard_vocab_id) REFERENCES Vocab(vocab_id) ON DELETE CASCADE,
        FOREIGN KEY (flashcard_desk_id) REFERENCES Desk(desk_id) ON DELETE CASCADE,
    );

  -- Table: Spaced_Repetition
  CREATE TABLE Spaced_Repetition (
      spaced_repetition_id INT PRIMARY KEY AUTO_INCREMENT, -- Primary key
      spaced_repetition_name ENUM('SM-2'),                -- Name of the repetition set with ENUM type
      spaced_repetition_count INT,                        -- Review count
      spaced_repetition_easiness_factor FLOAT,            -- Easiness factor (1 to 5)
      spaced_repetition_interval FLOAT,                   -- Interval in days
      spaced_repetition_next_day DATE,                    -- Next review date
      spaced_repetition_flashcard_id INT UNIQUE,          -- Reference to Flashcard
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,     -- Creation timestamp
      update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Update timestamp
      FOREIGN KEY (spaced_repetition_flashcard_id) REFERENCES Flashcard(flashcard_id) ON DELETE CASCADE -- Foreign key
  );



    ALTER TABLE Flashcard ADD CONSTRAINT unique_flashcard_position UNIQUE (flashcard_desk_id, flashcard_desk_position);
