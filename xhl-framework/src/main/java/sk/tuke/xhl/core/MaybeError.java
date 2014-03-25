/*
 * XHL - Extensible Host Language
 * Copyright 2012 Sergej Chodarev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sk.tuke.xhl.core;

import java.util.Collections;
import java.util.List;

/**
 * An object of type T or errors.
 * This object wraps a value to allow indicating errors that may be returned
 * instead of a result. Result can be returned even in a case where errors was
 * found, although it may be incomplete.
 */
public class MaybeError<T> {
    private final List<Error> errors;
    private final T value;

    private MaybeError(T value, List<Error> errors) {
        this.value = value;
        this.errors = errors;
    }

    /**
     * Wrap the result of successful operation.
     *
     * @param val Value of the result
     * @param <T> Type of the result
     * @return Wrapped result
     */
    public static <T> MaybeError<T> succeed(T val) {
        return new MaybeError<>(val, Collections.<Error>emptyList());
    }

    /**
     * Wrap the errors of failed operation.
     *
     * @param errors Error messages
     * @param <T>    Type of the result
     * @return Wrapped errors
     */
    public static <T> MaybeError<T> fail(List<Error> errors) {
        return new MaybeError<>(null, errors);
    }

    /**
     * Wrap the errors of failed operation with incomplete result.
     *
     * @param errors Error messages
     *               @param val P
     * @param <T>    Type of the result
     * @return Wrapped errors
     */
    public static <T> MaybeError<T> fail(List<Error> errors, T val) {
        return new MaybeError<>(val, errors);
    }

    /**
     * Extract the result of successful operation.
     */
    public T get() {
        return value;
    }

    /**
     * Extract error messages of failed operation.
     */
    public List<Error> getErrors() {
        return errors;
    }

    /**
     * Did the operation succeed?
     *
     * @return <code>true</code> if the operation was successful.
     */
    public boolean succeed() {
        return errors.isEmpty();
    }

    /**
     * Does this object contains the result of operation?
     */
    public boolean hasResult() {
        return value != null;
    }
}
