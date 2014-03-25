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
package sk.tuke.xhl.core.elements;

import java.util.Arrays;

import com.google.common.base.Joiner;

public class Symbol extends Expression {
    private final String name[];

    public Symbol(String name) {
        this(new String[]{name}, null);
    }

    public Symbol(String name, Position position) {
        this(new String[]{name}, position);
    }

    public Symbol(String[] name, Position position) {
        super(position);
        this.name = name;
    }

    public String getName() {
        return name[name.length-1];
    }

    /** Check if symbol has specified name. */
    public boolean isNamed(String n) {
        return getName().equals(n);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Symbol) {
            Symbol sym = (Symbol) obj;
            return Arrays.equals(sym.name, this.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(name);
    }

    @Override
    public String toString() {
        return Joiner.on('.').join(name);
    }

    @Override
    public <R> R accept(ElementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
