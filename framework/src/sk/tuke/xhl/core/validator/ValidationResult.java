/* XHL - Extensible Host Language
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
package sk.tuke.xhl.core.validator;

import sk.tuke.xhl.core.Error;
import sk.tuke.xhl.core.elements.Symbol;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ValidationResult {
    private final Type type;
    private final List<Error> errors;
    private final Map<Symbol, Type> defined;
    private final Map<Symbol, Type> definedGlobal;

    public ValidationResult(Type type, List<Error> errors) {
        this(type, errors, Collections.<Symbol, Type>emptyMap());
    }

    public ValidationResult(Type type, List<Error> errors,
                            Map<Symbol, Type> defined) {
        this(type, errors != null ? errors : Collections.<Error>emptyList(),
                defined, Collections.<Symbol, Type>emptyMap());
    }

    public ValidationResult(Type type, List<Error> errors,
                            Map<Symbol, Type> defined,
                            Map<Symbol, Type> definedGlobal) {
        this.type = type;
        this.errors = errors != null ? errors : Collections.<Error>emptyList();
        this.defined = defined;
        this.definedGlobal = definedGlobal;
    }

    public Type getType() {
        return type;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public Map<Symbol, Type> getDefined() {
        return defined;
    }

    public Map<Symbol, Type> getDefinedGlobal() {
        return definedGlobal;
    }
}
