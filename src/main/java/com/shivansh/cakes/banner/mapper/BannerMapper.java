package com.shivansh.cakes.banner.mapper;

import com.shivansh.cakes.banner.dto.response.BannerResponse;
import com.shivansh.cakes.banner.entity.Banner;
import org.springframework.stereotype.Component;

@Component
public class BannerMapper {
    public BannerResponse toResponse(Banner banner) {
        return new BannerResponse(
                banner.getId(),
                banner.getBanner(),
                banner.getTitle(),
                banner.getCaption(),
                banner.getCategory()
        );
    }
}
