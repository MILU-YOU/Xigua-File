package team.zlg.file.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import team.zlg.file.common.RestResult;
import team.zlg.file.common.ResultCodeEnum;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandlerAdvice {
    /**-------- 通用异常处理方法 --------**/
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public RestResult error(Exception e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);

        return RestResult.fail();    // 通用异常结果
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    //空指针处理方法
    public RestResult error(NullPointerException e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);
        return RestResult.setResult(ResultCodeEnum.NULL_POINT);
    }

    //下标越界处理方法
    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseBody
    public RestResult error(IndexOutOfBoundsException e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);
        return RestResult.setResult(ResultCodeEnum.INDEX_OUT_OF_BOUNDS);
    }
    //字段重复处理方法
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseBody
    public RestResult error(DuplicateKeyException e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);
        return RestResult.setResult(ResultCodeEnum.DUPLICATEKEY);
    }
}
