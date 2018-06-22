package com.xiaoyu.common.configuration;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;

/**
 * @author hongyu
 * @date 2018-06
 * @description 自定义tomcat apr模式需要给服务器安装额外的包,就算了
 */
@Deprecated
//@Component
public class TomcatContainerCustomizer implements EmbeddedServletContainerCustomizer {

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        if (!(container instanceof TomcatEmbeddedServletContainerFactory)) {
            return;
        }
        TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
        tomcat.setProtocol(Http11NioProtocol.class.getName());
        tomcat.addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
            }
        });
    }
}