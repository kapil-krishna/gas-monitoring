import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class RetrieveLocationData {

    public static List<Location> getBucketData(Regions clientRegion, String bucketName, String fileName) throws Exception {

        S3Object headerOverrideObject = null;

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .build();

            GetObjectRequest getObjectRequestHeaderOverride = new GetObjectRequest(bucketName, fileName);
            headerOverrideObject = s3Client.getObject(getObjectRequestHeaderOverride);

            ObjectMapper objectMapper = new ObjectMapper();
            List<Location> sensorLocations = objectMapper.readValue(headerOverrideObject.getObjectContent(), new TypeReference<List<Location>>(){});

            return (sensorLocations);

        } finally {
            if (headerOverrideObject != null) {
                headerOverrideObject.close();
            }
        }
    }
}