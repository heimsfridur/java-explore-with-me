DROP TABLE IF EXISTS users cascade;
DROP TABLE IF EXISTS categories cascade;
DROP TABLE IF EXISTS locations cascade;
DROP TABLE IF EXISTS events cascade;
DROP TABLE IF EXISTS requests cascade;
DROP TABLE IF EXISTS compilations cascade;
DROP TABLE IF EXISTS compilations_events cascade;
DROP TABLE IF EXISTS comments cascade;


CREATE TABLE users (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE categories (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS locations (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL
);

CREATE TABLE events (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    annotation VARCHAR(2000) NOT NULL,
    category_id INTEGER NOT NULL,
    confirmed_requests INTEGER,
    created_on TIMESTAMP NOT NULL,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    initiator_id INTEGER NOT NULL,
    location_id INTEGER,
    paid BOOLEAN DEFAULT false,
    participant_limit INTEGER DEFAULT 0,
    published_on TIMESTAMP,
    request_moderation BOOLEAN DEFAULT TRUE,
    state VARCHAR(200) NOT NULL,
    title VARCHAR(120) NOT NULL,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_initiator FOREIGN KEY (initiator_id) REFERENCES users (id),
    CONSTRAINT fk_location FOREIGN KEY (location_id) REFERENCES locations (id)
);

CREATE TABLE requests (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    created TIMESTAMP NOT NULL,
    event_id INTEGER NOT NULL,
    requester_id INTEGER NOT NULL,
    status VARCHAR(100) NOT NULL,
    CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_requester FOREIGN KEY (requester_id) REFERENCES users (id)
);

CREATE TABLE compilations (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    pinned BOOLEAN NOT NULL,
    title VARCHAR(100) NOT NULL
);

CREATE TABLE compilations_events (
    compilation_id INT NOT NULL,
    event_id INT NOT NULL,
    FOREIGN KEY (compilation_id) REFERENCES compilations(id),
    FOREIGN KEY (event_id) REFERENCES events(id),
    PRIMARY KEY(compilation_id, event_id)
);

CREATE TABLE comments (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    event_id INTEGER NOT NULL,
    author_id INTEGER NOT NULL,
    text VARCHAR(255) NOT NULL,
    created TIMESTAMP NOT NULL,
    last_update TIMESTAMP,
    CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_user FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
)