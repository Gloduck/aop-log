# 使用

+ 在上面代码定义好后，使用起来就比较简单了。

+ 首先，我们需要实现两个日志记录的策略。**并将其定义为Bean**。（我们没有提供默认的实现）。这里为了方便，直接使用`System.out.println`来模拟。

  ```java
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
  
  @Service
  public class MySqlLogSaveStrategy implements LogSaveStrategy {
      @Override
      public void saveLog(LogEntity log) {
          System.out.println("使用MySql进行了存储，值为：" + log.toString());
      }
  
      @Override
      public String getStrategyName() {
          return "mysql";
      }
  }
  ```

+ 然后编写一个转账接口，并且使用我们的注解定义。其中content中有4个变量，date变量是业务代码里面通过`LogRecordHolder`赋值的，其余变量是参数里获取的。然后使用`mysql`指定，使用`mysql`策略来存储。

    + 返回值通过`_res`获取，异常通过`_ex`获取。但**请尽量不要使用**，因为如果是使用`ControlerAdvice` + `ExceptionHandler`来处理异常的话，在业务代码抛出异常的时候就直接`_res`是获取不到值的。因为根本没有返回值。

  ```java
      @LogRecord(method = "Controller层转账方法", content = "用户：${{#param.fromUserId}} 于时间点 ${{#date}} 向用户：${{#param.toUserId}} 转账：${{#param.money}} 元", saveStrategy = "mysql")
      @PostMapping("")
      public Result<Object> transferMoney(@RequestBody TransferParam param){
          LogRecordHolder.putVariable("date", System.currentTimeMillis());
          return transferService.doTransferMoney(param.fromUserId, param.toUserId, param.money) ? Result.success() : Result.failed();
      }
  ```

+ 然后在Controller层调用的Service层也加一个注解：

  ```java
  @Service
  public class TransferService {
      @LogRecord(method = "Service层转账方法", content = "用户：${{#fromUserId}} 于时间点 ${T(System).currentTimeMillis()} 向用户：${{#toUserId}} 转账：${{#money}} 元", saveStrategy = "redis")
      public boolean doTransferMoney(Integer fromUserId, Integer toUserId, Double money){
          /* do something */
  
          return true;
      }
  }
  ```

+ 最后，启动应用。

+ 通过http client进行请求

  ```http
  POST http://localhost:8080/transfer
  Content-Type: application/json
  
  {"fromUserId": 1000, "toUserId": 2000, "money": 32.2}
  ```

+ 打印出来的结果为：

  ```shell
  使用Redis进行了存储，值为：LogEntity(method=Service层转账方法, content=用户：1000 于时间点 1651027193983 向用户：2000 转账：32.2 元, group=, params=[1000, 2000, 32.2], costTime=3, exception=null)
  使用MySql进行了存储，值为：LogEntity(method=Controller层转账方法, content=用户：1000 于时间点 1651027193963 向用户：2000 转账：32.2 元, group=, params=[TransferController.TransferParam(fromUserId=1000, toUserId=2000, money=32.2)], costTime=42, exception=null)
  ```
+ 实现参考[博客文章](https://mxecy.cn/post/java-aop-log-util/)

