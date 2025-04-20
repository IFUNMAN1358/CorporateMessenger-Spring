ALTER TABLE messenger_ks.message_files_by_message_id DROP file_path;
ALTER TABLE messenger_ks.message_files_by_message_id DROP content_type;

ALTER TABLE messenger_ks.message_files_by_message_id ADD small_file_path text;
ALTER TABLE messenger_ks.message_files_by_message_id ADD big_file_path text;
ALTER TABLE messenger_ks.message_files_by_message_id ADD mime_type text;