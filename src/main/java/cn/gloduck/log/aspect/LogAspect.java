package cn.gloduck.log.aspect;


import cn.gloduck.log.annotation.LogRecord;
import cn.gloduck.log.el.LogRecordExpressionEvaluator;
import cn.gloduck.log.entity.LogEntity;
import cn.gloduck.log.strategy.LogSaveStrategy;
import cn.gloduck.log.strategy.LogStrategyContext;
import lombok.Getter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 日志切面
 *
 * @author Gloduck
 * @date 2022/04/25
 */
@Aspect
@Component
public class LogAspect {
    private final Logger logger = LoggerFactory.getLogger(LogAspect.class);


    /**
     * Log SpEl表达式
     */
    private final LogRecordExpressionEvaluator evaluator = new LogRecordExpressionEvaluator();


    private final static String TRUE = "true";

    private final static String FALSE = "false";

    public static final Object NO_RESULT = new Object();


    /**
     * 切入点
     */
    @Pointcut("@annotation(logRecord)")
    public void pointcut(LogRecord logRecord) {
    }


    @Around(value = "pointcut(logRecord)", argNames = "joinPoint,logRecord")
    public Object process(ProceedingJoinPoint joinPoint, LogRecord logRecord) throws Throwable {
        Object result = null;
        Throwable exception = null;

        // 初始化JoinPoint的数据
        Object target = joinPoint.getThis();
        if (target == null) {
            target = NO_RESULT;
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        EvaluationContext context = new MethodBasedEvaluationContext(target, method, args, new DefaultParameterNameDiscoverer());
        AnnotatedElementKey elementKey = new AnnotatedElementKey(method, target.getClass());
        LogRecordHolder.getVariableMap(true);

        // 执行具体逻辑代码
        long startTime = System.currentTimeMillis();
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            exception = e;
        }
        long endTime = System.currentTimeMillis();
        // 记录日志
        boolean saveLog = condition(logRecord.condition(), elementKey, context);
        if (saveLog) {
            context.setVariable("_res", result);
            context.setVariable("_ex", exception);
            Map<String, Object> variableMap = LogRecordHolder.getVariableMap(false);
            variableMap.forEach(context::setVariable);
            String expressionContent = logRecord.content();
            String content = null;
            if(expressionContent != null){
                List<PlaceHolder> placeHolders = parsePlaceHolder(expressionContent);
                Map<String, String> parseValMap = placeHolders.stream().map(PlaceHolder::getValue).distinct().collect(Collectors.toMap(s -> s, o -> evaluator.parseValue(o, elementKey, context)));
                content = replacePlaceHolder(expressionContent, parseValMap, placeHolders);
            }
            LogEntity entity = new LogEntity(logRecord.method(), content, logRecord.group(), args, endTime - startTime, exception);
            doRecord(logRecord.saveStrategy(), entity);
        }
        LogRecordHolder.clear();
        logger.debug("LogAspect execute Log and parse spel cost {} millisecond.", System.currentTimeMillis() - endTime);


        // 抛出执行业务时的异常
        if (exception != null) {
            throw exception;
        }
        return result;
    }

    /**
     * 替换占位符中的值
     *
     * @param expression 目标表达式
     * @param varMap     变量map映射
     * @return {@link String}
     */
    public static String replacePlaceHolder(String expression, Map<String, String> varMap, List<PlaceHolder> placeHolders){
        if(placeHolders.size() == 0){
            return expression;
        }
        StringBuilder builder = new StringBuilder();
        char[] chars = expression.toCharArray();
        Iterator<PlaceHolder> iterator = placeHolders.iterator();
        PlaceHolder placeHolder = iterator.next();
        for (int i = 0; i < chars.length; i++) {
            if(i < placeHolder.startIndex){
                builder.append(chars[i]);
            }else if(i == placeHolder.endIndex){
                builder.append(varMap.get(placeHolder.value));
                if(iterator.hasNext()){
                    placeHolder = iterator.next();
                } else if(i + 1 < chars.length){
                    builder.append(expression.substring(i + 1));
                }
            }

        }
        return builder.toString();
    }
    /**
     * 解析出表达式中占位符的内容
     *
     * @param expression 表达式
     * @return {@link List}<{@link String}>
     */
    public static List<PlaceHolder> parsePlaceHolder(String expression){
        char[] chars = expression.toCharArray();
        List<PlaceHolder> resList = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if(chars[i] == '$'){
                continue;
            }
            if(chars[i] == '{' && i - 1 >= 0 && chars[i - 1] == '$'){
                // 找到占位符开始符
                int count = 1;
                int j = i + 1;
                for (; j < chars.length; j++) {
                    // 括号匹配结束符
                    if(chars[j] == '{'){
                        count++;
                    } else if(chars[j] == '}'){
                        count--;
                    }
                    if(count == 0){
                        PlaceHolder holder = new PlaceHolder(i - 1, j, builder.toString());
                        resList.add(holder);
                        builder = new StringBuilder();
                        break;
                    }
                    builder.append(chars[j]);
                }
                i = j + 1;
            }
        }
        return resList;
    }



    /**
     * 记录日志
     *
     * @param strategy 策略
     * @param entity   日志实体
     */
    private void doRecord(String strategy, LogEntity entity) {
        LogSaveStrategy logSaveStrategy = LogStrategyContext.getStrategy(strategy);
        if (logSaveStrategy == null) {
            /// 获取不到策略，则不记录
            logger.warn("The strategy ({}) annotated in LogRecord can't be found, check out if the strategy has been registered", strategy);
            return;
        }
        logSaveStrategy.saveLog(entity);
    }


    /**
     * 根据条件判断是否需要记录日志，同时如果字符串为true或false直接进行判断，而不需要走el表达式。
     *
     * @param expression 表达式
     * @param elementKey 关键元素
     * @param context    上下文
     * @return boolean
     */
    private boolean condition(String expression, AnnotatedElementKey elementKey, EvaluationContext context) {
        if (TRUE.equalsIgnoreCase(expression)) {
            return true;
        }
        if (FALSE.equalsIgnoreCase(expression)) {
            return false;
        }
        String parseValue = evaluator.parseValue(expression, elementKey, context);
        if(!(TRUE.equals(parseValue) || FALSE.equals(parseValue))){
            logger.warn("Condition return false for expression [{}], because the expression is invalid or return invalid boolean value, check out you spel expression",
                    expression);
            return false;
        }
        return TRUE.equals(parseValue);
    }


    @Getter
    private static class PlaceHolder{
        private final int startIndex;
        private final int endIndex;
        private final String value;

        public PlaceHolder(int startIndex, int endIndex, String value) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.value = value;
        }
    }

}