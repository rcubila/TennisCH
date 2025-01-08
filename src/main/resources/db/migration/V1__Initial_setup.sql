CREATE TABLE IF NOT EXISTS testing1 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    profile_picture_url VARCHAR(255),
    registration_date TIMESTAMP,
    last_login TIMESTAMP,
    role VARCHAR(50)
);
