package cn.gloduck.service.strategy;

import cn.gloduck.log.entity.LogEntity;
import cn.gloduck.log.strategy.LogSaveStrategy;
import org.springframework.stereotype.Service;

@Service
public class RedisLogSaveStrategy implements LogSaveStrategy {
    @Override
    public void saveLog(LogEntity log) {
        System.out.println("使用Redis进行了存储，值为：" + log.toString());
    }

    @Override
    public String getStrategyName() {
        return "redis";
    }
}
