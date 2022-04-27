package cn.gloduck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * 单元测试应用程序
 *
 * @author Gloduck
 * @date 2022/04/13
 */
@SpringBootApplication
public class Bootstrap extends WebMvcConfigurationSupport {
    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class);

    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converters.add(converter);
    }
}
