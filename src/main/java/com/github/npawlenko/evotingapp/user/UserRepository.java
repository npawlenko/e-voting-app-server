package com.github.npawlenko.evotingapp.user;

import com.github.npawlenko.evotingapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query(
            """
            SELECT u From User u
            WHERE u.id IN :userIds
            """
    )
    List<User> findByUserIds(@Param("userIds") List<Long> userIdsList);
}
