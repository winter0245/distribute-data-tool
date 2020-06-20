package com.winter.github.distribute.auto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

/**
 * <br>
 *
 * @author zhangdongdong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @date 2020年06月20日 22:56:07 <br>
 */
@Slf4j
@ConfigurationProperties("distribute.task")
@Data
public class DistributeTaskProperties {
    /**
     * 任务时间戳key模板
     */
    private static final String TIME_STAMP_KEY_PATTERN = "distribute:task:%s:timestamp";

    /**
     * 任务队列key模块
     */
    private static final String TASK_QUEUE_KEY_PATTERN = "distribute:task:%s:queue";

    /**
     * 分布式锁模板
     */
    private static final String TASK_LOCK_KEY_PATTERN = "distribute:task:%s:lock";

    /**
     * 记录任务上次执行的时间戳key模板(必须要有占位符%s)
     */
    private String taskStampKeyPatten = TIME_STAMP_KEY_PATTERN;

    /**
     * 记录任务队列的key模板(必须要有占位符%s)
     */
    private String taskQueueKeyPattern = TASK_QUEUE_KEY_PATTERN;

    /**
     * 记录任务分布式锁的key模板(必须要有占位符%s)
     */
    private String taskLockKeyPattern = TASK_LOCK_KEY_PATTERN;

    /**
     * 允许最小的任务偏差值(毫秒)
     */
    private long minTimeMillSeconds = 5000;

    /**
     * 参数校验
     */
    @PostConstruct
    public void initCheck() {
        if (StringUtils.isAnyBlank(taskLockKeyPattern, taskQueueKeyPattern, taskStampKeyPatten)) {
            throw new IllegalArgumentException("some task key pattern(s) is empty");
        }
        String now = String.valueOf(System.currentTimeMillis());
        if (taskLockKeyPattern.equals(String.format(taskLockKeyPattern, now))) {
            throw new IllegalArgumentException("taskLockKeyPattern pattern is invalid");
        }
        if (taskQueueKeyPattern.equals(String.format(taskQueueKeyPattern, now))) {
            throw new IllegalArgumentException("taskQueueKeyPattern pattern is invalid");
        }
        if (taskStampKeyPatten.equals(String.format(taskStampKeyPatten, now))) {
            throw new IllegalArgumentException("taskStampKeyPatten pattern is invalid");
        }
        if (minTimeMillSeconds <= 0) {
            throw new IllegalArgumentException("minTimeMillSeconds  is invalid");
        }
        log.info("taskLockKeyPattern :{}, taskQueueKeyPattern :{}, taskStampKeyPatten :{} , minTimeMillSeconds:{}", taskLockKeyPattern,
                taskQueueKeyPattern,
                taskStampKeyPatten, minTimeMillSeconds);
    }
}
