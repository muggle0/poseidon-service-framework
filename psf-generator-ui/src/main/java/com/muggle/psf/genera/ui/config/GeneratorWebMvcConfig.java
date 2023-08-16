package com.muggle.psf.genera.ui.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Description
 * Date 2021/8/6
 * Created by muggle
 */
@Configuration
public class GeneratorWebMvcConfig implements WebMvcConfigurer {


    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/poseidon-ui/**")
            .addResourceLocations("classpath:/psf-ui/");
    }
}
