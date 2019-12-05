package top.sl.tmpp.common.exception;

import org.springframework.http.HttpStatus;

/**
 * @author ShuLu
 * @date 2019/12/4 18:09
 */
public class NoBuyBookException extends BaseException {
    public NoBuyBookException(String msg) {
        super( msg, HttpStatus.BAD_REQUEST);
    }
}
