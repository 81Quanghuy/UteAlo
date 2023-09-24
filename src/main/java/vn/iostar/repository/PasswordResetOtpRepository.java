package vn.iostar.repository;


import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.iostar.entity.PasswordResetOtp;
import vn.iostar.entity.User;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
@Hidden
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, String> {

    Optional<PasswordResetOtp> findByOtp(String otp);

    Optional<PasswordResetOtp> findByUser(User user);

    Stream<PasswordResetOtp> findAllByExpiryDateLessThan(Date now);

    void deleteByExpiryDateLessThan(Date now);

    @Modifying
    @Query("delete from PasswordResetOtp t where t.expiryDate <= ?1")
    void deleteAllExpiredSince(Date now);
}
