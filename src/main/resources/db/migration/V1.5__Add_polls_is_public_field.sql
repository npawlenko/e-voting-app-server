DELETE FROM polls;

ALTER TABLE polls
ADD COLUMN is_public BOOLEAN NOT NULL;

INSERT INTO polls (question, closes_at, creator_id, is_public) VALUES
('Favorite Color?', '2023-12-31 23:59:59', 1, TRUE),
('Best Programming Language?', '2023-12-31 23:59:59', 2, TRUE);
