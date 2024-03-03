package com.Mr.fix.it.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.MessageMapping;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.IOException;

import com.Mr.fix.it.DTO.ChatMessageDTO;

import com.Mr.fix.it.Service.ChatService;

import com.Mr.fix.it.Request.ImageRequest;
import com.Mr.fix.it.Request.MessageRequest;
import com.Mr.fix.it.Request.DeleteChatRequest;
import com.Mr.fix.it.Request.InteractionRequest;

import com.Mr.fix.it.Response.ChatResponse;
import com.Mr.fix.it.Response.ChatsResponse;
import com.Mr.fix.it.Response.GenericResponse;
import com.Mr.fix.it.Response.MessagesResponse;

import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;
import com.Mr.fix.it.Exception.ExceptionType.ValidationException;

@RestController
@RequestMapping({"/api/client", "/api/worker"})
@CrossOrigin("*")
@RequiredArgsConstructor
public class ChatController
{
    private final ChatService chatService;

    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("new-message")
    public ResponseEntity<GenericResponse> newMessage(
        @RequestBody
        MessageRequest messageRequest
    ) throws NotFoundException
    {
        chatService.saveMessage(messageRequest);

        return ResponseEntity.ok(
            GenericResponse
                .builder()
                .state("success")
                .message("message sent successfully")
                .build()
        );
    }

    @PostMapping("notify-audio-call/{id}")
    public ResponseEntity<GenericResponse> notifyAudioMeeting(
        @PathVariable
        String id,
        @RequestHeader("Authorization")
        String token
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            chatService.notifyMeeting(
                Long.parseLong(id),
                false,
                token.substring(7)
            )
        );
    }

    @PostMapping("notify-video-call/{id}")
    public ResponseEntity<GenericResponse> notifyVideoMeeting(
        @PathVariable
        String id,
        @RequestHeader("Authorization")
        String token
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            chatService.notifyMeeting(
                Long.parseLong(id),
                true,
                token.substring(7)
            )
        );
    }

    @MessageMapping("/chat")
    public void processMessage(
        @Payload
        MessageRequest messageRequest
    ) throws NotFoundException
    {
        ChatMessageDTO  chatMessage = chatService.saveMessage(messageRequest);

        messagingTemplate.convertAndSendToUser(
            chatMessage.getRoomID().toString(),
            "/queue/messages",
            chatMessage
        );
    }

    @MessageMapping("/typing")
    public void typingRequest(
        @Payload
        InteractionRequest interactionRequest
    ) throws NotFoundException
    {
        messagingTemplate.convertAndSendToUser(
            interactionRequest.getRoomID(),
            "/queue/messages",
            interactionRequest
        );
    }

    @MessageMapping("/seen")
    public void seenRequest(
        @Payload
        InteractionRequest interactionRequest
    ) throws NotFoundException
    {
        chatService.updateMessagesSeen(
            interactionRequest.getParsedRoomID(),
            interactionRequest.getParsedReceiverID()
        );

        messagingTemplate.convertAndSendToUser(
            interactionRequest.getRoomID(),
            "/queue/messages",
            interactionRequest
        );
    }

    @PostMapping("/chat-image-upload")
    public ResponseEntity<GenericResponse> chatImageUpload(
        @Valid
        @ModelAttribute
        ImageRequest imageRequest,
        BindingResult result
    ) throws
        ValidationException,
        IOException
    {
        return ResponseEntity.ok(
            chatService.chatImageUpload(
                imageRequest,
                result
            )
        );
    }

    @GetMapping("/get-chats")
    public ResponseEntity<ChatsResponse> getChats(
        @RequestHeader(name="Authorization")
        String token
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            chatService.getChats(
                token.substring(7)
            )
        );
    }

    @GetMapping("/get-chat")
    public ResponseEntity<ChatResponse> getChat(
        @RequestParam
        String receiverID,
        @RequestHeader(name="Authorization")
        String token
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            chatService.getChat(
                Long.parseLong(receiverID),
                token.substring(7)
            )
        );
    }

    @GetMapping("/get-chat-messages")
    public ResponseEntity<MessagesResponse> getChatMessages(
        @RequestParam(value = "chatID")
        String chatID,
        @RequestHeader(name="Authorization")
        String token
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            chatService.getChatMessages(
                chatID,
                token.substring(7)
            )
        );
    }

    @PostMapping("/delete-chat")
    public ResponseEntity<GenericResponse> deleteChat(
        @RequestBody
        DeleteChatRequest deleteChatRequest,
        @RequestHeader(name = "Authorization")
        String token
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            chatService.deleteChat(
                deleteChatRequest,
                token.substring(7)
            )
        );
    }
}