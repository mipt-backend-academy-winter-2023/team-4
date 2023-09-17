CREATE TABLE vertexes
(
    id INT PRIMARY KEY,
    address VARCHAR NOT NULL
);

CREATE TABLE edges
(
    id INT PRIMARY KEY,
    name VARCHAR NOT NULL
);

INSERT INTO vertexes (id, address) VALUES (1, 'Главный корпус МФТИ');
INSERT INTO vertexes (id, address) VALUES (2, '12-ое общежитие МФТИ');
INSERT INTO vertexes (id, address) VALUES (3, '7-ое общежитие МФТИ');

INSERT INTO edges (id, name) VALUES (1, 'Институтский переулок');
INSERT INTO edges (id, name) VALUES (2, 'Первомайская улица');
