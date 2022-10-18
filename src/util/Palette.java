package src.util;

import java.util.ArrayList;
import java.util.List;
import src.util.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Palette {
    List<SimpleColor> colors = new ArrayList<SimpleColor>();
    List<Integer> amounts = new ArrayList<Integer>();
    int size = 0;

    public Palette(File name) throws IOException {
        FileReader fr=new FileReader(name);   
        BufferedReader br=new BufferedReader(fr);  
        String line;  
        while((line=br.readLine())!=null) {  
            addColor(new SimpleColor(line));
        }  
        fr.close();
    }

    public Palette(BufferedImage image, boolean strict) {
        int w = image.getWidth();
        int h = image.getHeight();
        for(int i=0;i<h;i++) {
            for(int j=0;j<w;j++) {
                addPixel(new SimpleColor(image.getRGB(j, i)), strict ? 0 : 1);
            }
        }
    }

    public int size() {return size;}

    public void reduceDots() {
        for(int i=0;i<size;i++) {
            int strict = 0;
            if(amounts.get(i) < 10) strict = 8;
            else if (amounts.get(i) < 50) strict = 4;
            else if (amounts.get(i) < 100) strict = 2;
            else if (amounts.get(i) < 200) strict = 1;
            else continue;
            SimpleColor sc = findClosest(colors.get(i), strict);
            SimpleColor.strictness = 0;
            if(!sc.equals(colors.get(i))) {
                int newColorIndex = colors.indexOf(sc);
                amounts.set(newColorIndex,amounts.get(newColorIndex)+amounts.get(i));
                colors.remove(i);
                amounts.remove(i);
                size--;
                i--;
            }
        }
    }

    public void addColor(SimpleColor sc) {
        colors.add(sc);
        amounts.add(0);
        size++;
    }

    public void addPixel(SimpleColor sc, int strictness) {
        SimpleColor.strictness = strictness;
        int index = colors.indexOf(sc);
        if(index == -1) {
            colors.add(sc);
            amounts.add(1);
            size++;
        } else {
            amounts.set(index,amounts.get(index)+1);
        }
    }

    public SimpleColor findRealClosest(SimpleColor sc) {
        int strict = 0;
        while(SimpleColor.strictness * SimpleColor.RANGE <= 255) {
            SimpleColor.strictness = strict;
            int index = colors.indexOf(sc);
            if(index != -1) {
                return colors.get(index);
            }
            strict++;
        }
        return null;
    }

    public SimpleColor findClosest(SimpleColor sc, int strictness) {
        SimpleColor.strictness = strictness;
        int index = colors.indexOf(sc);
        if(index == -1) {
            return sc;
        } else {
            return colors.get(index);
        }
    }

    public SimpleColor findLargestAmount() {
        int max=Integer.MIN_VALUE;
        SimpleColor sc = null;
        for(int i=0;i<size;i++) {
            if(amounts.get(i)>max) {
                max = amounts.get(i);
                sc = colors.get(i);
            }
        }
        return sc;
    }

    public void display() {
        displayShort();
        for(int i=0;i<size;i++) {
            SimpleColor sc = colors.get(i);
            System.out.print(sc + "-["+ amounts.get(i) + "]");
        }
    }
    public void displayShort() {
        if(size<20) {
            for(int i=0;i<size;i++) {
                SimpleColor sc = colors.get(i);
                System.out.print(sc + "-["+ amounts.get(i) + "]");
            }
        }
        System.out.println("\nTotal colours = " + size);
    }
}