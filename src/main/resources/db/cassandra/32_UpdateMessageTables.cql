ALTER TABLE messenger_ks.messages_by_id DROP chat_id;
ALTER TABLE messenger_ks.messages_by_chat_id DROP chat_id;

ALTER TABLE messenger_ks.messages_by_id ADD chat_id bigint;
ALTER TABLE messenger_ks.messages_by_chat_id ADD chat_id bigint;