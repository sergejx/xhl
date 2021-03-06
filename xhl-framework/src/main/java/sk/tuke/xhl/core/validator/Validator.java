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

import sk.tuke.xhl.core.Environment;
import sk.tuke.xhl.core.Error;
import sk.tuke.xhl.core.elements.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static sk.tuke.xhl.core.ModulesProvider.ModulesLoader;

public class Validator implements ElementVisitor<Type> {
    private final Environment<Type> table = new Environment<>();
    private final Environment<ElementValidator> elements = new Environment<>();
    private final List<Error> errors = new ArrayList<>();

    public Validator(Schema mainSchema) {
        addElements(mainSchema);

        ModulesLoader loader = new ModulesLoader();
        for (Schema.Import imp : mainSchema.getImports()) {
            Schema impSchema = loader.loadModule(imp.getModule()).getSchema();
            if (imp.allElements())
                addElements(impSchema);
            else {
                for (Symbol element : imp)
                    addElement(impSchema.get(element));
            }
        }
    }

    private void addElements(Iterable<ElementSchema> elements) {
        for (ElementSchema element : elements) {
            addElement(element);
        }
    }

    private void addElement(ElementSchema element) {
        this.elements.put(element.getSymbol(), element.getValidator());
        if (element.getParams().size() == 0)
            table.put(element.getSymbol(), element.getType());
        else
            table.put(element.getSymbol(), Type.Element);
    }

    public List<Error> getErrors() {
        return errors;
    }

    public Type check(Expression expression) {
        return expression.accept(this);
    }

    /**
     * Check expression inside a local scope.
     *
     * @param expression    Expression to check
     * @param localElements A collection of local elements available in the
     *                      scope
     * @return Type of the expression
     */
    public Type checkWithLocalScope(Expression expression,
                                    Iterable<ElementSchema> localElements) {
        table.push();
        elements.push();
        addElements(localElements);
        Type type = expression.accept(this);
        elements.pop();
        table.pop();
        return type;
    }

    @Override
    public Type visit(SNumber num) {
        return Type.Number;
    }

    @Override
    public Type visit(SBoolean bool) {
        return Type.Boolean;
    }

    @Override
    public Type visit(SString str) {
        return Type.String;
    }

    @Override
    public Type visit(SList lst) {
        for (Expression exp : lst) {
            check(exp);
        }
        return Type.List;
    }

    @Override
    public Type visit(SMap map) {
        for (Expression key : map.keySet()) {
            check(map.get(key));
        }
        return Type.Map;
    }

    @Override
    public Type visit(Symbol sym) {
        if (table.containsKey(sym)) {
            if (table.get(sym).equals(Type.Element)) {
                ElementValidator elValidator = elements.get(sym);
                ValidationResult result =
                        elValidator.check(this,
                                new SList(sym.getPosition()));
                errors.addAll(result.getErrors());
                return result.getType();
            } else
                return table.get(sym);
        } else {
            errors.add(new Error(sym.getPosition(), String.format(
                    "Symbol '%s' is not defined", sym)));
            return Type.AnyType;
        }
    }

    @Override
    public Type visit(Combination cmb) {
        if (!(cmb.get(0) instanceof Symbol)) {
            errors.add(new Error(cmb.getPosition(),
                    "Combination head is not a symbol"));
            return Type.AnyType;
        }
        Symbol head = (Symbol) cmb.get(0);
        if (!elements.containsKey(head)) {
            errors.add(new Error(head.getPosition(), String.format(
                    "Symbol '%s' is not defined", head)));
            return Type.AnyType;
        }
        ElementValidator elValidator = elements.get(head);
        ValidationResult result = elValidator.check(this, cmb.tail());
        table.putAll(result.getDefined());
        table.putAllGlobal(result.getDefinedGlobal());
        errors.addAll(result.getErrors());
        return result.getType();
    }

    @Override
    public Type visit(Block blk) {
        collectBackwardDefinitions(blk);
        for (Expression exp : blk)
            check(exp);
        return Type.Block;
    }

    public static Map<Symbol, Type> backwardDefinitions(Block blk,
                                                        Schema schema) {
        // FIXME: Remove duplication!
        Map<Symbol, Type> table = new HashMap<>();
        for (Expression exp : blk) {
            try {
                Combination cmb = (Combination) exp;
                Symbol head = (Symbol) cmb.head();
                SList tail = cmb.tail();
                ElementSchema elemSchema = schema.get(head);
                if (schema != null)
                    table.putAll(elemSchema.getValidator().forwardDefinitions(
                            tail));
            } catch (ClassCastException e) {
                // Ignore cases, where types did not match expectations
            }
        }
        return table;
    }

    private void collectBackwardDefinitions(Block blk) {
        for (Expression exp : blk) {
            try {
                Combination cmb = (Combination) exp;
                Symbol head = (Symbol) cmb.head();
                SList tail = cmb.tail();
                ElementValidator elValidator = elements.get(head);
                if (elValidator == null) {
                    errors.add(new Error(head.getPosition(), "Element '" + head
                            + "' not defined."));
                } else
                    table.putAll(elValidator.forwardDefinitions(tail));
            } catch (ClassCastException e) {
                // Ignore cases, where types did not match expectations
            }
        }
    }
}
