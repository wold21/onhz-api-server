package com.onhz.server.config;

import com.onhz.server.interceptor.SessionUpdateInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer{
    private final SessionUpdateInterceptor sessionUpdateInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionUpdateInterceptor)
                .addPathPatterns("/api/**");
    } 

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new ReviewEnumConverter());
        registry.addConverter(new StringToLongConverter());
        registry.addConverter(new StringToIntegerConverter());
    }

    public static class StringToLongConverter implements Converter<String, Long> {
        @Override
        public Long convert(String source) {
            if (source == null || source.isEmpty() || "null".equals(source)) {
                return null;
            }
            return Long.valueOf(source);
        }
    }

    public static class StringToIntegerConverter implements Converter<String, Integer> {
        @Override
        public Integer convert(String source) {
            if (source == null || source.isEmpty() || "null".equals(source)) {
                return null;
            }
            return Integer.valueOf(source);
        }
    }
}
