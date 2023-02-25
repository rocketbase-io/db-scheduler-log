/**
 * Copyright (C) Marten Prieß
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
