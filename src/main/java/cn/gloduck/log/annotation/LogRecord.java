package cn.gloduck.log.annotation;


import cn.gloduck.log.strategy.LogSaveStrategy;

import java.lang.annotation.*;

/**
 * 日志
 *
 * @author Gloduck
 * @date 2022/04/25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface LogRecord {
    /**
     * 方法名称
     *
     * @return {@link String}
     */
    String method() default "";

    /**
     * 内容，使用占位符${}来编写el表达式，同时不要有不匹配的{}括号，否则会导致解析出错。
     * 编写示例如下：
     * <pre>
     *     "用户：${{transfer.fromUser}} 向用户：${{transfer.toUser}}转了一笔账，时间为：${T(System).currentTimeMillis()}"
     * </pre>
     * 上面例子中的【{transfer.fromUser}】、【{transfer.toUser}】、【T(System).currentTimeMillis()】均为SpEl表达式，会使用SpEl进行解析。
     * 如果因为缺少必要参数等原因导致无法解析，解析后的值将会为占位符里面的值。
     *
     * @return {@link String}
     */
    String content() default "";

    /**
     * 日志分类
     *
     * @return {@link String}
     */
    String group() default "";

    /**
     * 保存条件，支持SpringEl表达式，返回true才能保存
     *
     * @return {@link String}
     */
    String condition() default "true";

    /**
     * 保存环境
     * 此参数和{@link LogSaveStrategy#getStrategyName()}对应。
     *
     * @return {@link String}
     */
    String saveStrategy();

}
