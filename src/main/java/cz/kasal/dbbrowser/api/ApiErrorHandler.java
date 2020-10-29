package cz.kasal.dbbrowser.api;

import cz.kasal.dbbrowser.model.ApiExceptionDTO;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiErrorHandler {


    /**
     * Handles {@link EntityNotFoundException} in rest controllers
     *
     * @param e exception to be handled
     * @return custom response with ApiErrorDTO representing error as body
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody
    ApiExceptionDTO handleEntityNotFoundException(EntityNotFoundException e) {
        return constructException(e.getLocalizedMessage(), e);
    }

    /**
     * Handles {@link DataIntegrityViolationException} in rest controllers
     *
     * @param e exception to be handled
     * @return custom response with ApiErrorDTO representing error as body
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody
    ApiExceptionDTO handleConstraintViolation(DataIntegrityViolationException e) {
        Throwable t = ExceptionUtils.getRootCause(e);
        return constructException(t.getMessage(), e);
    }


    /**
     * Helper method used to construct API ERROR DTO consisting of main cause and errors
     *
     * @param message    custom message describing what happened
     * @param exceptions relatecd exceptions
     * @return
     */
    private ApiExceptionDTO constructException(String message, Exception... exceptions) {
        ApiExceptionDTO apiExceptionDTO = new ApiExceptionDTO();
        apiExceptionDTO.setMessage(message);
        apiExceptionDTO.setErrors(Arrays.stream(exceptions).map(Throwable::getMessage).collect(Collectors.toList()));
        return apiExceptionDTO;
    }

}
