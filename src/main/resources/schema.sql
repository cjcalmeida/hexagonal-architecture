CREATE TABLE Game(
    id NUMERIC NOT NULL PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(15) NOT NULL UNIQUE,
    description VARCHAR(255),
    release_date DATE NOT NULL,
    creation_date TIMESTAMP
);