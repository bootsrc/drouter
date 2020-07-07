# drouter

简介：drouter项目是Dubbo 自定义router的应用。

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

step3: 浏览器输入 [http://localhost:12501/action?input=xxx](http://localhost:12501/action?input=xxx)
看效果。

如果输入错误的ip。页面上会报错如下
```text
No provider available for the service bootsrc.api.DemoService:0.0.1
```
输入正确的ip。页面会返回
```text
Hello, xxx
```

