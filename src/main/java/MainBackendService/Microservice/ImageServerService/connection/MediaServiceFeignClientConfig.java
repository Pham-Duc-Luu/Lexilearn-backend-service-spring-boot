package MainBackendService.Microservice.ImageServerService.connection;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MediaServiceFeignClientConfig {

    @Value("${service.media.api.key}")
    private String mediaApiKey;

    @Bean
    public RequestInterceptor apiKeyRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                template.header("x-api-key", mediaApiKey);
            }
        };
    }


}
