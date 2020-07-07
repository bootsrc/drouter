package bootsrc.consumer.controller;

import bootsrc.api.DemoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author liushaoming
 * @date 2020-07-07 10:58 上午
 */
@RestController
@RequestMapping("")
public class HomeController {
    @Resource
    private DemoService demoService;

    @RequestMapping("action")
    public String action(String input) {
        return demoService.action(input);
    }

}
