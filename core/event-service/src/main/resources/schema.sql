CREATE TABLE IF NOT EXISTS category
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(50),
    CONSTRAINT pk_category PRIMARY KEY (id),
    CONSTRAINT category_name_unique UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS event (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    category_id BIGINT NOT NULL,
    initiator     BIGINT NOT NULL,
    loc_lat DOUBLE PRECISION NOT NULL,
    loc_lon DOUBLE PRECISION NOT NULL,
    title VARCHAR(120),
    annotation Text,
    description Text,
    confirmed_requests INTEGER NOT NULL,
    participant_limit INTEGER NOT NULL,
    request_moderation BOOLEAN NOT NULL,
    paid BOOLEAN NOT NULL,
    created_on TIMESTAMP WITHOUT TIME ZONE,
    event_date TIMESTAMP WITHOUT TIME ZONE,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    state VARCHAR(255),
    views BIGINT[],
    CONSTRAINT pk_event PRIMARY KEY (id),
    CONSTRAINT fk_event_on_category FOREIGN KEY (category_id) REFERENCES category (id)
);


CREATE TABLE IF NOT EXISTS compilation
(
    id       bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned   BOOLEAN                                 NOT NULL,
    title    VARCHAR(50)                             NOT NULL,
    CONSTRAINT pk_compilations PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS compilation_events
(
    compilation_id BIGINT REFERENCES compilation (id),
    events_id      BIGINT REFERENCES event (id)
);
