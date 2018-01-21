package com.xiaoyu.modules.biz.test;

import java.util.concurrent.CountDownLatch;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.xiaoyu.SimlenoteApplication;
import com.xiaoyu.modules.biz.article.service.api.IArticleService;
import com.xiaoyu.modules.biz.user.service.api.IUserService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SimlenoteApplication.class)
public class TestBiz {

    @Autowired
    private IArticleService articleService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ConfigurableApplicationContext context;
    
    @Test
    public void test() {
        HttpServletRequest request = new MockHttpServletRequest();
        System.out.println(JSON.toJSONString(JSON.parse(this.userService.commonNums(request, "1")), true));
    }

    @After
    public void after() throws Exception {
        System.out.println("context已关闭");
        context.stop();
        context.close();
        new CountDownLatch(1).await();
    }

}
