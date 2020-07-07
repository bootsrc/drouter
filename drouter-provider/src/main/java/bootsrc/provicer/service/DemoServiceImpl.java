package bootsrc.provicer.service;

import bootsrc.api.DemoService;
import org.apache.dubbo.config.annotation.Service;

/**
 * @author liushaoming
 * @date 2020-07-07 10:53 上午
 */
@Service(version = "0.0.1")
public class DemoServiceImpl implements DemoService {
    @Override
    public String action(String input) {
        return "Hello, " + input;
    }
}
