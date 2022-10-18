package src.main;

import src.util.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class PixelColorCounter {
    static String inFolder = "resources/input/";
    static String midFolder= "resources/middle/";
    static String finalFolder = "resources/final/";
    static String compareFolder = "resources/compare/";
    static String matchingPalette = "legopalette.txt";
    public static void main(String[] args) {
        generateOuputsForInputs();
    }

    

    public static void generateOuputsForInputs() {
        File folder = new File(inFolder);
        File[] inputs = folder.listFiles();
        for (File f : inputs) {
            System.out.println(f.getName().split("[.]")[0]);
            try {
                reduceMatchCompare(f.getName().split("[.]")[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Palette createPaletteFromFile(String name) throws IOException {
        File file=new File(name); 
        FileReader fr=new FileReader(file);   
        BufferedReader br=new BufferedReader(fr);  
        String line;  
        Palette palette = new Palette();
        while((line=br.readLine())!=null) {  
            palette.addColor(new SimpleColor(line));
        }  
        fr.close();
        return palette;
    }

    public static void reduceMatchCompare(String name) throws IOException {
        String inFile = inFolder + name + ".png";
        String midFile = midFolder + name + "_temporary" + ".png";
        String finalFile = finalFolder + name + "_final" + ".png";
        String compareFile = compareFolder + name + "_compare" + ".png";
        System.out.println("-----------------------------------------------");
        Palette paletteGoal = createPaletteFromFile(matchingPalette);
        Palette paletteReduction = readPng(inFile, false);
        System.out.println("Total Goal Colors = " + paletteGoal.size());
        System.out.println("PNG = " + name);
        System.out.println("First Iteration Picture Colors = " + paletteReduction.size());
        paletteReduction.reduceDots();
        System.out.println("Second Iteration Picture Colors = " + paletteReduction.size());
        simplifyPng(inFile, midFile, paletteReduction);  //simplify original
        simplifyPng(midFile, finalFile, paletteGoal);  //match to desired
        comparePng(inFile, midFile, finalFile, compareFile);
        Palette paletteResult = readPng(finalFile, true);
        System.out.println("Best Match");
        paletteResult.display();
        System.out.println("\n-----------------------------------------------");
    }

    public static void comparePng(String p1, String p2, String p3, String output) throws IOException {
        File file1 = new File(p1);
        File file2 = new File(p2);
        File file3 = new File(p3);
        File newFile = new File(output);
        BufferedImage image1 = ImageIO.read(file1);
        BufferedImage image2 = ImageIO.read(file2);
        BufferedImage image3 = ImageIO.read(file3);
        int w = image1.getWidth();
        int h = image1.getHeight();
        BufferedImage newImage = new BufferedImage(w*3, h, BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<h;i++) {
            for(int j=0;j<w;j++) {
                newImage.setRGB(j, i, image1.getRGB(j, i));
                newImage.setRGB(j+w, i, image2.getRGB(j, i));
                newImage.setRGB(j+w+w,i, image3.getRGB(j, i));
            }
        }
        ImageIO.write(newImage, "png", newFile);
    }

    public static void simplifyPng(String input, String output, Palette palette) throws IOException {
        File file = new File(input);
        File newFile = new File(output);
        BufferedImage image = ImageIO.read(file);
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        SimpleColor color;
        for(int i=0;i<h;i++) {
            for(int j=0;j<w;j++) {
                color = new SimpleColor(image.getRGB(j, i));
                color = palette.findRealClosest(color);
                newImage.setRGB(j, i, color.toInt());
            }
        }
        ImageIO.write(newImage, "png", newFile);
    }

    public static Palette readPng(String name, boolean strict) throws IOException {
        File file = new File(name);
        BufferedImage image = ImageIO.read(file);
        int w = image.getWidth();
        int h = image.getHeight();
        System.out.println(w + "x" + h);
        Palette palette = new Palette();
        for(int i=0;i<h;i++) {
            for(int j=0;j<w;j++) {
                palette.addPixel(new SimpleColor(image.getRGB(j, i)), strict ? 0 : 1);
            }
        }
        return palette;
    }
}


