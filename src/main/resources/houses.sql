CREATE TABLE IF NOT EXISTS `house_blocks`
(
    `id`       INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    `house_id` INTEGER NOT NULL,
    `x`        INTEGER NOT NULL,
    `y`        INTEGER NOT NULL,
    `z`        INTEGER NOT NULL,
    FOREIGN KEY (`house_id`) REFERENCES `houses` (`id`)
);

CREATE TABLE IF NOT EXISTS `houses`
(
    `id`          INTEGER     NOT NULL PRIMARY KEY AUTOINCREMENT,
    `name`        VARCHAR(50) NOT NULL UNIQUE,
    `world_uuid`  VARCHAR(36) NOT NULL,
    `owner_uuid`  VARCHAR(36),
    `buy_cost`    REAL        NOT NULL,
    `rent_cost`   REAL        NOT NULL,
    `rented_at`   TIMESTAMP,
    `rent_due`    TIMESTAMP,
    `renter_uuid` VARCHAR(36),
    `sell_cost`   REAL,
    `kicked`      BOOLEAN DEFAULT 0,
    `minX`        INTEGER     NOT NULL,
    `minY`        INTEGER     NOT NULL,
    `minZ`        INTEGER     NOT NULL,
    `maxX`        INTEGER     NOT NULL,
    `maxY`        INTEGER     NOT NULL,
    `maxZ`        INTEGER     NOT NULL
);

CREATE TABLE IF NOT EXISTS `messages`
(
    `id`      INTEGER     NOT NULL PRIMARY KEY AUTOINCREMENT,
    `uuid`    VARCHAR(36) NOT NULL,
    `message` TEXT        NOT NULL,
    `time`    TIMESTAMP   NOT NULL
);