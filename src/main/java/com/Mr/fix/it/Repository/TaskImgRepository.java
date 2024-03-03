package com.Mr.fix.it.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Mr.fix.it.Entity.TaskImg;

public interface TaskImgRepository extends JpaRepository<TaskImg, Long>
{
}