package com.gym.ai_hearthealth.AIService;

public class StructOutPut {
    public record StreamChatSession(
            String sessionId,
            Long userHash,
            String initialMessage,
            Long startTime,
            Long expiryTime,
            Integer messageCount,
            String status
    ){}
}
