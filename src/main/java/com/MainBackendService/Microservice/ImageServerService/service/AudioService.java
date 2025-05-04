package com.MainBackendService.Microservice.ImageServerService.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

@Service("audioService")
public class AudioService implements MediaService {
    Logger logger = LogManager.getLogger(AudioService.class);
    @Autowired
    private AudioServiceClient audioServiceClient;

    public String getPresignURl(String url, String authorizationHeader) {
        if (url == null) {
            return null;
        }

        if (!isAwsPresignedUrl(url)) {
            return url;
        }

        String filename = getFileNameFromUrl(url);
        if (filename == null) {
            return url;

        }
        return audioServiceClient.getUserAudio(authorizationHeader, filename).getPublicUrl();

    }

    public Boolean isPresignedUrlExpired(String presignedUrl) {
//        try {
//            URL url = new URL(presignedUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("HEAD"); // Use HEAD to avoid downloading the file
//            connection.setConnectTimeout(5000);
//            connection.setReadTimeout(5000);
//            int responseCode = connection.getResponseCode();
//
//            // 200 = OK, 403 = Forbidden (could be expired), 404 = Not Found
//            return responseCode != 200;
//        } catch (IOException e) {
//            // If there's an error (e.g., timeout or unreachable), assume expired
//            logger.error("Error checking presigned URL: " + e.getMessage());
//            return true;
//        }
        return true;
    }

    @Async
    public CompletableFuture<String> getPresignUrlAsync(String url, String authorizationHeader) {
        try {
            if (url == null || !isAwsPresignedUrl(url)) {
                return CompletableFuture.completedFuture(url);
            }

            if (!isPresignedUrlExpired(url)) {
                return CompletableFuture.completedFuture(url);
            }

            String filename = getFileNameFromUrl(url);
            if (filename == null) return CompletableFuture.completedFuture(url);

            String newUrl = audioServiceClient.getUserAudio(authorizationHeader, filename).getPublicUrl();
            return CompletableFuture.completedFuture(newUrl);

        } catch (Exception e) {
            // Log the error but return null safely
            System.err.println("Error while fetching presigned audio URL: " + e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }

    public boolean isAwsPresignedUrl(String url) {
        try {
            URI uri = new URI(url);
            String query = uri.getQuery();
            return query != null && query.contains("X-Amz-Signature")
                    && query.contains("X-Amz-Date")
                    && query.contains("X-Amz-Algorithm");
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public String getFileNameFromUrl(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            return path.substring(path.lastIndexOf('/') + 1);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
