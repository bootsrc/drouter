package bootsrc.drouter.core;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.cluster.Router;
import org.apache.dubbo.rpc.cluster.RouterFactory;

/**
 * @author liushaoming
 * @date 2020-07-07 10:31 上午
 */
@Activate
public class GrayscaleRouterFactory implements RouterFactory {
    @Override
    public Router getRouter(URL url) {
        return new GrayscaleRouter(url);
    }
}
