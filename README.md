<h1>Initialize:</h1>

1. `docker-compose up --build cassandra minio`

---

**Cassandra init:**

1. `docker exec -it cassandra bash`

2. `cqlsh -u USERNAME -p PASSWORD`

3. `CREATE KEYSPACE IF NOT EXISTS messenger_ks WITH REPLICATION = {'class': 'SimpleStrategy','replication_factor': 1};`

---

**Minio init:**

1. Go to `localhost:9001`.

2. Insert `minioadmin` as login and password from .env.

3. Create buckets: `user-photos`, `employee-photos`, `chat-photos`, `message-files`.

4. Create auth credentials and input accessToken and secretToken from minio client to .env.