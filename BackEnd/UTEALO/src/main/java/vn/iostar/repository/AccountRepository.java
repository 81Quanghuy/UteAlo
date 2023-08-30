package vn.iostar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iostar.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, String>{

}
