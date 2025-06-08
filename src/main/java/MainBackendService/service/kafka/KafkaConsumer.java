//package MainBackendService.service.kafka;
//
/// / Importing required classes
//
//import MainBackendService.dto.DeskDto;
//import MainBackendService.dto.kafka.debeziumConnector.mysql.KafkaMysqlMessageStructure;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.jooq.sample.model.tables.records.DeskRecord;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.annotation.PartitionOffset;
//import org.springframework.kafka.annotation.TopicPartition;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//
//@Service
//public class KafkaConsumer {
//    private final ElasticsearchOperations elasticsearchOperations;
//    Logger logger = LogManager.getLogger(KafkaConsumer.class);
//    @Value(value = "${kafka.topic.lexilearn.desk}")
//    private String deskTopic;
//    @Value(value = "${kafka.group.id}")
//    private String kafkaGroupId;
//
//    public KafkaConsumer(ElasticsearchOperations elasticsearchOperations) {
//        this.elasticsearchOperations = elasticsearchOperations;
//    }
//
//
//    @KafkaListener(
//            topicPartitions = @TopicPartition(topic = "${kafka.topic.lexilearn.desk}",
//                    partitionOffsets = {
//                            @PartitionOffset(partition = "0", initialOffset = "0")}
//            ),
//            concurrency = "1" // Ensures  x message is processed at a time
//    )
//    public void consume(@Payload String message) throws IOException {
//        // Print statement
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        KafkaMysqlMessageStructure<DeskRecord> kafkaMysqlMessageStructure = objectMapper.readValue(message, KafkaMysqlMessageStructure.class);
//        DeskDto deskDto = objectMapper.treeToValue(kafkaMysqlMessageStructure.getPayloadAfter(), DeskDto.class);
//        // Save the DeskDto to Elasticsearch
//        if (deskDto != null) {
//            elasticsearchOperations.save(deskDto, deskDto.getIndexCoordinates());
//        }
//
//    }
//}