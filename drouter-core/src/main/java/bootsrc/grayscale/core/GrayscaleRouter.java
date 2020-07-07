package bootsrc.grayscale.core;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liushaoming
 * @date 2020-07-07 10:07 上午
 */
public class GrayscaleRouter implements Router, Comparable<Router> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrayscaleRouter.class);

    private URL url;
    private int priority = 0;

    public GrayscaleRouter(URL url) {
        this.url = url;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public <T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        if (invokers == null) {
            return null;
        }
        String ip = RpcContext.getContext().getAttachment(GrayscaleConstants.ROUTER_IP);
        if (StringUtils.isEmpty(ip)) {
            ip = System.getProperty(GrayscaleConstants.ROUTER_IP);
        }
        if (StringUtils.isEmpty(ip)) {
            return invokers;
        } else {
            RpcContext.getContext().setAttachment(GrayscaleConstants.ROUTER_IP, ip);
        }

        List<Invoker<T>> result = new ArrayList<>(invokers.size());

        for (Invoker<T> invoker : invokers) {
            String invokerHost = invoker.getUrl().getHost();
            LOGGER.info("invokerHost={}", invokerHost);
            if (ip.equals(invoker.getUrl().getIp()) || ip.equals(invokerHost)) {
                result.add(invoker);
            }
        }
        return result;
    }

    @Override
    public boolean isRuntime() {
        return false;
    }

    @Override
    public boolean isForce() {
        return false;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(Router o) {
        if (o == null || o.getClass() != GrayscaleRouter.class) {
            return 1;
        }
        GrayscaleRouter c = (GrayscaleRouter) o;
        return this.priority == c.priority ? url.toFullString().compareTo(c.url.toFullString())
                : (this.priority > c.priority ? 1 : -1);
    }

    /**
     * 排除本地服务
     *
     * @param invokers
     * @return
     */
    @Deprecated
    private <T> List<Invoker<T>> excludeLocalService(List<Invoker<T>> invokers) {
        List<Invoker<T>> result = new ArrayList<Invoker<T>>();
        invokers.forEach(invoker -> {
            String providerHost = invoker.getUrl().getHost();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("providerHost={}", providerHost);
            }
            if (!providerHost.equals(GrayscaleConstants.LOCALHOST) && !providerHost.startsWith("172.") && !providerHost.startsWith("192.") && !providerHost.startsWith("127.0.0.1")) {
                result.add(invoker);
            }
        });
        return result;
    }
}
