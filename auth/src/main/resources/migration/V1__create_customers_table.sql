CREATE TABLE "user"
(
    "id"         SERIAL,
    "login"     VARCHAR NOT NULL,
    "pwdhash"  VARCHAR NOT NULL
);

INSERT INTO "user" (login, pwdhash)
VALUES  ('login1', 'hshpwd1'),
        ('login2', 'hshpwd2');