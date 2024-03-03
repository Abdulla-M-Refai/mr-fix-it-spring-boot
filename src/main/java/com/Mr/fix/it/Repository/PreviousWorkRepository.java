package com.Mr.fix.it.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Mr.fix.it.Entity.PreviousWork;

public interface PreviousWorkRepository extends JpaRepository<PreviousWork, Long>
{
}