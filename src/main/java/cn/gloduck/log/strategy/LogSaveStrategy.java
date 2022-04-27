package cn.gloduck.log.strategy;


import cn.gloduck.log.entity.LogEntity;

/**
 * 日志保存策略
 * 实现了此策略，并将其注册成Bean，则可以使用。或者调用{@link LogStrategyContext#registryStrategy(LogSaveStrategy)}注册
 *
 * @author Gloduck
 * @date 2022/04/25
 */
public interface LogSaveStrategy {
    /**
     * 保存日志
     *
     * @param log 日志
     */
    void saveLog(LogEntity log);

    /**
     * 得到策略名称
     *
     * @return {@link String}
     */
    String getStrategyName();
}
