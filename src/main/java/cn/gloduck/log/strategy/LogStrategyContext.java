package cn.gloduck.log.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 日志策略上下文
 *
 * @author Gloduck
 * @date 2022/04/25
 */
public class LogStrategyContext {
    private final Logger logger = LoggerFactory.getLogger(LogStrategyContext.class);
    private final Map<String, LogSaveStrategy> logStrategyMap;
    private static final LogStrategyContext SINGLETON = new LogStrategyContext();
    public LogStrategyContext() {
        this.logStrategyMap = new HashMap<>(4);
    }

    /**
     * 注册日志保存策略
     *
     * @param strategy 策略
     */
    public static  <T extends LogSaveStrategy> void  registryStrategy(T strategy){
        if(SINGLETON.logStrategyMap.containsKey(strategy.getStrategyName())){
            SINGLETON.logger.warn("Register strategy ({}) failed, the strategy with same name already exist in container", strategy.getStrategyName());
        }
        SINGLETON.logStrategyMap.put(strategy.getStrategyName(), strategy);
    }

    /**
     * 获取日志保存策略
     *
     * @param name 名字 {@link LogSaveStrategy#getStrategyName()}
     * @return {@link LogSaveStrategy}
     */
    public static LogSaveStrategy getStrategy(String name){
        return SINGLETON.logStrategyMap.get(name);
    }
}
