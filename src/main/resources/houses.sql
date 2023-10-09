CREATE TABLE IF NOT EXISTS `rent`
(
    `house_id`    INTEGER NOT NULL PRIMARY KEY,
    `renter_uuid` VARCHAR(36),
    `rent_cost`   REAL    NOT NULL,
    `rented_at`   DATETIME,
    `days_rented` INTEGER NOT NULL,
    `renewable`   BOOLEAN NOT NULL,
    FOREIGN KEY (`house_id`) REFERENCES `houses` (`id`)
);

CREATE TABLE IF NOT EXISTS `house_blocks`
(
    `id`       INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    `house_id` INTEGER NOT NULL ,
    `x`        INTEGER NOT NULL,
    `y`        INTEGER NOT NULL,
    `z`        INTEGER NOT NULL,
    FOREIGN KEY (`house_id`) REFERENCES `houses` (`id`)
);

CREATE TABLE IF NOT EXISTS `houses`
(
    `id`             INTEGER     NOT NULL PRIMARY KEY AUTOINCREMENT,
    `name`           VARCHAR(50) NOT NULL UNIQUE,
    `world_uuid`     VARCHAR(36) NOT NULL,
    `owner_uuid`     VARCHAR(36),
    `base_buy_cost`  REAL        NOT NULL,
    `base_rent_cost` REAL        NOT NULL,
    `sell_cost`      REAL        NOT NULL,
    `minX`           INTEGER     NOT NULL,
    `minY`           INTEGER     NOT NULL,
    `minZ`           INTEGER     NOT NULL,
    `maxX`           INTEGER     NOT NULL,
    `maxY`           INTEGER     NOT NULL,
    `maxZ`           INTEGER     NOT NULL
);