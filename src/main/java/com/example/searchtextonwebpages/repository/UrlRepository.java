package com.example.searchtextonwebpages.repository;

import com.example.searchtextonwebpages.model.Url;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    @Query(value = "SELECT * FROM urls WHERE status is null limit :limit", nativeQuery = true)
    List<Url> findNoneExceptionsUrl(@Param("limit") int limit);

    @Query(value = "SELECT * FROM urls WHERE status is not null", nativeQuery = true)
    List<Url> findAllNotNullStatus();

    @Transactional
    @Modifying
    @Query("update Url u set u.status = :status where u.id = :urlId")
    int updateStatus(@Param("status") String status, @Param("urlId") long urlId);
}
