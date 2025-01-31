create table group_chat_members_by_user_id (
    id uuid,
    chat_id uuid,
    user_id uuid,
    user_first_name text,
    joined_at timestamp,
    primary key (user_id, id)
);