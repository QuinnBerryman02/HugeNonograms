package src.main;

import src.util.ImageColorReducer;

public class Main {
    public static void main(String[] args) {
        begin("resources/middle/hit1.png");
    }

    public static void begin(String name) {
        Nonogram n = new Nonogram(ImageColorReducer.openStringAsImage(name));
        Window w = new Window(n);
    }
}
