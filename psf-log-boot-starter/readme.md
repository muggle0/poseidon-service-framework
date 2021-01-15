对 log 模块的要求

1. 完全解耦，只依赖于 common 和parent(parent 本身只能做版本控制，而common主要用于暴露接口)

2. 灵活配置日志输出方式 feign 输出 mq 输出 logstash 输出

3. 将来能被控制台采集

over