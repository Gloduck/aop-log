package cn.gloduck.log.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 日志策略注册类
 *
 * @author Gloduck
 * @date 2022/04/25
 */
@Component
public class LogStrategyRegistry implements ApplicationContextAware {
    private final Logger logger = LoggerFactory.getLogger(LogStrategyRegistry.class);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, LogSaveStrategy> saveStrategyMap = applicationContext.getBeansOfType(LogSaveStrategy.class);
        saveStrategyMap.forEach((beanName, logSaveStrategy) -> {
            logger.info("Register log save strategy ({}) in container.", logSaveStrategy.getStrategyName());
            LogStrategyContext.registryStrategy(logSaveStrategy);
        });
    }
}
