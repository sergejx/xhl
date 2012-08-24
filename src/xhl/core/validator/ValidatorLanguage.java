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
package xhl.core.validator;

import xhl.core.EvaluationException;
import xhl.core.GenericModule;
import xhl.core.elements.Block;
import xhl.core.elements.Expression;
import xhl.core.elements.SList;
import xhl.core.elements.Symbol;
import xhl.core.validator.ElementSchema.DefSpec;
import xhl.core.validator.ElementSchema.ParamSpec;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static xhl.core.validator.ElementSchema.DefSpec.global;

public class ValidatorLanguage extends GenericModule {
    /**
     * A stack representing currently processed elements.
     */
    private final Deque<ElementSchema> currentElement = new LinkedList<>();
    private final Schema schema = new Schema();

    public ValidatorLanguage() {
        for (Type type : Type.defaultTypes) {
            addSymbol(type.getName(), type);
        }
    }

    @Override
    public boolean isLanguage() {
        return true;
    }

    @Override
    protected boolean canHaveModules() {
        return false;
    }

    @Element(name = "import")
    public void importElement(@Symbolic Symbol name,
                              @Symbolic Expression names) {
        if (names instanceof Symbol && ((Symbol) names).isNamed("all")) {
            schema.addImport(new Schema.Import(name.getName()));
        } else if (names instanceof SList) {
            List<Symbol> elements = new ArrayList<>();
            for (Expression el : (SList) names) {
                if (el instanceof Symbol)
                    elements.add((Symbol) el);
                else
                    throw new EvaluationException(
                            "Incompatible type of the arguments");
            }
            schema.addImport(new Schema.Import(name.getName(), elements));
        } else {
            throw new EvaluationException(
                    "Incompatible type of the arguments");
        }

    }

    @Element
    public void element(@Symbolic Symbol name, @Symbolic Block blk) {
        final ElementSchema element = new ElementSchema(name);
        if (currentElement.isEmpty())
            schema.put(element);
        else
            currentElement.peek().addLocalElement(element);
        currentElement.push(element);
        evaluator.eval(blk);
        currentElement.remove();
    }

    @Element
    public void doc(String doc) {
        currentElement.peek().setDoc(doc);
    }

    @Element
    public void params(List<ParamSpec> args) {
        currentElement.peek().setParams(args);
    }

    @Element
    public ParamSpec val(Type type) {
        return ParamSpec.val(type);
    }

    @Element
    public ParamSpec sym(Type type) {
        return ParamSpec.sym(type);
    }

    @Element
    public ParamSpec variadic(ParamSpec param) {
        return ParamSpec.variadic(param);
    }

    @Element
    public ParamSpec block(ParamSpec param) {
        return ParamSpec.block(param);
    }

    @Element
    public void type(Type type) {
        currentElement.peek().setType(type);
    }

    @Element
    public void defines(double arg, Type type) {
        checkArgument(arg % 1 == 0);
        DefSpec def = new DefSpec((int) arg, type);
        currentElement.peek().addDefine(def);
    }

    @Element
    public void defines_backward(double arg, Type type) {
        checkArgument(arg % 1 == 0);
        DefSpec def = new DefSpec((int) arg, type, true);
        currentElement.peek().addDefine(def);
    }

    @Element
    public void defines_global(double arg, Type type) {
        checkArgument(arg % 1 == 0);
        DefSpec def = new DefSpec((int) arg, type, true);
        currentElement.peek().addDefine(global(def));
    }

    @Element
    public void newtype(@Symbolic Symbol name) {
        evaluator.putSymbol(name, new Type(name));
    }

    @Element(name = "<:")
    public void subtype(Type subtype, Type supertype) {
        subtype.addSupertype(supertype);
    }

    public Schema getReadSchema() {
        return schema;
    }
}
