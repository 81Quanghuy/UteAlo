package vn.iostar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iostar.entity.FilesMedia;

public interface FileRepository extends JpaRepository<FilesMedia, String> {
}
