package com.individuals.feign;

import com.client.api.UserApi;
import com.individuals.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-api", url = "${user.service.url}", configuration = FeignConfig.class)
public interface UserServiceClient extends UserApi {
}
