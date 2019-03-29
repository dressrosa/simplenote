/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.xiaoyu.beacon.starter.EnableBeacon;

/**
 * @author hongyu
 * @date 2018-08
 * @description
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableBeacon
public class SimlenoteUserApplication {

    public static void main(String args[]) {
        SpringApplication app = new SpringApplication(SimlenoteUserApplication.class);
        app.setWebEnvironment(false);
        app.run(args);
    }

}
