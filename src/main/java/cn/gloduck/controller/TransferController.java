package cn.gloduck.controller;

import cn.gloduck.service.TransferService;
import cn.gloduck.entity.Result;
import cn.gloduck.log.annotation.LogRecord;
import cn.gloduck.log.aspect.LogRecordHolder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfer")
public class TransferController {
    @Autowired
    private TransferService transferService;

    @LogRecord(method = "Controller层转账方法", content = "用户：${{#param.fromUserId}} 于时间点 ${{#date}} 向用户：${{#param.toUserId}} 转账：${{#param.money}} 元", saveStrategy = "mysql")
    @PostMapping("")
    public Result<Object> transferMoney(@RequestBody TransferParam param){
        LogRecordHolder.putVariable("date", System.currentTimeMillis());
        return transferService.doTransferMoney(param.fromUserId, param.toUserId, param.money) ? Result.success() : Result.failed();
    }

    @Data
    public static class TransferParam {
        private Integer fromUserId;

        private Integer toUserId;

        private Double money;
    }
}
