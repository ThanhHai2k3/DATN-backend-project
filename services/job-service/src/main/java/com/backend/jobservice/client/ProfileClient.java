package com.backend.jobservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "profile-service", url = "${services.profile.base-url}")
public interface ProfileClient {

    //Chưa xây dựng API này, sẽ chỉnh sửa lại profile service cho đồng nhất
    @GetMapping("/api/profile/v1/employers/{id}/company")
    UUID getCompanyIdByEmployer(@PathVariable("id") UUID employerId);
}
