package com.nagornov.CorporateMessenger.application.dto.message;

import com.nagornov.CorporateMessenger.domain.model.Message;
import com.nagornov.CorporateMessenger.domain.model.MessageFile;
import com.nagornov.CorporateMessenger.domain.model.ReadMessage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MessageResponse {

    @NotNull
    private Message message;

    private List<ReadMessage> readMessages;

    private List<MessageFile> messageFiles;

}
