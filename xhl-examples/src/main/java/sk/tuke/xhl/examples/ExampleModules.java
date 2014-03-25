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

package sk.tuke.xhl.examples;

import sk.tuke.xhl.core.Module;
import sk.tuke.xhl.core.ModulesProvider;
import sk.tuke.xhl.examples.computer.ComputerModule;
import sk.tuke.xhl.examples.drawing.DrawingModule;
import sk.tuke.xhl.examples.entity.EntityModule;
import sk.tuke.xhl.examples.statemachine.StateMachineModule;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class ExampleModules implements ModulesProvider {

    private final Set<String> modules = newHashSet(
            "computer", "drawing", "entity", "controller");

    @Override
    public boolean hasModule(String name) {
        return modules.contains(name);
    }

    @Override
    public Module getModule(String name) {
        switch (name) {
            case "computer":
                return new ComputerModule();
            case "drawing":
                return new DrawingModule();
            case "entity":
                return new EntityModule();
            case "controller":
                return new StateMachineModule();
            default:
                return null;
        }
    }
}
