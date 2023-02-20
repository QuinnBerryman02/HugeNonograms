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
    Hints hints;
    Hints obscuredHints;
    Palette palette;
    SimpleColor background;
    BufferedImage image;
    BufferedImage leftHintImage;
    BufferedImage topHintImage;
    int W=0,H=0;

    public Nonogram(BufferedImage image) {
        this.image = image; 
        W = image.getWidth();
        H = image.getHeight();
        palette = new Palette(image, true);
        background = palette.findLargestAmount();
        hints = new Hints(image);
        obscuredHints = hints.obscure(background);
        log();
        createHintImages(obscuredHints);
        try {
            ImageIO.write(topHintImage, "png", new File("tophint.png"));
            ImageIO.write(leftHintImage, "png", new File("lefthint.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Hints getHints() {
        return obscuredHints;
    }

    public void createHintImages(Hints hints) {
        int cellSize = 1;
        int w = W * cellSize;
        int h = H * cellSize;
        int hw = hints.maxW * cellSize;
        int hh = hints.maxH * cellSize;
        leftHintImage = new BufferedImage(hw, h, BufferedImage.TYPE_INT_RGB);
        topHintImage = new BufferedImage(w, hh, BufferedImage.TYPE_INT_RGB);
        List<List<ColorAmount>> rows = hints.rows;
        List<List<ColorAmount>> cols = hints.cols;
        for(int i=0;i<rows.size();i++) {
            List<ColorAmount> row = rows.get(i);
            int rowMax = row.size();
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
            int colMax = col.size();
            int x0 = (j) * cellSize;
            int y0 = (hh) - (colMax) * cellSize;
            for(int i=0;i<colMax;i++) {
                ColorAmount ca = col.get(i);
                int[] rgbArr = ca.genRGBArray(cellSize);
                topHintImage.setRGB(x0, y0+i*cellSize, cellSize, cellSize, rgbArr, 0, 0);
            }
        }
    }

    public void log() {
        System.out.println("New Nonogram created");
        System.out.println("Size: " + W + "x" + H);
        System.out.println("Theoretical Hint Sizes: " + hints.maxW + "x" + hints.maxH);
        System.out.println("Obscured Hint Sizes: " + obscuredHints.maxW + "x" + obscuredHints.maxH);
        palette.show();
        System.out.println("Background color is: " + background);
    }

    class Hints {
        List<List<ColorAmount>> rows = new ArrayList<List<ColorAmount>>();
        List<List<ColorAmount>> cols = new ArrayList<List<ColorAmount>>();
        int maxW = Integer.MIN_VALUE;
        int maxH = Integer.MIN_VALUE;

        public Hints() {}

        public Hints(BufferedImage image) {
            int W = image.getWidth();
            int H = image.getHeight();
            SimpleColor curColor;
            ColorAmount prevCA;
            for(int i=0;i<H;i++) {
                ArrayList<ColorAmount> row = new ArrayList<ColorAmount>();
                int size = 0;
                for(int j=0;j<W;j++) {
                    size++;
                    curColor = new SimpleColor(image.getRGB(j, i));
                    if(row.size()==0) {
                        row.add(0, new ColorAmount(curColor, 1));
                        continue;
                    }
                    prevCA = row.get(0);
                    if(!prevCA.color.exact(curColor)) {
                        row.add(0, new ColorAmount(curColor, 1));
                    } else {
                        prevCA.incr();
                        size--;
                    }
                }
                if(size>maxW) maxW=size;
                Collections.reverse(row);
                rows.add(row);
            }
            for(int j=0;j<W;j++) {
                ArrayList<ColorAmount> col = new ArrayList<ColorAmount>();
                int size = 0;
                for(int i=0;i<H;i++) {
                    size++;
                    curColor = new SimpleColor(image.getRGB(j, i));
                    if(col.size()==0) {
                        col.add(0, new ColorAmount(curColor, 1));
                        continue;
                    }
                    prevCA = col.get(0);
                    if(!prevCA.color.exact(curColor)) {
                        col.add(0, new ColorAmount(curColor, 1));
                    } else {
                        prevCA.incr();
                        size--;
                    }
                }
                if(size>maxH) maxH=size;
                Collections.reverse(col);
                cols.add(col);
            }
        }

        public Hints obscure(SimpleColor ignore) {
            Hints obscured = new Hints();
            for(int i=0;i<rows.size();i++) {
                ArrayList<ColorAmount> newrow = new ArrayList<ColorAmount>();
                int size = 0;
                for(ColorAmount ca : rows.get(i)) {
                    if(!ca.color.exact(ignore)) {
                        newrow.add(ca);
                        size++;
                    } 
                }
                if(size>obscured.maxW) obscured.maxW=size;
                obscured.rows.add(newrow);
            }
            for(int i=0;i<cols.size();i++) {
                ArrayList<ColorAmount> newcol = new ArrayList<ColorAmount>();
                int size = 0;
                for(ColorAmount ca : cols.get(i)) {
                    if(!ca.color.exact(ignore)) {
                        newcol.add(ca);
                        size++;
                    }
                }
                if(size>obscured.maxH) obscured.maxH=size;
                obscured.cols.add(newcol);
            }
            return obscured;
        }

        void log() {
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

    class ColorAmount {
        SimpleColor color;
        int amount;

        public ColorAmount(SimpleColor sc, int amt) {
            color = sc;
            amount = amt;
        }

        void incr() {
            amount++;
        }
    
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
