package src.main;

import src.util.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Nonogram {
    public static void main(String[] args) {
        begin("resources/middle/hit1_temporary.png");
    }

    public static void begin(String name) {
        Hint hint = null;
        try {
            hint = calculateNonogramHints(name);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        hint.display();
        hint = hint.obscure(new SimpleColor("#FFFFFF"));
        hint.display();
    }

    public static Hint calculateNonogramHints(String name) throws IOException {
        File file = new File(name);
        BufferedImage image = ImageIO.read(file);
        int w = image.getWidth();
        int h = image.getHeight();
        SimpleColor current;
        ColorAmount previous;
        Hint hint = new Hint();
        List<List<ColorAmount>> rows = new ArrayList<List<ColorAmount>>();
        for(int i=0;i<h;i++) {
            ArrayList<ColorAmount> row = new ArrayList<ColorAmount>();
            for(int j=0;j<w;j++) {
                current = new SimpleColor(image.getRGB(j, i));
                if(row.size()==0) {
                    row.add(0, new ColorAmount(current, 1));
                    continue;
                }
                previous = row.get(0);
                if(!previous.color.equals(current)) {
                    row.add(0, new ColorAmount(current, 1));
                } else {
                   previous.incr();
                }
            }
            Collections.reverse(row);
            rows.add(row);
        }
        List<List<ColorAmount>> cols = new ArrayList<List<ColorAmount>>();
        for(int j=0;j<w;j++) {
            ArrayList<ColorAmount> col = new ArrayList<ColorAmount>();
            for(int i=0;i<h;i++) {
                current = new SimpleColor(image.getRGB(j, i));
                if(col.size()==0) {
                    col.add(0, new ColorAmount(current, 1));
                    continue;
                }
                previous = col.get(0);
                if(!previous.color.equals(current)) {
                    col.add(0, new ColorAmount(current, 1));
                } else {
                   previous.incr();
                }
            }
            Collections.reverse(col);
            cols.add(col);
        }
        hint.rows = rows;
        hint.cols = cols;
        return hint;
    }

    static class Hint {
        List<List<ColorAmount>> rows;
        List<List<ColorAmount>> cols;

        Hint obscure(SimpleColor ignore) {
            Hint obscured = new Hint();
            List<List<ColorAmount>> newrows = new ArrayList<List<ColorAmount>>();
            List<List<ColorAmount>> newcols = new ArrayList<List<ColorAmount>>();
            for(int i=0;i<rows.size();i++) {
                ArrayList<ColorAmount> newrow = new ArrayList<ColorAmount>();
                for(ColorAmount ca : rows.get(i)) {
                    if(!ca.color.equals(ignore)) newrow.add(ca);
                }
                newrows.add(newrow);
            }
            for(int i=0;i<cols.size();i++) {
                ArrayList<ColorAmount> newcol = new ArrayList<ColorAmount>();
                for(ColorAmount ca : cols.get(i)) {
                    if(!ca.color.equals(ignore)) newcol.add(ca);
                }
                newcols.add(newcol);
            }
            obscured.rows = newrows;
            obscured.cols = newcols;
            return obscured;
        }

        void display() {
            System.out.println("Rows");
            for(int i=0;i<rows.size();i++) {
                System.out.print("["+i+"]<");
                for(ColorAmount ca : rows.get(i)) {
                    System.out.print("-(" + ca.color + "=" + ca.amount + ")");
                }
                System.out.print("\n");
            }
            System.out.println("Columns");
            for(int i=0;i<cols.size();i++) {
                System.out.print("["+i+"]<");
                for(ColorAmount ca : cols.get(i)) {
                    System.out.print("-(" + ca.color + "=" + ca.amount + ")");
                }
                System.out.print("\n");
            }
        }
    }

    static class ColorAmount {
        ColorAmount(SimpleColor sc, int amt) {
            color = sc;
            amount = amt;
        }
        void incr() {
            amount++;
        }
        SimpleColor color;
        int amount;
    }
}
