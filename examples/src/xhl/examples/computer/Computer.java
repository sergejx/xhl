package xhl.examples.computer;

public class Computer {
    private final Processor processor;
    private final Disk[] disks;

    public Computer(Processor processor, Disk[] disks) {
        super();
        this.processor = processor;
        this.disks = disks;
    }

    public Processor getProcessor() {
        return processor;
    }

    public Disk[] getDisks() {
        return disks;
    }

    public void print() {
        System.out.println("Computer");
        System.out.println(processor);
        for (Disk disk : disks) {
            System.out.println(disk);
        }
    }
}
