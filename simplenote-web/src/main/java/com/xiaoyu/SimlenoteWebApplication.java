/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.xiaoyu.beacon.autoconfigure.EnableBeacon;

/**
 * @author hongyu
 * @date 2018-08
 * @description
 */
@SpringBootApplication
@EnableBeacon
public class SimlenoteWebApplication {

    public static void main(String args[]) {
       SpringApplication.run(SimlenoteWebApplication.class, args);
    }

}
