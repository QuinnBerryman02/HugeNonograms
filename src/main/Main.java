package src.main;

public class Main {
    public static void main(String[] args) {
        begin("resources/middle/smallerBook_temporary.png");
    }

    public static void begin(String name) {
        Nonogram n = new Nonogram(PixelColorCounter.stringToImage(name));
        Window w = new Window(n);
    }
}
