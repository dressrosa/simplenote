/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author xiaoyu 2016年3月15日
 *         二者作用相同
 *         (@ComponentScan 注解会告诉Spring递归搜索
 *         包和它的子路径下直接或者是间接地标记了Spring的@Component注解的类)
 * @Configuration
 * @ComponentScan
 * @EnableAutoConfiguration
 */
@SpringBootApplication
@EnableTransactionManagement
public class SimlenoteApplication {

    public static void main(String args[]) {
        SpringApplication.run(SimlenoteApplication.class);
    }
}
