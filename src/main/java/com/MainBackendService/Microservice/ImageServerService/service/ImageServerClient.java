package com.MainBackendService.Microservice.ImageServerService.service;

import com.MainBackendService.Microservice.ImageServerService.dto.UserImageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "image-api", url = "${service.image.url}")
public interface ImageServerClient {
    @GetMapping("/images")
    UserImageDTO getUserImage(
            @RequestHeader(value = "Authorization", required = true) String authorizationHeader,
            @RequestParam(name = "file_name") String file_name);


    @PostMapping("/private/upload")
    UserImageDTO uploadImage(
            @RequestHeader(value = "Authorization", required = true) String authorizationHeader,
            @RequestParam("image") MultipartFile file);

}
