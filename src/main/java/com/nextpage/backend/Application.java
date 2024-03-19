package com.nextpage.backend;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:env.properties")
@SpringBootApplication
public class Application {

	@Value("${AWS_ACCESS_KEY}")
	private String accessKey;

	@Value("${AWS_SECRET_KEY}")
	private String secretKey;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public AmazonS3 amazonS3Client() {
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
		return AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds))
				.withRegion(Regions.AP_NORTHEAST_2)
				.build();
	}
}
