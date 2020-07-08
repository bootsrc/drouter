# drouter

简介：drouter项目是Dubbo自定义router的应用。比如consumer想调用指定ip的机器上的provider。

步骤：

- step1 :启动drouter-provider

<br/>

- step2 :添加参数-Drouter_ip=you-machine-ip
比如
```text
-Drouter_ip=192.168.0.200
```
启动drouter-consumer
如果不设置system property值router_ip
则能路由到所有到provider.

<br/>

如果设置了router_ip，则只会调用这个ip机器上到provider。
从而应用了自定义router机制。

- step3: 浏览器输入 [http://localhost:12501/action?input=xxx](http://localhost:12501/action?input=xxx)
看效果。

如果输入错误的ip。页面上会报错如下
```text
No provider available for the service bootsrc.api.DemoService:0.0.1
```
输入正确的ip。页面会返回
```text
Hello, xxx
```

drouter-core可以作为一个jar包被引用。算一个框架的雏形。
drouter-api, drouter-provider, drouter-consumer仅仅是demo，用来展示drouter-core的应用的。

<br/>

## 源码解析
GrayscaleRouter.java
```java
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
```

GrayscaleRouterFactory.java
```java
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
```

## 总结
GrayscaleRouterFactory只在项目启动的过程中被调用getRouter(URL url)一次

GrayscaleRouter调用次数：
每次dubbo调用的时候，GrayscaleRouter#route(invokers)都会被调用一次。
