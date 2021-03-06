package kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class ProducerDemoWithKeys {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        final Logger logger = LoggerFactory.getLogger(ProducerDemoWithKeys.class);
        String bootstrapServer = "localhost:9092";

        //create producer properties
        Properties properties = new Properties();
        properties.setProperty(BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.setProperty(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);


        for (int i = 0; i < 10; i++) {
            //create a producer record

            String topic = "first_topic";
            String value = "hello world" + i;
            String key = "id_" + i;
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, key, value);

            logger.info("key: " + key);

            //send data
            producer.send(record, (recordMetadata, e) -> {

                //execute every time a record is successfully sent or an exception is thrown
                if (e == null) {

                    //the record was successfully sent
                    logger.info("Received new metadata.\n" +
                            "Topic: " + recordMetadata.topic() + "\n" +
                            "Partition: " + recordMetadata.partition() + "\n" +
                            "Offset: " + recordMetadata.offset() + "\n" +
                            "Timestamp: " + recordMetadata.timestamp());

                } else {
                    logger.error("Error while producing", e);
                }
            }).get();
        }
        producer.flush();
        producer.close();

    }
}
