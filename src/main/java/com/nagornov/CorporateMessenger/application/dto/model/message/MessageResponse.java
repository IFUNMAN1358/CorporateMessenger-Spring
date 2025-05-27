package com.nagornov.CorporateMessenger.application.dto.model.message;

import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.domain.model.message.MessageFile;
import com.nagornov.CorporateMessenger.domain.model.message.ReadMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

import java.util.List;

@Data
@ToString(exclude = {"message", "readMessages", "messageFiles"})
public class MessageResponse {

    private Message message;
    private List<ReadMessage> readMessages;
    private List<MessageFile> messageFiles;

    public MessageResponse(
            @NonNull Message message,
            @NonNull List<ReadMessage> readMessages,
            @NonNull List<MessageFile> messageFiles
    ) {
        this.message = message;
        this.readMessages = readMessages;
        this.messageFiles = messageFiles;
    }

}
