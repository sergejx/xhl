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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Base class for composed elements, that are lists of expressions.
 *
 * @author Sergej Chodarev
 */
public abstract class ExpressionsList extends Expression implements
        Iterable<Expression> {

    protected final List<Expression> list = new LinkedList<>();

    protected ExpressionsList() {
    }

    public ExpressionsList(Position position) {
        super(position);
    }

    public boolean add(Expression e) {
        return list.add(e);
    }

    public int size() {
        return list.size();
    }

    public Expression get(int arg0) {
        return list.get(arg0);
    }

    public List<Expression> subList(int from, int to) {
        return list.subList(from, to);
    }

    public Iterator<Expression> iterator() {
        return list.iterator();
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
