package bootsrc.drouter.core;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.cluster.Router;
import org.apache.dubbo.rpc.cluster.RouterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liushaoming
 * @date 2020-07-07 10:31 上午
 */
@Activate
// (group = {CommonConstants.CONSUMER})
public class GrayscaleRouterFactory implements RouterFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrayscaleRouterFactory.class);

    public GrayscaleRouterFactory() {
        LOGGER.info("GrayscaleRouterFactory_construct");
    }

    @Override
    public Router getRouter(URL url) {
        return new GrayscaleRouter(url);
    }
}
