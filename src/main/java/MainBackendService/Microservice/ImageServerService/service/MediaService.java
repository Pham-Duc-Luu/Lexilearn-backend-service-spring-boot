package MainBackendService.Microservice.ImageServerService.service;

import java.util.concurrent.CompletableFuture;

public interface MediaService {
    boolean isAwsPresignedUrl(String url);

    String getFileNameFromUrl(String url);

    Boolean isPresignedUrlExpired(String url);

    String getPresignURl(String url, String authorizationHeader);

    CompletableFuture<String> getPresignUrlAsync(String url, String authorizationHeader);
}
