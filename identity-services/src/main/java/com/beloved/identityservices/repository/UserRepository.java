package com.beloved.identityservices.repository;
import com.beloved.identityservices.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean  existsByEmail(String email);
    boolean existsByAccountNumber(String accountNumber);
    User findByAccountNumber(String accountNumber);
    Optional<User> findByEmail(String username);
    Optional<User> findOneByEmailAndPassword(String email, String password);

}
