CREATE KEYSPACE IF NOT EXISTS messenger WITH REPLICATION = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};