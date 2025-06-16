<h1>Initialize:</h1>

---

**Cassandra init:**

1. `docker-compose up --build cassandra`

2. `docker exec -it cassandra bash`

3. `cqlsh -u <USERNAME> -p <PASSWORD>`. Username and password from .env.

4. `CREATE KEYSPACE IF NOT EXISTS messenger_ks WITH REPLICATION = {'class': 'SimpleStrategy','replication_factor': 1};`

---

**Minio init:**

1. `docker-compose up --build minio`

2. Go to `localhost:9001`.

3. Insert `minioadmin` as login and password from .env.

4. Create buckets: `user-photos`, `employee-photos`, `chat-photos`, `message-files`.

5. Go to "Identity" -> "Service Accounts" into client. Create non-custom auth credentials and input accessToken and secretToken from minio client to .env.

---

**Run:**

1. `docker-compose up --build`

---