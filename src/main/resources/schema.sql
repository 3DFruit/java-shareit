CREATE TABLE IF NOT EXISTS users
(
    user_id   bigint       NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    email     varchar(150) NOT NULL,
    user_name varchar(200) NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (user_id),
    CONSTRAINT unique_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    item_id      bigint       NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    item_name    varchar(100) NOT NULL,
    description  varchar(200) NOT NULL,
    is_available boolean      NOT NULL,
    user_id      bigint,
    request_id   bigint,
    CONSTRAINT items_pkey PRIMARY KEY (item_id),
    CONSTRAINT fk_item_owner
        FOREIGN KEY (user_id)
            REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id bigint      NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    item_id    bigint      NOT NULL,
    user_id    bigint      NOT NULL,
    status     varchar(10) NOT NULL,
    start_time timestamp   NOT NULL,
    end_time   timestamp   NOT NULL,
    CONSTRAINT bookings_pkey PRIMARY KEY (booking_id),
    CONSTRAINT check_dates check (start_time < end_time),
    CONSTRAINT fk_booking_item
        FOREIGN KEY (item_id)
            REFERENCES items (item_id),
    CONSTRAINT fk_booking_booker
        FOREIGN KEY (user_id)
            REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS requests
(
    request_id  bigint       NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    description varchar(400) NOT NULL,
    user_id     bigint       NOT NULL,
    created     timestamp    NOT NULL,
    CONSTRAINT requests_pkey PRIMARY KEY (request_id),
    CONSTRAINT fk_request_requester
        FOREIGN KEY (user_id)
            REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id   bigint       NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    item_id      bigint       NOT NULL,
    author_id    bigint       NOT NULL,
    comment_text varchar(200) NOT NULL,
    created      timestamp    NOT NULL,
    CONSTRAINT comments_pkey PRIMARY KEY (comment_id),
    CONSTRAINT fk_comment_item
        FOREIGN KEY (item_id)
            REFERENCES items (item_id),
    CONSTRAINT fk_comment_author
        FOREIGN KEY (author_id)
            REFERENCES users (user_id)
);