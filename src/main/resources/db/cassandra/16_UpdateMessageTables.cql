ALTER TABLE messenger_ks.messages_by_id ADD sender_first_name text;
ALTER TABLE messenger_ks.messages_by_id ADD sender_username text;

ALTER TABLE messenger_ks.messages_by_chat_id ADD sender_first_name text;
ALTER TABLE messenger_ks.messages_by_chat_id ADD sender_username text;