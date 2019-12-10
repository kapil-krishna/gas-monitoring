import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.util.Topics;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class FeelinGassy {

    public static void main(String[] args) throws Exception {
        Regions clientRegion = Regions.EU_WEST_2;
        RetrieveData.getBucketData
                (clientRegion,"eventprocessing-locationss3bucket-7mbrb9iiisk4", "locations.json");

        AmazonSNS sns = AmazonSNSClient
                .builder()
                .withRegion(clientRegion)
                .build();
        AmazonSQS sqs = AmazonSQSClient
                .builder()
                .withRegion(clientRegion)
                .build();

        String myTopicArn = "arn:aws:sns:eu-west-2:099421490492:EventProcessing-snsTopicSensorDataPart1-QVDE0JIXZS1V";
        String myQueueUrl = sqs.createQueue(new CreateQueueRequest("KapilsQueue")).getQueueUrl();

        Topics.subscribeQueue(sns, sqs, myTopicArn, myQueueUrl);

        List<Message> messages = sqs.receiveMessage(new ReceiveMessageRequest(myQueueUrl)).getMessages();

        if (messages.size() > 0) {
            System.out.println("Message: " +  (messages.get(0)).getBody());
        } else {
            System.out.println("No messages found");
        }

        // TODO: 10/12/2019
        // todo 1. create class to model body of a message
        // todo 2. write code to match body.message.locationId === id.headerOverrideObject
        // todo 3. print all matching messages
        // todo 4. delete queue

        System.out.println("Deleting the test queue.\n");
        sqs.deleteQueue(new DeleteQueueRequest(myQueueUrl));
    }
}



