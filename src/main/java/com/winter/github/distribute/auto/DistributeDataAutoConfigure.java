package com.winter.github.distribute.auto;

import com.winter.github.distribute.aop.CombineAspect;
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
@Configuration
public class DistributeDataAutoConfigure {

    @Bean
    public CombineAspect combineAspect() {
        return new CombineAspect();
    }

}
