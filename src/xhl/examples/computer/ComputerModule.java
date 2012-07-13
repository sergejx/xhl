package xhl.examples.computer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xhl.core.GenericModule;
import xhl.core.elements.Block;
import xhl.core.elements.Expression;
import xhl.core.elements.Symbol;

/**
 * Simple computer configuration language.
 *
 * Inspired by examples from http://martinfowler.com/dslwip/
 *
 * @author Sergej Chodarev
 */
public class ComputerModule extends GenericModule {

    private Computer computer = null;

    public ComputerModule() {
        addKeyword(new Symbol("type"));
        addKeyword(new Symbol("cores"));
        addKeyword(new Symbol("interface"));
        addKeyword(new Symbol("speed"));
        addKeyword(new Symbol("size"));

        for (Processor.Type type : Processor.Type.values()) {
            addSymbol(new Symbol(type.name()), type);
        }
        for (Disk.Interface iface : Disk.Interface.values()) {
            addSymbol(new Symbol(iface.name()), iface);
        }
    }

    public Computer getComputer() {
        return computer;
    }

    @Override
    public boolean isLanguage() {
        return true;
    }

    @Element(evaluateArgs = false)
    public void computer(Block components) {
        Processor processor = null;
        List<Disk> disks = new ArrayList<>();
        for (Expression expr : components) {
            Object comp = evaluator.eval(expr);
            if (comp instanceof Processor) {
                processor = (Processor) comp;
            } else if (comp instanceof Disk) {
                disks.add((Disk) comp);
            }
        }
        Processor.Type.values();
        Disk[] diskaArray = disks.toArray(new Disk[disks.size()]);
        computer = new Computer(processor, diskaArray);
    }

    @Element
    public Processor processor(Map<Symbol, Object> properties) {
        Processor.Type type = null;
        int speed = Processor.UNKNOWN_SPEED;
        int cores = 1;
        for (Symbol key : properties.keySet()) {
            if (key.isNamed("type")) {
                type = (Processor.Type) properties.get(key);
            } else if  (key.isNamed("speed")) {
                speed = ((Double) properties.get(key)).intValue();
            } else if (key.isNamed("cores")) {
                cores = ((Double) properties.get(key)).intValue();
            }
        }
        return new Processor(type, speed, cores);
    }

    @Element
    public Disk disk(Map<Symbol, Object> properties) {
        int size = Disk.UNKNOWN_SIZE;
        int speed = Disk.UNKNOWN_SPEED;
        Disk.Interface iface = null;
        for (Symbol key : properties.keySet()) {
            if (key.isNamed("size")) {
                size = ((Double) properties.get(key)).intValue();
            } else if  (key.isNamed("speed")) {
                speed = ((Double) properties.get(key)).intValue();
            } else if  (key.isNamed("interface")) {
                iface = (Disk.Interface) properties.get(key);
            }
        }
        return new Disk(size, speed, iface);
    }
}
