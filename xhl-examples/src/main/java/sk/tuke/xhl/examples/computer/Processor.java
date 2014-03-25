package sk.tuke.xhl.examples.computer;

public class Processor {
    public enum Type {
        i386, x86_64, arm, alpha, powerpc
    }

    public static final int UNKNOWN_SPEED = -1;

    private final Type type;
    private final int speed;
    private final int cores;

    public Processor(Type type, int speed, int cores) {
        this.type = type;
        this.speed = speed;
        this.cores = cores;
    }

    public Type getType() {
        return type;
    }

    public int getSpeed() {
        return speed;
    }

    public int getCores() {
        return cores;
    }

    @Override
    public String toString() {
        return String.format(
                "Processor:\n\ttype: %s\n\tspeed: %s\n\tcores: %s", type,
                speed, cores);
    }
}
