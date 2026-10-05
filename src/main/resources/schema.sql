CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name varchar(30) NOT NULL,
    email varchar(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGSERIAL PRIMARY KEY,
    description varchar(200),
    requestor_id BIGINT REFERENCES users(id) NOT NULL
    );

CREATE TABLE IF NOT EXISTS items (
    id BIGSERIAL PRIMARY KEY,
    name varchar NOT NULL,
    description varchar(200) NOT NULL,
    is_available BOOLEAN NOT NULL,
    owner_id BIGINT REFERENCES users(id) NOT NULL,
    request_id BIGINT REFERENCES requests(id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGSERIAL PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT REFERENCES items(id) NOT NULL,
    booker_id BIGINT REFERENCES users(id) NOT NULL,
    status varchar(15) NOT NULL
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    text varchar(200) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT REFERENCES items(id) NOT NULL,
    author_id BIGINT REFERENCES users(id) NOT NULL
);