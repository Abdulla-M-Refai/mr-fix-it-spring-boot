package com.Mr.fix.it.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Mr.fix.it.Entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long>
{
}