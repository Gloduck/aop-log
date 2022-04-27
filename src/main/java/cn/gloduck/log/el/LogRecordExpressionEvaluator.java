package cn.gloduck.log.el;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志记录表达式求值程序
 *
 * @author Gloduck
 * @date 2022/04/25
 */
public class LogRecordExpressionEvaluator extends CachedExpressionEvaluator {
    private final Logger logger = LoggerFactory.getLogger(LogRecordExpressionEvaluator.class);

    private final Map<ExpressionKey, Expression> expressionCache = new ConcurrentHashMap<>(64);

    /**
     * 解析值
     *
     * @param keyExpression spel表达式
     * @param methodKey     方法描述
     * @param evalContext   eval上下文
     * @return {@link String}
     */
    public String parseValue(String keyExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        String parseExpression;
        try {
            parseExpression = getExpression(expressionCache, methodKey, keyExpression).getValue(evalContext, String.class);
        } catch (Exception e) {
            parseExpression = keyExpression;
            logger.error("Parse expression [" + keyExpression + "] error, check out if the expression is correct, we will return expression itself.", e);
        }
        return parseExpression;
    }
}
