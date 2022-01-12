package us.supercheng.api.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import us.supercheng.utils.APIResponse;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public APIResponse handlerMaxUploadFile() {
        return APIResponse.errorMsg("File too big... max size 500k");
    }
}
