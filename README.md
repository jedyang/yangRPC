## yangRPC
一个简易版RPC框架模型

### 代码模块

- provider   服务提供者。负责发布RPC服务，接受和处理请求
- consumer   服务消费者。使用动态代理发起请求，负载均衡算法。
- registry  注册中心。zookeeper实现。服务注册，服务发现，负载均衡。
- protocol   通信协议相关。编解码，序列化与反序列化
- common    公共类库。统一封装请求与响应。
- api   堆外暴露的服务接口，主要用于测试
