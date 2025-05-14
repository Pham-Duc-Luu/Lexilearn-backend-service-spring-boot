package MainBackendService.Microservice.ImageServerService.service;

import MainBackendService.Microservice.ImageServerService.connection.MediaServiceFeignClientConfig;
import MainBackendService.Microservice.ImageServerService.dto.ImageDto;
import MainBackendService.Microservice.ImageServerService.dto.UserImageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "image-api", url = "${service.media.url}", configuration = MediaServiceFeignClientConfig.class)
public interface ImageServerClient {
    @GetMapping("/images")
    UserImageDTO getUserImage(
            @RequestHeader(value = "Authorization", required = true) String authorizationHeader,
            @RequestParam(name = "file_name") String file_name);


    @PostMapping(value = "/images/private/upload", consumes = "multipart/form-data")
    ImageDto uploadImage(
            @RequestHeader(value = "Authorization", required = true) String authorizationHeader,
            @RequestPart("image") MultipartFile file);
}
