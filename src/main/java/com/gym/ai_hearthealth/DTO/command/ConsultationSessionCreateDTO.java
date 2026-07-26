package com.gym.ai_hearthealth.DTO.command;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ConsultationSessionCreateDTO {
    @Size(max = 200, message = "会话标题最多200个字符")
    private String sessionTitle;

    @NotBlank(message = "初始信息不能为空")
    @Size(max = 2000, message = "会话初始消息最多2000个字符")
    private String initialMessage;

}
