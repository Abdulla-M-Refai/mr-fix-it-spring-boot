package com.Mr.fix.it.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Mr.fix.it.Entity.CanceledTasks;

public interface CanceledTasksRepository extends JpaRepository<CanceledTasks, Long>
{

}
