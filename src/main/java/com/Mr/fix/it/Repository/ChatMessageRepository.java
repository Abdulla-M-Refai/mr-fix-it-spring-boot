package com.Mr.fix.it.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Mr.fix.it.Entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>
{
}
