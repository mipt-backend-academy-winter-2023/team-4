CREATE TABLE nodes (
   id INT PRIMARY KEY,
   name VARCHAR(256),
   lat FLOAT NOT NULL,
   lon FLOAT NOT NULL
);

INSERT INTO nodes (id, name, lat, lon) VALUES
(5, "Belka", 50.1, 69.7),
(10, "Savelka", 100.3, 99.2),
(120, "Dolgopa", 2.6, 3.8);