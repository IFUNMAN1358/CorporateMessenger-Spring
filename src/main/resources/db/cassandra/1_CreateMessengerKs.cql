CREATE KEYSPACE IF NOT EXISTS messenger_ks WITH REPLICATION = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};