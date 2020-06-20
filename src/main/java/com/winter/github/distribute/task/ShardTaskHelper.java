package com.winter.github.distribute.task;

import com.winter.github.distribute.auto.DistributeTaskProperties;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * <br>
 *
 * @author zhangdongdong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @date 2020年06月06日 23:45:22 <br>
 */
@Slf4j
public class ShardTaskHelper {
    /**
     * 存储任务队列
     */
    @Resource
    private RedisTemplate<String, ShardTaskContext> redisTemplate;

    /**
     * 存储时间戳
     */
    @Resource
    private RedisTemplate<String, Long> longRedisTemplate;

    /**
     * 分片任务参数
     */
    @Resource
    private DistributeTaskProperties distributeTaskProperties;

    /**
     * redisson客户端
     */
    @Resource
    private RedissonClient redissonClient;

    /**
     * Description: 种植任务<br>
     *
     * @param shardTaskContexts
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    public void plantingTask(List<ShardTaskContext> shardTaskContexts) {
        if (CollectionUtils.isEmpty(shardTaskContexts)) {
            log.warn("not find task contexts ,skip handle");
            return;
        }
        ShardTaskContext sample = shardTaskContexts.get(0);
        String timestampKey = String.format(distributeTaskProperties.getTaskStampKeyPatten(), sample.getTaskKey());
        String lockKey = String.format(distributeTaskProperties.getTaskLockKeyPattern(), sample.getTaskKey());
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.lock();
            Long lastTime = longRedisTemplate.opsForValue().get(timestampKey);
            if (lastTime == null || (Math.abs(lastTime - sample.getCreateTime().getTime()) > distributeTaskProperties.getMinTimeMillSeconds())) {
                //检查时间戳是否过期
                log.info("last plant task for key :{} is [{}] to early or not find ,plat new tasks [{}]", timestampKey, lastTime, shardTaskContexts);
                String queueKey = String.format(distributeTaskProperties.getTaskQueueKeyPattern(), sample.getTaskKey());
                redisTemplate.opsForList().rightPushAll(queueKey, shardTaskContexts);
                longRedisTemplate.opsForValue().set(timestampKey, sample.getCreateTime().getTime());
            } else {
                log.info("last plant task for key :{} is [{}] , skip plant tasks ", timestampKey, new Date(lastTime));
            }
        } catch (Exception e) {
            log.error("plant tasks :{} error", shardTaskContexts);
        } finally {
            lock.unlock();
            log.info("unlock for key :{} success ", lockKey);
        }
    }

    /**
     * Description: 取任务<br>
     *
     * @param taskKey
     * @return com.minivision.marmot.task.ShardTaskContext <br>
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    public ShardTaskContext fetchTasks(String taskKey) {
        String queueKey = String.format(distributeTaskProperties.getTaskQueueKeyPattern(), taskKey);
        return redisTemplate.opsForList().leftPop(queueKey);
    }

}
