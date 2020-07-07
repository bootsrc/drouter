package bootsrc.consumer;

import bootsrc.api.DemoService;
import org.apache.dubbo.config.spring.ReferenceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liushaoming
 * @date 2020-07-07 11:00 上午
 */
@Configuration
public class Config {
    @Bean
    public ReferenceBean<DemoService> demoServiceReferenceBean() {
        ReferenceBean<DemoService> referenceBean = new ReferenceBean<>();
        referenceBean.setInterface(DemoService.class);
        referenceBean.setVersion("0.0.1");
        return referenceBean;
    }
}
