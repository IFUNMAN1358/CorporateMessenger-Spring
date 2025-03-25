package com.nagornov.CorporateMessenger.application.dto.message;

import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.domain.model.message.MessageFile;
import com.nagornov.CorporateMessenger.domain.model.message.ReadMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MessageResponse {

    private Message message;

    private List<ReadMessage> readMessages;

    private List<MessageFile> messageFiles;

}
