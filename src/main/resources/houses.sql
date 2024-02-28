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
    `id`         INTEGER     NOT NULL PRIMARY KEY AUTOINCREMENT,
    `name`       VARCHAR(50) NOT NULL UNIQUE,
    `world_uuid` VARCHAR(36) NOT NULL,
    `owner_uuid` VARCHAR(36),
    `buy_cost`   REAL        NOT NULL,
    `rent_cost`  REAL        NOT NULL,
    `rented_at`  TIMESTAMP,
    `rent_due`   TIMESTAMP,
    `sell_cost`  REAL,
    `minX`       INTEGER     NOT NULL,
    `minY`       INTEGER     NOT NULL,
    `minZ`       INTEGER     NOT NULL,
    `maxX`       INTEGER     NOT NULL,
    `maxY`       INTEGER     NOT NULL,
    `maxZ`       INTEGER     NOT NULL
);