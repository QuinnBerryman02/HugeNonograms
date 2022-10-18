package src.main;

import src.util.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Nonogram {
    Hints hints = null;
    Hints obscuredHints = null;
    int hintW, hintH;
    Palette palette = null;
    SimpleColor background = null;
    int W = 0,H = 0;

    public Nonogram(BufferedImage image) {
        W = image.getWidth();
        H = image.getHeight();
        palette = new Palette(image, true);
        background = palette.findLargestAmount();
        hints = calculateNonogramHints(image);
        System.out.println("New Nonogram created");
        System.out.println("Size: " + W + "x" + H);
        System.out.println("Hint Sizes: " + hintW + "x" + hintH);
        palette.displayShort();
        System.out.println("Background color is: " + background);
        obscuredHints = hints.obscure(background);
    }

    public Hints calculateNonogramHints(BufferedImage image) {
        SimpleColor current;
        ColorAmount previous;
        Hints hints = new Hints();
        List<List<ColorAmount>> rows = new ArrayList<List<ColorAmount>>();
        hintW = Integer.MIN_VALUE;
        hintH = Integer.MIN_VALUE;
        for(int i=0;i<H;i++) {
            ArrayList<ColorAmount> row = new ArrayList<ColorAmount>();
            int effectiveSize=0;
            for(int j=0;j<W;j++) {
                current = new SimpleColor(image.getRGB(j, i));
                boolean isBackground = current.exact(background);
                if(row.size()==0) {
                    row.add(0, new ColorAmount(current, 1));
                    effectiveSize += isBackground ? 0 : 1;
                    continue;
                }
                previous = row.get(0);
                if(!previous.color.exact(current)) {
                    row.add(0, new ColorAmount(current, 1));
                    effectiveSize += isBackground ? 0 : 1;
                } else {
                   previous.incr();
                }
            }
            if(effectiveSize>hintW) hintW=effectiveSize;
            Collections.reverse(row);
            rows.add(row);
        }
        List<List<ColorAmount>> cols = new ArrayList<List<ColorAmount>>();
        for(int j=0;j<W;j++) {
            ArrayList<ColorAmount> col = new ArrayList<ColorAmount>();
            int effectiveSize=0;
            for(int i=0;i<H;i++) {
                current = new SimpleColor(image.getRGB(j, i));
                boolean isBackground = current.exact(background);
                if(col.size()==0) {
                    col.add(0, new ColorAmount(current, 1));
                    effectiveSize += isBackground ? 0 : 1;
                    continue;
                }
                previous = col.get(0);
                if(!previous.color.exact(current)) {
                    col.add(0, new ColorAmount(current, 1));
                    effectiveSize += isBackground ? 0 : 1;
                } else {
                   previous.incr();
                }
            }
            if(effectiveSize>hintH) hintH=effectiveSize;
            Collections.reverse(col);
            cols.add(col);
        }
        hints.rows = rows;
        hints.cols = cols;
        return hints;
    }

    static class Hints {
        List<List<ColorAmount>> rows;
        List<List<ColorAmount>> cols;

        Hints obscure(SimpleColor ignore) {
            Hints obscured = new Hints();
            List<List<ColorAmount>> newrows = new ArrayList<List<ColorAmount>>();
            List<List<ColorAmount>> newcols = new ArrayList<List<ColorAmount>>();
            for(int i=0;i<rows.size();i++) {
                ArrayList<ColorAmount> newrow = new ArrayList<ColorAmount>();
                for(ColorAmount ca : rows.get(i)) {
                    if(!ca.color.exact(ignore)) newrow.add(ca);
                }
                newrows.add(newrow);
            }
            for(int i=0;i<cols.size();i++) {
                ArrayList<ColorAmount> newcol = new ArrayList<ColorAmount>();
                for(ColorAmount ca : cols.get(i)) {
                    if(!ca.color.exact(ignore)) newcol.add(ca);
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
