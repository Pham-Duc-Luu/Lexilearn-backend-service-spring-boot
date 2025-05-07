package MainBackendService.Microservice.ImageServerService.service;

import MainBackendService.Microservice.ImageServerService.connection.MediaServiceFeignClientConfig;
import MainBackendService.Microservice.ImageServerService.dto.UserAudioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "audio-api", url = "${service.media.url}", configuration = MediaServiceFeignClientConfig.class)
public interface AudioServiceClient {
    @GetMapping("/audio")
    UserAudioDTO getUserAudio(
            @RequestHeader(value = "Authorization", required = true) String authorizationHeader,
            @RequestParam(name = "file_name") String file_name);


    @PostMapping("/audio/private/upload")
    UserAudioDTO uploadAudio(
            @RequestHeader(value = "Authorization", required = true) String authorizationHeader,
            @RequestParam("audio") MultipartFile file);

}
