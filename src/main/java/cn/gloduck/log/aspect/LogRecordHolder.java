package cn.gloduck.log.aspect;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Log变量保持器。
 * 如果需要在SpEl插入额外的变量，可以通过此类来设置。
 *
 * @author Gloduck
 * @date 2022/04/26
 */
public class LogRecordHolder {
    private static final InheritableThreadLocal<Stack<Map<String, Object>>> variableMapStack = new InheritableThreadLocal<>();

    /**
     * 放入方法变量
     *
     * @param key  键
     * @param value 值
     */
    public static void putVariable(String key, Object value) {
        getVariableMap(false).put(key, value);
    }


    /**
     * 获取方法变量
     *
     * @param key 键
     * @return {@link Object}
     */
    public static Object getVariable(String key) {
        return getVariableMap(false).get(key);
    }

    /**
     * 获取方法变量Map
     *
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public static Map<String, Object> getVariableMap() {
        return getVariableMap(false);
    }

    /**
     * 清理Log变量Map
     */
     static void clear() {
        Stack<Map<String, Object>> mapStack = variableMapStack.get();
        if(mapStack == null || mapStack.size() == 0){
            variableMapStack.remove();
        } else {
            mapStack.pop();
        }
    }


    /**
     * 获取变量map
     *
     * @param createNew 是否创建一个新的map
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    static Map<String, Object> getVariableMap(boolean createNew){
        Stack<Map<String, Object>> mapStack = variableMapStack.get();
        if (mapStack == null) {
            mapStack = new Stack<>();
            variableMapStack.set(mapStack);
        }
        Map<String, Object> variableMap;
        if(createNew || mapStack.size() == 0){
            variableMap = new HashMap<>(4);
            mapStack.push(variableMap);
        } else {
            variableMap = mapStack.peek();
        }
        return variableMap;
    }
}
