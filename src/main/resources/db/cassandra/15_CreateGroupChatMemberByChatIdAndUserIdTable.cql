create table group_chat_members_by_chat_id_and_user_id (
    id uuid,
    chat_id uuid,
    user_id uuid,
    user_first_name text,
    joined_at timestamp,
    primary key ((chat_id, user_id), id)
);