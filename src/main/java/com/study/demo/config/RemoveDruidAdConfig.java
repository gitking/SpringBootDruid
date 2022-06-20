package com.study.demo.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.util.Utils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.*;
import java.io.IOException;

/**
 * https://mp.weixin.qq.com/s/_8chazf6PUOOOhTOLUl91A
 * 8 去 Ad（广告）
 * 访问监控页面的时候，你可能会在页面底部（footer）看到阿里巴巴的广告
 * 原因：引入的druid的jar包中的common.js(里面有一段js代码是给页面的footer追加广告的)
 * 如果想去掉，有两种方式：
 * (1) 直接手动注释这段代码
 * 如果是使用Maven，直接到本地仓库中，查找这个jar包
 * 要注释的代码：
 * // this.buildFooter();
 * common.js的位置：
 * com/alibaba/druid/1.1.23/druid-1.1.23.jar!/support/http/resources/js/common.js
 * (2) 使用过滤器过滤
 * 注册一个过滤器，过滤common.js的请求，使用正则表达式替换相关的广告内容
 *
 */
@Configurable
@ConditionalOnWebApplication
@AutoConfigureAfter(DruidDataSourceAutoConfigure.class)
@ConditionalOnProperty(name = "spring.datasource.druid.stat-view-servlet.enabled", havingValue = "true", matchIfMissing = true)
public class RemoveDruidAdConfig {

    /**
     * 去除druid监控页面自带的底部广告
     * @param druidStatProperties
     * @return
     */
    @Bean
    public FilterRegistrationBean removeDruidAdFilterRegistrationBean(DruidStatProperties druidStatProperties) {
        // 获取Web监控页面的参数
        DruidStatProperties.StatViewServlet config = druidStatProperties.getStatViewServlet();
        //提取common.js的配置路径
        String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*";

        String commonJsPattern = pattern.replaceAll("\\*", "js/common.js");

        final String filePath = "support/http/resources/js/common.js";

        //创建Filter 进行过滤
        Filter filter = new Filter() {

            @Override
            public void init(FilterConfig filterConfig) throws ServletException {}

            @Override
            public void doFilter(ServletRequest request, ServletResponse reponse, FilterChain chain) throws IOException,ServletException {
                // 重置缓冲区，响应头不会被重置
                reponse.resetBuffer();
                // 获取common.js
                String text = Utils.readFromResource(filePath);

                // 正则替换banner，除去底部的广告信息
                text = text.replaceAll("<a.*?banner\"></a><br/>","");
                text = text.replaceAll("powered.*?shrek.wang</a>","");
                reponse.getWriter().write(text);
                chain.doFilter(request, reponse);
            }

            @Override
            public void destroy(){}

        };

        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns(commonJsPattern);
        return registrationBean;
    }
}
