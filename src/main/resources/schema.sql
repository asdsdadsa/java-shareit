CREATE TABLE IF NOT EXISTS users (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name     varchar(100) NOT NULL,
    email    varchar(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS requests (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    description     varchar(1000) NOT NULL,
    requester_id INTEGER REFERENCES users(id) ON DELETE CASCADE NOT NULL,
    created    TIMESTAMP  NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name varchar(50) NOT NULL,
    description varchar(100) NOT NULL,
    is_available BOOLEAN NOT NULL,
    owner_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    request_id INTEGER REFERENCES requests(id) ON DELETE CASCADE
);

 CREATE TABLE IF NOT EXISTS bookings (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP  NOT NULL,
    item_id INTEGER REFERENCES items(id) ON DELETE CASCADE,
    booker_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    status varchar(50) NOT NULL
);
 CREATE TABLE IF NOT EXISTS comments (
   id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
   text varchar(5000) NOT NULL,
   item_id INTEGER REFERENCES items(id) ON DELETE CASCADE,
   author_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
   created_date TIMESTAMP NOT NULL
 );