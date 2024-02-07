package site.termterm.api.global.aws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AmazonS3Util {
    @Value("${cloud.aws.S3.bucket}")
    private String S3_BUCKET;

    private final AmazonS3Client amazonS3Client;

    private Date getExpiration(){
        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + 1000 * 60 * 5); // 5분

        return expiration;
    }

    public String getPresignedUrl(String memberIdentifier) {
        try {
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(S3_BUCKET, "profile-images/" + memberIdentifier)
                            .withMethod(HttpMethod.PUT)
                            .withExpiration(getExpiration());

            return amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
        }catch (Exception e){
            throw new CustomApiException("Presigned Url 생성에 실패하였습니다.");
        }
    }

    public void removeS3Image(String memberIdentifier) {
        ObjectListing objectList = amazonS3Client.listObjects(S3_BUCKET, "profile-images/" + memberIdentifier);
        List<S3ObjectSummary> objectSummaryList = objectList.getObjectSummaries();

        String[] keysList = new String[objectSummaryList.size()];
        int count = 0;
        for(S3ObjectSummary summary : objectSummaryList){
            keysList[count++] = summary.getKey();
        }

        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(S3_BUCKET).withKeys(keysList);
        amazonS3Client.deleteObjects(deleteObjectsRequest);
    }
}
