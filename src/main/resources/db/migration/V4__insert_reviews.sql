ALTER TABLE reviews
DROP COLUMN review;

ALTER TABLE reviews
ADD COLUMN rate VARCHAR(20) NOT NULL;

ALTER TABLE reviews
ADD CONSTRAINT UQ_movie_id_user_email UNIQUE(movie_id, user_email);

INSERT INTO reviews(movie_id, user_email, rate) VALUES (1, 'hubert.pardel@gmail.com', 'VERY_GOOD');
INSERT INTO reviews(movie_id, user_email, rate) VALUES (2, 'hubert.pardel@gmail.com', 'GOOD');
INSERT INTO reviews(movie_id, user_email, rate) VALUES (3, 'hubert.pardel@gmail.com', 'AVERAGE');
INSERT INTO reviews(movie_id, user_email, rate) VALUES (2, 'pardel.hubert@gmail.com', 'VERY_GOOD');
INSERT INTO reviews(movie_id, user_email, rate) VALUES (3, 'pardel.hubert@gmail.com', 'BAD');
INSERT INTO reviews(movie_id, user_email, rate) VALUES (9, 'pardel.hubert@gmail.com', 'VERY_BAD');