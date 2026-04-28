package com.neocompany.taroro.domain.message.dto.response;

import java.time.Instant;
import java.util.List;

import com.neocompany.taroro.domain.message.entity.ChatMessage;
import com.neocompany.taroro.domain.message.entity.MessageType;

import lombok.Getter;

@Getter
public class ChatMessageResponse {

    private final Long messageId;
    private final Long roomId;
    private final Long senderId;
    private final String senderName;
    private final String senderRole;  // MASTER or USER
    private final MessageType messageType;
    private final String content;
    private final Instant createdAt;
    private final long readCount;

    public ChatMessageResponse(ChatMessage msg, String senderName, String senderRole, long readCount) {
        this.messageId = msg.getId();
        this.roomId = msg.getRoomId();
        this.senderId = msg.getSenderId();
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.messageType = msg.getMessageType();
        this.content = msg.getContent();
        this.createdAt = msg.getCreatedAt();
        this.readCount = readCount;
    }

    @Getter
    public static class PageResult {
        private final Long roomId;
        private final List<ChatMessageResponse> messages;
        private final boolean hasNext;
        private final Long nextCursor;

        public PageResult(Long roomId, List<ChatMessageResponse> messages, boolean hasNext) {
            this.roomId = roomId;
            this.messages = messages;
            this.hasNext = hasNext;
            this.nextCursor = hasNext && !messages.isEmpty()
                    ? messages.get(messages.size() - 1).getMessageId()
                    : null;
        }
    }
}
