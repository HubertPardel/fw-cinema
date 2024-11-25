CREATE TABLE IF NOT EXISTS movies (
	id serial PRIMARY KEY,
	title VARCHAR (255) NOT NULL,
	imdb_id VARCHAR (50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS showtimes (
    id serial PRIMARY KEY,
    movie_id INTEGER NOT NULL REFERENCES movies(id),
    show_date DATE NOT NULL,
    show_time TIME NOT NULL,
    price_amount DECIMAL(12,2) NOT NULL,
    price_currency VARCHAR(3) NOT NULL,
    UNIQUE(movie_id, show_date, show_time)
);

CREATE TABLE IF NOT EXISTS reviews (
    id serial PRIMARY KEY,
    movie_id INTEGER NOT NULL REFERENCES movies(id),
    user_email VARCHAR(255) NOT NULL,
    review SMALLINT NOT NULL
);