import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.util.Topics;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.MessageBody;
import models.MessageInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RetrieveEventData {

    public static List<MessageInfo> getS3Data(Regions clientRegion) throws IOException {

        AmazonSNS sns = AmazonSNSClient
                .builder()
                .withRegion(clientRegion)
                .build();
        AmazonSQS sqs = AmazonSQSClient
                .builder()
                .withRegion(clientRegion)
                .build();

        String myTopicArn = "arn:aws:sns:eu-west-2:099421490492:EventProcessing-snsTopicSensorDataPart1-QVDE0JIXZS1V";
        String myQueueUrl = sqs.createQueue(new CreateQueueRequest(UUID.randomUUID().toString())).getQueueUrl();

        Topics.subscribeQueue(sns, sqs, myTopicArn, myQueueUrl);

        List<MessageBody> messageBodyList = new ArrayList<>();
        List<MessageInfo> messageInfoList = new ArrayList<>();

        long t = System.currentTimeMillis();
        long end = t+5000;

        while(System.currentTimeMillis() < end) {

            List<Message> messages = sqs.receiveMessage(new ReceiveMessageRequest(myQueueUrl).withMaxNumberOfMessages(10).withWaitTimeSeconds(5)).getMessages();


            for (Message message : messages) {
                ObjectMapper objectMapper = new ObjectMapper();
                messageBodyList.add(objectMapper.readValue(message.getBody(), new TypeReference<MessageBody>() {
                }));
            }
        }

        for (MessageBody messageBody : messageBodyList) {
            ObjectMapper objectMapper = new ObjectMapper();
            messageInfoList.add(objectMapper.readValue(messageBody.getMessage(), new TypeReference<MessageInfo>() {
            }));
        }

        System.out.println("Deleting the test queue...\n");
        sqs.deleteQueue(new DeleteQueueRequest(myQueueUrl));

        return messageInfoList;
    }
}
