package com.winter.github.distribute.auto;

import com.winter.github.distribute.aop.CombineAspect;
import com.winter.github.distribute.task.ShardTaskHelper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动初始化,加载切面<br>
 *
 * @author zhangdongdong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @date 2020年05月26日 09:32:22 <br>
 */
@EnableConfigurationProperties(DistributeTaskProperties.class)
@Configuration
public class DistributeDataAutoConfigure {

    @Bean
    public CombineAspect combineAspect() {
        return new CombineAspect();
    }

    @Bean
    public ShardTaskHelper shardTaskHelper0(){
        return new ShardTaskHelper();
    }

}
