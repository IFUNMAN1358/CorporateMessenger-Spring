CREATE TABLE unread_messages_by_chat_id_and_user_id (
    chat_id uuid,
    user_id uuid,
    unread_count int,
    PRIMARY KEY ((chat_id, user_id))
);