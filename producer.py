from faker import Faker
import json
import random
import time
from kafka import KafkaProducer
from datetime import datetime

faker = Faker()
producer = KafkaProducer(bootstrap_servers='localhost:9092')

user_ids = [faker.uuid4() for _ in range(10)]  # Generate a list of 100 unique user IDs

while True:
    event = {
        "timestamp": int(datetime.now().timestamp() * 1000),
        "userId": random.choice(user_ids),  # Randomly select a user ID from the list
        "eventType": "pageView",
        "productId": faker.uuid4(),
        "sessionDuration": faker.random_int(min=0, max=300)
    }
    producer.send('user-events', json.dumps(event).encode('utf-8'))
    time.sleep(0.1)

