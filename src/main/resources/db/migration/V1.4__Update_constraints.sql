ALTER TABLE polls DROP CONSTRAINT polls_creator_id_fkey;
ALTER TABLE polls ADD CONSTRAINT polls_creator_id_fkey
    FOREIGN KEY (creator_id) REFERENCES users(id)
    ON DELETE CASCADE;

ALTER TABLE poll_answers DROP CONSTRAINT poll_answers_poll_id_fkey;
ALTER TABLE poll_answers ADD CONSTRAINT poll_answers_poll_id_fkey
    FOREIGN KEY (poll_id) REFERENCES polls(id)
    ON DELETE CASCADE;

ALTER TABLE votes DROP CONSTRAINT votes_voter_id_fkey;
ALTER TABLE votes ADD CONSTRAINT votes_voter_id_fkey
    FOREIGN KEY (voter_id) REFERENCES users(id)
    ON DELETE CASCADE;

ALTER TABLE votes DROP CONSTRAINT votes_poll_id_fkey;
ALTER TABLE votes ADD CONSTRAINT votes_poll_id_fkey
    FOREIGN KEY (poll_id) REFERENCES polls(id)
    ON DELETE CASCADE;
