package capstone.demo.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AwsS3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String s3AccessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String s3SecretKey;

    @Value("${cloud.aws.region.static}")
    private String s3Region;

    @Bean
    public S3Client amazonS3() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(s3AccessKey, s3SecretKey);

        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(s3Region))
                .build();
    }

    @Bean
    public S3Presigner amazonS3Presigner() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(s3AccessKey, s3SecretKey);

        return S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(s3Region))
                .build();
    }

}
