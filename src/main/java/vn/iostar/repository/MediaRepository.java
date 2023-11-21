package vn.iostar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iostar.entity.Files;

public interface MediaRepository extends JpaRepository<Files, String> {
}
