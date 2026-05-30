CREATE TABLE IF NOT EXISTS bootcamps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    description VARCHAR(90),
    launch_date DATETIME NOT NULL,
    duration INT NOT NULL
);

CREATE TABLE IF NOT EXISTS bootcamp_capacity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_bootcamp BIGINT NOT NULL,
    id_capacity BIGINT NOT NULL,
    CONSTRAINT fk_bootcamp FOREIGN KEY (id_bootcamp) REFERENCES bootcamps(id)
);
