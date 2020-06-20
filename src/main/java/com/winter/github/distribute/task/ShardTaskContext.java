package com.winter.github.distribute.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Date;

/**
 * 分片任务上下文<br>
 *
 * @author zhangdongdong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @date 2020年06月06日 23:27:42 <br>
 */
@Data
public class ShardTaskContext implements Serializable {

    private String taskKey;

    /**
     * 任务元数据
     */
    private JSONObject taskMeta;

    /**
     * 分片参数
     */
    private JSONObject shardParams;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * Description: 反序列化任务内容<br>
     *
     * @param type
     * @return T <br>
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    public <T> T parseMeta(Class<T> type) {
        if (taskMeta == null) {
            return null;
        }
        return taskMeta.toJavaObject(type);
    }

    /**
     * Description: 反序列化任务分片参数<br>
     *
     * @param type
     * @return T <br>
     * @author zhangdongdong <br>
     * @taskId <br>
     */
    public <T> T parseShardParams(Class<T> type) {
        if (shardParams == null) {
            return null;
        }
        return shardParams.toJavaObject(type);
    }

    public ShardTaskContext() {
    }

    public ShardTaskContext(Object taskMeta, String taskKey) {
        Assert.notNull(taskMeta, "task meta obj not null");
        Assert.notNull(taskKey, "task key not null");
        this.taskMeta = (JSONObject) JSON.toJSON(taskMeta);
        this.taskKey = taskKey;
        this.createTime = new Date();
    }

    public ShardTaskContext(Object taskMeta, String taskKey, Object shardParams) {
        this(taskMeta, taskKey);
        if (shardParams != null) {
            this.shardParams = (JSONObject) JSON.toJSON(shardParams);
        }
    }

}
