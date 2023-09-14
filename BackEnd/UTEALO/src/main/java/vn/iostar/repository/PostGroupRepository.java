package vn.iostar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iostar.entity.PostGroup;

@Repository
public interface PostGroupRepository extends JpaRepository<PostGroup, Integer> {
	Optional<PostGroup> findByPostGroupName(String postGroupName);
}
