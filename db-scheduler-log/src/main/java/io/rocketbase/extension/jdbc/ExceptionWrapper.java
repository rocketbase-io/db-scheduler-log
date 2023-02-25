package io.rocketbase.extension.jdbc;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExceptionWrapper implements Serializable {
    public String classname;
    public String message;
    public List<String> stackTraces;

    public ExceptionWrapper(Throwable cause) {
        classname = cause.getClass().getName();
        message = cause.getMessage();
        stackTraces = Arrays.stream(cause.getStackTrace())
            .map(stackTraceElement -> stackTraceElement.toString())
            .collect(Collectors.toList());
    }
}
