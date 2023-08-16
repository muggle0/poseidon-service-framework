spring.application.name= ${module}
nacosaddr=127.0.0.1:8848
spring.cloud.nacos.discovery.server-addr= ${'$'}{nacosaddr}
spring.cloud.nacos.discovery.namespace=psf
spring.cloud.nacos.config.server-addr= ${'$'}{nacosaddr}