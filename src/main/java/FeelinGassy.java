
import com.amazonaws.regions.Regions;
import models.Location;
import models.MessageInfo;
import java.util.List;
import java.util.stream.Collectors;

public class FeelinGassy {

    public static void main(String[] args) throws Exception {

        Regions clientRegion = Regions.EU_WEST_2;
        String bucketName = "eventprocessing-locationss3bucket-7mbrb9iiisk4";
        String fileName = "locations.json";

        List<Location> gasLocations = RetrieveLocationData.getBucketData(clientRegion, bucketName, fileName);

        List<MessageInfo> messageInfoList = RetrieveEventData.getS3Data(clientRegion);

        //match bucketdata ids to s3 data locationIds

        List<MessageInfo> filteredMessages = messageInfoList.stream()
                .filter(messageInfo -> gasLocations.stream()
                .anyMatch(location ->
                        messageInfo.getLocationId().equals(location.getId())))
                .collect(Collectors.toList());

        System.out.println(gasLocations);
        System.out.println(messageInfoList);
        System.out.println(filteredMessages);

        }
    }


