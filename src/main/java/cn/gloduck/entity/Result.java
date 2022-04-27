package cn.gloduck.entity;

public class Result<T> {
    private  Result(Integer code, String msg, T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    private Integer code;
    private String msg;
    private T data;

    /**
     * 结果
     *
     * @param code 状态码
     * @param msg  消息
     * @param data 数据
     * @return {@link Result}<{@link T}>
     */
    public static <T> Result<T> result(StatusCode code, String msg, T data){
        return new Result<>(code.value, msg, data);
    }

    /**
     * 失败
     *
     * @return {@link Result}<{@link T}>
     */
    public static <T> Result<T> failed(){
        return failed("失败");
    }

    /**
     * 失败
     *
     * @param msg 消息
     * @return {@link Result}<{@link T}>
     */
    public static <T> Result<T> failed(String msg){
        return new Result<>(StatusCode.FAILED.value, msg, null);
    }

    /**
     * 错误
     *
     * @return {@link Result}<{@link T}>
     */
    public static <T> Result<T> error(){
        return error("错误");
    }

    /**
     * 错误
     *
     * @param msg 消息
     * @return {@link Result}<{@link T}>
     */
    public static <T> Result<T> error(String msg){
        return new Result<>(StatusCode.ERROR.value, msg, null);
    }

    /**
     * 成功
     *
     * @return {@link Result}<{@link T}>
     */
    public static <T> Result<T> success(){
        return success("成功", null);
    }

    /**
     * 成功
     *
     * @param data 数据
     * @return {@link Result}<{@link T}>
     */
    public static <T> Result<T> success(T data){
        return success("成功", data);
    }

    /**
     * 成功
     *
     * @param msg  提示
     * @param data 数据
     * @return {@link Result}<{@link T}>
     */
    public static <T> Result<T> success(String msg, T data){
        return new Result<>(StatusCode.SUCCESS.value, msg, data);
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public  enum StatusCode{
        /**
         * 成功
         */
        SUCCESS(200),
        /**
         * 失败
         */
        FAILED(417),

        /**
         * 错误
         */
        ERROR(500);

        private final int value;

        StatusCode(int value) {
            this.value = value;
        }
    }
}
