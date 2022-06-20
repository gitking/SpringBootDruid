package com.study.demo.druid.controller;

import com.alibaba.druid.stat.DruidStatManagerFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 9 获取 Druid 的监控数据
 * Druid 的监控数据可以在 开启 StatFilter 后，通过 DruidStatManagerFacade 进行获取;
 * DruidStatManagerFacade#getDataSourceStatDataList 该方法可以获取所有数据源的监控数据，
 * 除此之外 DruidStatManagerFacade 还提供了一些其他方法，可以按需选择使用。
 * 来源：https://blog.csdn.net/weixin_44730681/article/details/107944048
 */
@RestController
@RequestMapping(value = "/druid")
public class DruidStatController {
    @GetMapping("/stat")
    public Object druidStat(){
        //获取Druid数据源的监控数据
        return DruidStatManagerFacade.getInstance().getDataSourceStatDataList();
    }
}
