package vn.iostar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iostar.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

	Optional<Account> findByEmail(String email);

	Optional<Account> findByPhone(String phone);

	Optional<Account> findByUserUserId(String userId);

	Optional<Account> findByEmailAndIsActiveIsTrue(String email);

	Optional<Account> findByUserUserIdAndIsActiveIsTrue(String userId);

	Optional<Account> findByPhoneAndIsActiveIsTrue(String emailOrPhone);
}
