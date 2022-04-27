package cn.gloduck.service;

import cn.gloduck.log.annotation.LogRecord;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TransferService {
    @LogRecord(method = "Service层转账方法", content = "用户：${{#fromUserId}} 于时间点 ${T(System).currentTimeMillis()} 向用户：${{#toUserId}} 转账：${{#money}} 元", saveStrategy = "redis")
    public boolean doTransferMoney(Integer fromUserId, Integer toUserId, Double money){
        /* do something */

        return true;
    }
}
