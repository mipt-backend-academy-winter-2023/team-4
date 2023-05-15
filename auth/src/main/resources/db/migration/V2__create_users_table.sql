CREATE TABLE "user"
(
    "id" SERIAL,
    "username" VARCHAR NOT NULL,
    "password" VARCHAR NOT NULL
);

INSERT INTO "user" (username, password) VALUES ('p1rattttt', '12345678');