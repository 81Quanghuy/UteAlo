package vn.iostar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import vn.iostar.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer>{

}
