package com.nextpage.backend.service;

import com.sksamuel.scrimage.ImmutableImage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import com.sksamuel.scrimage.webp.WebpWriter;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

@Service
public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);
    private final AmazonS3 amazonS3;

    @Value("${AWS_BUCKET}")
    private String bucketName;

    public ImageService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String uploadImageToS3(String imageUrl) {
        try {
            // 이미지를 파일 형태로 다운로드
            File imageFile = downloadImage(imageUrl);
            if (imageFile == null) {
                logger.error("Failed to download image from URL: {}", imageUrl);
                return null;
            }

            // 원본 파일 크기 출력
            logger.info("원본 파일 크기 : " + imageFile.length() + " bytes");

            // Thumbnailator 라이브러리를 사용하여 이미지 리사이징
            BufferedImage resizedImage = Thumbnails.of(imageFile)
                    .size(512, 512)
                    .asBufferedImage();

            // 리사이징된 BufferedImage를 임시 파일로 저장
            File tempFile = new File(imageFile.getParent(), "temp_" + System.currentTimeMillis() + ".png");
            ImageIO.write(resizedImage, "png", tempFile);

            // 임시 파일 크기 출력
            logger.info("리사이징 후 크기 : " + tempFile.length() + " bytes");

            // Scrimage 라이브러리를 사용하여 리사이징된 이미지를 WebP로 변환하고 파일로 저장
            ImmutableImage image = ImmutableImage.loader().fromFile(tempFile);
            File outputFile = new File(tempFile.getParent(), System.currentTimeMillis() + ".webp");
            image.output(WebpWriter.DEFAULT, outputFile);

            // 변환된 WebP 파일 크기 출력
            logger.info("리사이징 + Webp 적용 후 크기 : " + outputFile.length() + " bytes");

            // 파일의 길이를 가져와서 메타데이터에 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(outputFile.length());

            // 파일을 S3에 업로드
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, outputFile.getName(), new FileInputStream(outputFile), metadata);
            amazonS3.putObject(putObjectRequest);

            // 업로드된 파일의 URL 반환
            return amazonS3.getUrl(bucketName, outputFile.getName()).toString();
        } catch (Exception e) {
            logger.error("Image processing failed. Cause: {}", e.getMessage(), e);
            throw new RuntimeException("Image processing failed. Cause: " + e.getMessage(), e);
        }
    }

    private File downloadImage(String imageUrl) {
        logger.info("Downloading image to file from URL: {}", imageUrl);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(imageUrl))
                .build();

        try {
            // 임시 파일 생성
            File tempFile = File.createTempFile("image", ".tmp");
            // 파일로 직접 다운로드
            HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(tempFile.toPath()));
            int statusCode = response.statusCode();
            if (statusCode >= 200 && statusCode < 300) {
                return tempFile;
            } else {
                logger.error("Error fetching image. Status: {}", statusCode);
                return null;
            }
        } catch (Exception e) {
            logger.error("Failed to download image to file. URL: {}, Error: {}", imageUrl, e.getMessage(), e);
            return null;
        }
    }
}
