CREATE TABLE edges (
    id INT PRIMARY KEY,
    name VARCHAR(256),
    firstNode INT NOT NULL,
    secondNode INT NOT NULL
);