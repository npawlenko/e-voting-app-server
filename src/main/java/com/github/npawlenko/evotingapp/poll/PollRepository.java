package com.github.npawlenko.evotingapp.poll;

import com.github.npawlenko.evotingapp.model.Poll;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    @Query(
            """
            SELECT DISTINCT p FROM Poll p
            LEFT JOIN FETCH p.userGroup ug
            WHERE p.isPublic
                OR p.creator.id = :userId
                OR ug.id IN (
                                SELECT ug2.id FROM UserGroup ug2
                                JOIN ug2.users u WHERE u.id = :userId
                            )
            """
    )
    List<Poll> findAccessibleForUserPolls(@Param("userId") Long userId, Pageable pageable);

    List<Poll> findPollByCreatorId(Long creatorId, Pageable pageable);
}
