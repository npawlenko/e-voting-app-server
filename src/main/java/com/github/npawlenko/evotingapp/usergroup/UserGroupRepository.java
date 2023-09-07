package com.github.npawlenko.evotingapp.usergroup;

import com.github.npawlenko.evotingapp.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    @Query("""
           SELECT ug FROM UserGroup ug
           JOIN ug.polls
           """
    )
    List<UserGroup> findUserGroupByPollId(Long pollId);
}
