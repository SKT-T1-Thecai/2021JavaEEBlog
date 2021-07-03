package com.zl.config;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.FileNotFoundException;

@Configuration
public class CrosConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");
    }

    @SneakyThrows
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        File path = new File(ResourceUtils.getURL("src/main/resources/").getPath());
        String baseUrl = path.getAbsolutePath();
        System.out.println(baseUrl);
        //D:\library\src\main\resources
//        registry.addResourceHandler("/HeadImage/**").addResourceLocations("file:D:/library/src/main/" +
//                "resources/HeadImage/");
        registry.addResourceHandler("/HeadImage/**").addResourceLocations("file:"+baseUrl.replace('\\','/')
        +"/HeadImage/");
    }
}


