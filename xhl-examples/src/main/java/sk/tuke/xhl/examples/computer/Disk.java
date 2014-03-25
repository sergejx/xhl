package sk.tuke.xhl.examples.computer;

public class Disk {
    public enum Interface {
        ATA133, SATA, SATA2
    }

    public static final int UNKNOWN_SIZE = -1;

    public static final int UNKNOWN_SPEED = -1;

    private final int size;
    private final int speed;
    private final Interface iface;

    public Disk(int size, int speed, Interface iface) {
        this.size = size;
        this.speed = speed;
        this.iface = iface;
    }

    public int getSize() {
        return size;
    }

    public int getSpeed() {
        return speed;
    }

    public Interface getInterface() {
        return iface;
    }

    @Override
    public String toString() {
        return String.format("Disk (size: %s, speed: %s, interface: %s)", size,
                speed, iface);
    }
}
