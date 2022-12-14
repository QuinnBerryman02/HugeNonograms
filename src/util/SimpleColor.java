package src.util;

import java.awt.Color;

public class SimpleColor {
    private int r,g,b;
    
    
    public static int strictness = 1;
    public final static int SHADES = 17;
    public final static int RANGE = 255 / SHADES;

    public SimpleColor(String hexcode) {
        r = fromHex2(hexcode.substring(1, 3));
        g = fromHex2(hexcode.substring(3, 5));
        b = fromHex2(hexcode.substring(5, 7));
    }

    public SimpleColor(int color) {
        Color c = new Color(color);
        r = c.getRed();
        g = c.getGreen();
        b = c.getBlue();
    }

    public boolean isLight() {
        return (r+g+b > 3*256/2f);
    }

    public Color toColor() {
        return new Color(toInt());
    }

    public int toInt() {
        return (new Color(r, g, b).getRGB());
    }

    public boolean close(SimpleColor sc) {
        return Math.abs(r - sc.r) <= RANGE*strictness
        && Math.abs(g - sc.g) <= RANGE*strictness
        && Math.abs(b - sc.b) <= RANGE*strictness;
    }

    public boolean exact(SimpleColor sc) {
        return Math.abs(r - sc.r) <= 0
        && Math.abs(g - sc.g) <= 0
        && Math.abs(b - sc.b) <= 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SimpleColor) {
            SimpleColor sc = (SimpleColor)obj;
            return close(sc);
        } else return false;
    }

    @Override
    public String toString() {
        return "#" + toHex2(r) + toHex2(g) + toHex2(b);
    }

    public static String toHex2(int n) {
        return toHex1(n / 16) + toHex1(n % 16);
    }

    public static String toHex1(int n) {
        if(n>=0&&n<=9) return String.valueOf(n);
        switch(n) {
            case 10: return "A";
            case 11: return "B";
            case 12: return "C";
            case 13: return "D";
            case 14: return "E";
            case 15: return "F";
            default: return "-";
        }
    }

    public static int fromHex2(String h) {
        return fromHex1(h.charAt(0)) * 16 + fromHex1(h.charAt(1));
    }

    public static int fromHex1(char h) {
        switch(h) {
            case '0': return 0;
            case '1': return 1;
            case '2': return 2;
            case '3': return 3;
            case '4': return 4;
            case '5': return 5;
            case '6': return 6;
            case '7': return 7;
            case '8': return 8;
            case '9': return 9;
            case 'A': return 10;
            case 'B': return 11;
            case 'C': return 12;
            case 'D': return 13;
            case 'E': return 14;
            case 'F': return 15;
            case 'a': return 10;
            case 'b': return 11;
            case 'c': return 12;
            case 'd': return 13;
            case 'e': return 14;
            case 'f': return 15;
            default: return -1;
        }
    }
}