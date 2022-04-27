package cn.gloduck.log.entity;

import lombok.Getter;
import lombok.ToString;

/**
 * 日志实体
 *
 * @author Gloduck
 * @date 2022/04/25
 */
@Getter
@ToString
public class LogEntity {
    /**
     * 方法
     */
    private final String method;

    /**
     * 内容
     */
    private final String content;

    /**
     * 分类
     */
    private final String group;

    /**
     * 请求参数
     */
    private final Object[] params;

    /**
     * 花费时间
     */
    private final Long costTime;


    /**
     * 异常，只有执行失败的时候才会有值，默认为null
     */
    private final Throwable exception;

    public LogEntity(String method, String content, String group,  Object[] params, Long costTime, Throwable exception) {
        this.method = method;
        this.content = content;
        this.group = group;
        this.params = params;
        this.costTime = costTime;
        this.exception = exception;
    }
}
