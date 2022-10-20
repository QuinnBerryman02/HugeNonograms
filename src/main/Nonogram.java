package src.main;

import src.util.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

public class Nonogram {
    Hints hints = null;
    Hints obscuredHints = null;
    int hintW, hintH;
    Palette palette = null;
    SimpleColor background = null;
    int W = 0,H = 0;
    BufferedImage image = null;
    BufferedImage leftHintImage = null;
    BufferedImage topHintImage = null;

    public Nonogram(BufferedImage image) {
        this.image = image; 
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
        createHintImages();
        File newFile1 = new File("tophint.png");
        File newFile2 = new File("lefthint.png");
        try {
            ImageIO.write(topHintImage, "png", newFile1);
            ImageIO.write(leftHintImage, "png", newFile2);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    public int calculateRealMax(List<ColorAmount> line) {
        int total = 0;
        for (ColorAmount ca : line) {
            if(!ca.color.exact(background)) total++;
        }
        return total;
    }

    public void createHintImages() {
        int cellSize = 10;
        int w = W * cellSize;
        int h = H * cellSize;
        int hw = hintW * cellSize;
        int hh = hintH * cellSize;
        leftHintImage = new BufferedImage(hw, h, BufferedImage.TYPE_INT_RGB);
        topHintImage = new BufferedImage(w, hh, BufferedImage.TYPE_INT_RGB);
        List<List<ColorAmount>> rows = hints.rows;
        List<List<ColorAmount>> cols = hints.cols;
        for(int i=0;i<rows.size();i++) {
            List<ColorAmount> row = rows.get(i);
            int rowMax = calculateRealMax(row);
            int x0 = (hw) - (rowMax) * cellSize;
            int y0 = (i) * cellSize;
            for(int j=0;j<rowMax;j++) {
                ColorAmount ca = row.get(j);
                int[] rgbArr = ca.genRGBArray(cellSize);
                leftHintImage.setRGB(x0+j*cellSize, y0, cellSize, cellSize, rgbArr, 0, 0);
            }
        }
        for(int j=0;j<cols.size();j++) {
            List<ColorAmount> col = cols.get(j);
            int colMax = calculateRealMax(col);
            int x0 = (j) * cellSize;
            int y0 = (hh) - (colMax) * cellSize;
            for(int i=0;i<colMax;i++) {
                ColorAmount ca = col.get(i);
                int[] rgbArr = ca.genRGBArray(cellSize);
                topHintImage.setRGB(x0, y0+i*cellSize, cellSize, cellSize, rgbArr, 0, 0);
            }
        }
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

        @Override
        public String toString() {
            return color.toString() + " " + amount;
        }

        int[] genRGBArray(int size) {
            int[] a = new int[size];
            for (int p=0;p<size;p++) {
                a[p] = color.toInt();
            }
            return a;
            //maybe add in the text here
            //g.setColor(ca.color.toColor());
            // g.fillRect(x0+(j)*cellSize, y0, cellSize, cellSize);
            // g.setColor(ca.color.isLight() ? Color.BLACK : Color.WHITE);
            // char[] chars = Integer.toString(ca.amount).toCharArray();
            // g.drawChars(chars, 0, chars.length, x0+(j)*cellSize, y0+cellSize);
        }
    }
}
