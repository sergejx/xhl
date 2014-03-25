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

import com.google.common.collect.ImmutableList;
import sk.tuke.xhl.core.elements.*;

import java.util.HashSet;
import java.util.Set;

public class Type {
    public static final Type AnyType = new Type("AnyType");
    public static final Type Null = new Type("Null");
    public static final Type Element = new Type("Element");
    public static final Type Boolean = new Type("Boolean");
    public static final Type Number = new Type("Number");
    public static final Type String = new Type("String");
    public static final Type Symbol = new Type("Symbol");
    public static final Type List = new Type("List");
    public static final Type Map = new Type("Map");
    public static final Type Block = new Type("Block");
    public static final Type Combination = new Type("Combination");

    public static final ImmutableList<Type> defaultTypes = ImmutableList.of
            (AnyType, Null, Element, Boolean, Number, String, Symbol, List,
                    Map, Block, Combination);

    private final Symbol name;
    private final Set<Type> supertypes = new HashSet<>();

    public Type(Symbol name) {
        this.name = name;
    }

    private Type(String name) {
        this(new Symbol(name));
    }

    public Symbol getName() {
        return name;
    }

    public boolean addSupertype(Type type) {
        return supertypes.add(type);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Type && name.equals(((Type) obj).name);
    }

    @Override
    public java.lang.String toString() {
        return name.toString();
    }

    /**
     * Check is a type is compatible with <code>that</code> type.
     */
    public boolean is(Type that) {
        return this.equals(AnyType) || that.equals(AnyType) ||
                this.equals(that) || supertypes.contains(that);
    }

    public boolean isNamed(String n) {
        return name.isNamed(n);
    }

    public static Type typeOfElement(Expression ex) {
        if (ex instanceof Symbol)
            return Symbol;
        if (ex instanceof SString)
            return String;
        if (ex instanceof SNumber)
            return Number;
        if (ex instanceof SBoolean)
            return Boolean;
        if (ex instanceof SList)
            return List;
        if (ex instanceof SMap)
            return Map;
        if (ex instanceof Combination)
            return Combination;
        if (ex instanceof Block)
            return Block;
        return AnyType;
    }
}
