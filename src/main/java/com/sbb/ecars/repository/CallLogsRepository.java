package com.sbb.ecars.repository;

import com.sbb.ecars.domain.CallLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CallLogsRepository extends JpaRepository<CallLogs, Long> {
    List<CallLogs> findByCaller(String category);
    List<CallLogs> findByIsDuplicate(Boolean isDuplicate);
}
