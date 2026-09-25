package com.shivansh.cakes.banner.service.impl;

import com.shivansh.cakes.banner.dto.request.BannerRequest;
import com.shivansh.cakes.banner.dto.response.BannerResponse;
import com.shivansh.cakes.banner.entity.Banner;
import com.shivansh.cakes.banner.mapper.BannerMapper;
import com.shivansh.cakes.banner.repository.BannerRepository;
import com.shivansh.cakes.banner.service.BannerService;
import com.shivansh.cakes.common.exception.BusinessException;
import com.shivansh.cakes.common.exception.ResourceNotFoundException;
import com.shivansh.cakes.common.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;
    private final BannerMapper bannerMapper;
    private final FileStorageService fileStorageService;

    public BannerServiceImpl(BannerRepository bannerRepository, BannerMapper bannerMapper,
                             FileStorageService fileStorageService) {
        this.bannerRepository = bannerRepository;
        this.bannerMapper = bannerMapper;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponse> findAll() {
        return bannerRepository.findAll().stream().map(bannerMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BannerResponse findById(Long id) {
        return bannerMapper.toResponse(getOrThrow(id));
    }

    @Override
    public BannerResponse create(BannerRequest request, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new BusinessException("Banner image is required");
        }
        String imageUrl = fileStorageService.store(image, "banner-img");

        Banner banner = new Banner();
        banner.setBanner(imageUrl);
        banner.setTitle(request.title());
        banner.setCaption(request.caption());
        banner.setCategory(request.category());
        return bannerMapper.toResponse(bannerRepository.save(banner));
    }

    @Override
    public BannerResponse update(Long id, BannerRequest request, MultipartFile image) {
        Banner banner = getOrThrow(id);
        banner.setTitle(request.title());
        banner.setCaption(request.caption());
        banner.setCategory(request.category());

        if (image != null && !image.isEmpty()) {
            fileStorageService.delete(banner.getBanner());
            banner.setBanner(fileStorageService.store(image, "banner-img"));
        }
        return bannerMapper.toResponse(bannerRepository.save(banner));
    }

    @Override
    public void delete(Long id) {
        // BRS §8.4: cannot delete the last banner
        if (bannerRepository.count() <= 1) {
            throw new BusinessException("Cannot delete the last banner");
        }
        Banner banner = getOrThrow(id);
        fileStorageService.delete(banner.getBanner());
        bannerRepository.delete(banner);
    }

    private Banner getOrThrow(Long id) {
        return bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found: " + id));
    }
}
