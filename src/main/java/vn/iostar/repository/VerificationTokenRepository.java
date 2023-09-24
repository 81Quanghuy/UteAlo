package vn.iostar.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.entity.VerificationToken;

@Hidden
@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, String> {
    <Optional> VerificationToken findByToken(String token);
}
