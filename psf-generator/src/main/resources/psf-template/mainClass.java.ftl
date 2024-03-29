package ${projectPackage};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Profile;

/**
* @author muggle
* @Description
* @createTime 2020-12-18
*/

@SpringBootApplication
@EnableAsync
@EnableDiscoveryClient
@EnableScheduling
public class ${otherField.className} {
public static void main(String[] args) {
SpringApplication.run(${otherField.className}.class, args);
}


}
