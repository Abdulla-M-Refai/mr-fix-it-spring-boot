package com.Mr.fix.it.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Mr.fix.it.Entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>
{

}
