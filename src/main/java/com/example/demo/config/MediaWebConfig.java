
package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class MediaWebConfig implements WebMvcConfigurer {

    private final Path mediaDirectory;

    public MediaWebConfig(
            @Value("${app.media.dir:${user.home}/.groceries/media}") String mediaDirectory) {
        this.mediaDirectory = Path.of(mediaDirectory)
                .toAbsolutePath()
                .normalize();
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String mediaLocation = mediaDirectory.toUri().toString();

        if (!mediaLocation.endsWith("/")) {
            mediaLocation += "/";
        }

        registry.addResourceHandler("/media/**")
                .addResourceLocations(mediaLocation);
    }
}
