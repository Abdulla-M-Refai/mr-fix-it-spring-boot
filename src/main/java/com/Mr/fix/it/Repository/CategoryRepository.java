package com.Mr.fix.it.Repository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import com.Mr.fix.it.Entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>
{
    Optional<Category> findByType(String type);

    @Query("SELECT c FROM Category c ORDER BY FUNCTION('RAND')")
    List<Category> findAllInRandomOrder();
}