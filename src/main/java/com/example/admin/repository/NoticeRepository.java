package com.example.admin.repository;

import com.example.admin.model.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("SELECT p FROM Notice p WHERE p.deleted = false ORDER BY p.createdAt DESC")
    Page<Notice> findAllActive(Pageable pageable);

    @Query("SELECT p FROM Notice p WHERE p.deleted = false AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Notice> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Notice p WHERE p.deleted = false AND p.id = :id")
    Optional<Notice> findActiveById(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Notice p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);
}
