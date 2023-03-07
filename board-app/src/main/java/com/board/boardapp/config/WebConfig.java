package com.board.boardapp.config;

import com.board.boardapp.interceptor.RefererInterceptor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.board.boardapp")
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RefererInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**"
                                        , "/js/**"
                                        , "/board/boardList"
                                        , "/imageBoard/imageBoardList"
                                        , "/imageBoard/imageBoardDetail"
                                        , "/comment/hierarchicalBoardComment"
                                        , "/comment/imageBoardComment"
                                        , "/member/join"
                                        , "/member/loginForm"
                );

    }
}
