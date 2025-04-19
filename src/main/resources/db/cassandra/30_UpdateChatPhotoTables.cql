ALTER TABLE messenger_ks.chat_photos_by_chat_id ADD small_file_size bigint;
ALTER TABLE messenger_ks.chat_photos_by_chat_id ADD big_file_size bigint;
ALTER TABLE messenger_ks.chat_photos_by_chat_id ADD mime_type text;
ALTER TABLE messenger_ks.chat_photos_by_chat_id ADD is_main boolean;