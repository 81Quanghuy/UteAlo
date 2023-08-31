package vn.iostar.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.entity.EmailVerification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Hidden
@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, String> {
    List<EmailVerification> findByExpirationTimeBefore(LocalDateTime expirationTime);
    Optional<EmailVerification> findByEmail(String email);
}
