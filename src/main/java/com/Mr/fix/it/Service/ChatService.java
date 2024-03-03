package com.Mr.fix.it.Service;

import com.Mr.fix.it.Entity.Enum.MessageType;
import com.Mr.fix.it.Response.*;
import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.validation.BindingResult;

import java.io.File;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import java.io.IOException;

import com.Mr.fix.it.Entity.User;
import com.Mr.fix.it.Entity.ChatRoom;
import com.Mr.fix.it.Entity.ChatMessage;

import com.Mr.fix.it.Repository.UserRepository;
import com.Mr.fix.it.Repository.ChatRoomRepository;
import com.Mr.fix.it.Repository.ChatMessageRepository;

import com.Mr.fix.it.DTO.UserDTO;
import com.Mr.fix.it.DTO.ChatRoomDTO;
import com.Mr.fix.it.DTO.ChatMessageDTO;

import com.Mr.fix.it.Request.ImageRequest;
import com.Mr.fix.it.Request.MessageRequest;
import com.Mr.fix.it.Request.DeleteChatRequest;

import com.Mr.fix.it.Util.Helper;
import com.Mr.fix.it.Config.Security.JwtService;

import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;
import com.Mr.fix.it.Exception.ExceptionType.ValidationException;
import com.Mr.fix.it.Exception.ExceptionType.NotAuthorizedException;

@Service
@RequiredArgsConstructor
public class ChatService
{
    private final UserRepository userRepository;

    private final ChatRoomRepository chatRoomRepository;

    private final ChatMessageRepository chatMessageRepository;

    private final JwtService jwtService;

    private final NotificationService notificationService;

    private final ImageUploadingService imageUploadingService;

    @Transactional
    public ChatMessageDTO saveMessage(
        MessageRequest messageRequest
    ) throws NotFoundException
    {
        var sender = userRepository.findById(messageRequest.getParsedSenderID())
            .orElseThrow(() -> new NotFoundException("sender not found"));

        var receiver =  userRepository.findById(messageRequest.getParsedReceiverID())
            .orElseThrow(() -> new NotFoundException("receiver not found"));

        var chatRoom = chatRoomRepository.findBySenderIDAndReceiverID(
            sender.getId(),
            messageRequest.getParsedReceiverID()
        ).or(
            () -> {
                var newChatRoom = chatRoomRepository.save(
                    ChatRoom.builder()
                        .sender(sender)
                        .receiver(receiver)
                        .isDeletedForReceiver(false)
                        .isDeletedForSender(false)
                        .build()
                );

                return Optional.of(newChatRoom);
            }
        ).get();

        if(chatRoom.getIsDeletedForSender() || chatRoom.getIsDeletedForReceiver())
        {
            chatRoom.setIsDeletedForSender(false);
            chatRoom.setIsDeletedForReceiver(false);
            chatRoomRepository.save(chatRoom);
        }

        var message = ChatMessage
            .builder()
            .chatRoom(chatRoom)
            .sender(sender)
            .receiver(receiver)
            .content(messageRequest.getContent())
            .messageType(messageRequest.getType())
            .timestamp(LocalDateTime.now())
            .isSeenForReceiver(false)
            .isDeletedForSender(false)
            .isDeletedForReceiver(false)
            .build();

        chatMessageRepository.save(message);
        chatMessageRepository.flush();

        new java.util.Timer().schedule(
            new java.util.TimerTask()
            {
                @Override
                public void run()
                {
                    if(!chatMessageRepository.findById(message.getId()).get().getIsSeenForReceiver())
                    {
                        if(message.getReceiver().getFcm() != null)
                            notificationService.sendNotificationByToken(
                                NotificationMessage
                                    .builder()
                                    .title(sender.getFirstName() + " " + sender.getLastName())
                                    .body(message.getMessageType() == MessageType.TEXT ? message.getContent() : "image")
                                    .recipientToken(message.getReceiver().getFcm())
                                    .data(new HashMap<>())
                                    .build()
                            );
                    }
                }
            },
            500
        );

        return ChatMessageDTO
            .builder()
            .roomID(message.getChatRoom().getId())
            .senderID(message.getSender().getId())
            .receiverID(message.getReceiver().getId())
            .content(message.getContent())
            .type(message.getMessageType())
            .timestamp(message.getTimestamp())
            .seen(false)
            .build();
    }

    public ChatsResponse getChats(
        String token
    ) throws NotFoundException
    {
        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        List<ChatRoom> chatRooms = chatRoomRepository.findAllByUserID(user.getId());
        List<ChatRoomDTO> chatRoomDTOS = new ArrayList<>(chatRooms
            .stream()
            .map(chat ->
                ChatRoomDTO
                    .builder()
                    .chatID(chat.getId())
                    .sender(getUserDto(user))
                    .receiver(
                        Objects.equals(chat.getReceiver().getId(), user.getId()) ?
                        getUserDto(chat.getSender()) :
                        getUserDto(chat.getReceiver())
                    )
                    .lastMessage(
                        chat.getChatMessages() != null && !chat.getChatMessages().isEmpty() ?
                            ChatMessageDTO
                                .builder()
                                .roomID(chat.getId())
                                .senderID(chat.getSender().getId())
                                .receiverID(chat.getReceiver().getId())
                                .content(chat.getChatMessages().get(chat.getChatMessages().size() - 1).getContent())
                                .type(chat.getChatMessages().get(chat.getChatMessages().size() - 1).getMessageType())
                                .timestamp(chat.getChatMessages().get(chat.getChatMessages().size() - 1).getTimestamp())
                                .build() : null
                    )
                    .newMessages(
                        chat.getChatMessages()
                            .stream()
                            .filter(message ->
                                !message.getIsSeenForReceiver() &&
                                Objects.equals(
                                    message.getReceiver().getId(),
                                    user.getId()
                                )
                            )
                            .toList()
                            .size()
                    )
                    .build()
            )
            .toList());

        chatRoomDTOS = chatRoomDTOS
            .stream()
            .sorted((cr1, cr2) -> cr2.getLastMessage().getTimestamp().compareTo(cr1.getLastMessage().getTimestamp()))
            .toList();

        return ChatsResponse
            .builder()
            .chats(chatRoomDTOS)
            .build();
    }

    private UserDTO getUserDto(User user)
    {
        return UserDTO
            .builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .dob(user.getDob())
            .gender(user.getGender().name())
            .city(user.getCity())
            .email(user.getEmail())
            .phone(user.getPhone())
            .img(user.getImg())
            .type(user.getType())
            .favorites(null)
            .build();
    }

    public MessagesResponse getChatMessages(
        String chatID,
        String token
    ) throws NotFoundException
    {
        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        long parsedChatID = Long.parseLong(chatID);
        var chatRoom = chatRoomRepository.findById(parsedChatID)
            .orElseThrow(() -> new NotFoundException("chat not found"));

        if(!Objects.equals(chatRoom.getSender().getId(), user.getId()) && !Objects.equals(chatRoom.getReceiver().getId(), user.getId()))
            throw new NotAuthorizedException("not authorized");

        List<ChatMessageDTO> messages = chatRoom
            .getChatMessages()
            .stream()
            .filter(chatMessage ->
                (Objects.equals(chatMessage.getSender().getId(), user.getId()) &&
                !chatMessage.getIsDeletedForSender()) ||
                (Objects.equals(chatMessage.getReceiver().getId(), user.getId()) &&
                !chatMessage.getIsDeletedForReceiver())
            )
            .map(message ->
                ChatMessageDTO
                    .builder()
                    .roomID(chatRoom.getId())
                    .senderID(message.getSender().getId())
                    .receiverID(message.getReceiver().getId())
                    .content(message.getContent())
                    .type(message.getMessageType())
                    .timestamp(message.getTimestamp())
                    .seen(message.getIsSeenForReceiver())
                    .build()
            )
            .toList();

        return MessagesResponse
            .builder()
            .messages(messages)
            .build();
    }

    public GenericResponse chatImageUpload(
        ImageRequest imageRequest,
        BindingResult result
    ) throws
        ValidationException,
        IOException
    {
        Helper.fieldsValidate(result);

        String fileName = Helper.generateFileName(imageRequest.getImg());
        File file = imageUploadingService.convertToFile(imageRequest.getImg(), fileName);
        String uri = imageUploadingService.uploadFile(file, fileName);

        return GenericResponse
            .builder()
            .state("success")
            .message(uri)
            .build();
    }

    @Transactional
    public GenericResponse deleteChat(
        DeleteChatRequest deleteChatRequest,
        String token
    ) throws NotFoundException
    {
        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        var chatRoom = chatRoomRepository.findById(deleteChatRequest.getParsedChatID())
            .orElseThrow(() -> new NotFoundException("chat not found"));

        if(Objects.equals(chatRoom.getSender().getId(), user.getId()))
            chatRoom.setIsDeletedForSender(true);
        else if(Objects.equals(chatRoom.getReceiver().getId(), user.getId()))
            chatRoom.setIsDeletedForReceiver(true);

        chatRoom.getChatMessages().forEach(chatMessage -> {
            if(Objects.equals(chatMessage.getSender().getId(), user.getId()))
                chatMessage.setIsDeletedForSender(true);
            else if(Objects.equals(chatMessage.getReceiver().getId(), user.getId()))
                chatMessage.setIsDeletedForReceiver(true);

            chatMessageRepository.save(chatMessage);
        });

        chatRoomRepository.save(chatRoom);

        return GenericResponse
            .builder()
            .state("success")
            .message("chat deleted successfully")
            .build();
    }

    public ChatResponse getChat(
        long receiverID,
        String token
    ) throws NotFoundException
    {
        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        var receiver = userRepository.findById(receiverID)
            .orElseThrow(() -> new NotFoundException("receiver not found"));

        var chatRoom = chatRoomRepository.findBySenderIDAndReceiverID(user.getId(), receiver.getId());

        ChatRoomDTO chatRoomDTO = null;

        if(chatRoom.isPresent())
        {
            chatRoomDTO = ChatRoomDTO
                .builder()
                .chatID(chatRoom.get().getId())
                .sender(getUserDto(user))
                .receiver(
                    Objects.equals(chatRoom.get().getReceiver().getId(), user.getId()) ?
                    getUserDto(chatRoom.get().getSender()) :
                    getUserDto(chatRoom.get().getReceiver())
                )
                .lastMessage(
                    chatRoom.get().getChatMessages() != null && !chatRoom.get().getChatMessages().isEmpty() ?
                        ChatMessageDTO
                            .builder()
                            .roomID(chatRoom.get().getId())
                            .senderID(chatRoom.get().getSender().getId())
                            .receiverID(chatRoom.get().getReceiver().getId())
                            .content(chatRoom.get().getChatMessages().get(chatRoom.get().getChatMessages().size() - 1).getContent())
                            .type(chatRoom.get().getChatMessages().get(chatRoom.get().getChatMessages().size() - 1).getMessageType())
                            .timestamp(chatRoom.get().getChatMessages().get(chatRoom.get().getChatMessages().size() - 1).getTimestamp())
                            .build() : null
                )
                .newMessages(
                    chatRoom.get().getChatMessages()
                    .stream()
                    .filter(message ->
                        !message.getIsSeenForReceiver() &&
                            Objects.equals(
                                message.getReceiver().getId(),
                                user.getId()
                            )
                    )
                    .toList()
                    .size()
                )
                .build();
        }

        return ChatResponse
            .builder()
            .chat(chatRoomDTO)
            .build();
    }

    @Transactional
    public void updateMessagesSeen(
        Long roomID,
        Long receiverID
    ) throws NotFoundException
    {
        var chatRoom = chatRoomRepository.findById(roomID)
            .orElseThrow(() -> new NotFoundException("room not found"));

        var receiver = userRepository.findById(receiverID)
            .orElseThrow(() -> new NotFoundException("receiver not found"));

        chatRoom.getChatMessages().forEach(chatMessage -> {
            if(Objects.equals(chatMessage.getSender().getId(), receiver.getId()))
                chatMessage.setIsSeenForReceiver(true);
        });

        chatRoomRepository.save(chatRoom);
    }

    public GenericResponse notifyMeeting(
        long id,
        boolean isVideo,
        String token
    ) throws NotFoundException
    {
        String email = jwtService.extractUsername(token);

        var sender = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("sender not found"));

        var receiver = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("receiver not found"));

        if(receiver.getFcm() != null)
            notificationService.sendNotificationByToken(
                NotificationMessage
                    .builder()
                    .title(isVideo ? "Video Meeting" : "Audio Meeting")
                    .body(sender.getFirstName() + " " + sender.getLastName() + " joined " + (isVideo ? "video" : "audio") + " meeting")
                    .recipientToken(receiver.getFcm())
                    .data(new HashMap<>())
                    .build()
            );

        return GenericResponse
            .builder()
            .state("success")
            .message("meeting notifued successfully")
            .build();
    }
}
