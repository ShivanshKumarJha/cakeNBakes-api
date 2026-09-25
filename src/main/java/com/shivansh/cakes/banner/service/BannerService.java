package com.shivansh.cakes.banner.service;

import com.shivansh.cakes.banner.dto.request.BannerRequest;
import com.shivansh.cakes.banner.dto.response.BannerResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BannerService {
    List<BannerResponse> findAll();
    BannerResponse findById(Long id);
    BannerResponse create(BannerRequest request, MultipartFile image);
    BannerResponse update(Long id, BannerRequest request, MultipartFile image);
    void delete(Long id);
}
