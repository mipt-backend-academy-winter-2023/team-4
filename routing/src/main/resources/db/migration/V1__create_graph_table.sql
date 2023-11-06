CREATE TABLE buildings (
    id   INTEGER PRIMARY KEY,
    name VARCHAR NULL,
    lat  DECIMAL NOT NULL,
    long DECIMAL NOT NULL
);

CREATE TABLE intersections (
    id   INTEGER PRIMARY KEY,
    lat  DECIMAL NOT NULL,
    long DECIMAL NOT NULL
);

CREATE TABLE streets (
    id          SERIAL  PRIMARY KEY,
    name        VARCHAR NOT NULL,
    source      INTEGER NOT NULL,
    destination INTEGER NOT NULL
);

INSERT INTO buildings VALUES (1, 'main building', 55.929456, 37.518310);
INSERT INTO buildings VALUES (2, 'buffet', 55.930683, 37.520558);
INSERT INTO buildings VALUES (3, 'stadium', 55.927514, 37.524811);

INSERT INTO intersections VALUES (4, 55.929627, 37.521716);
INSERT INTO intersections VALUES (5, 55.928433, 37.522313);

INSERT INTO streets VALUES (1, 'institutskiy per.', 1, 2);
INSERT INTO streets VALUES (2, 'pervomaiskaya', 3, 1);
INSERT INTO streets VALUES (3, 'pervomaiskaya', 3, 2);
