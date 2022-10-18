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
    static String compareFolder = "resources/compare/";
    static String finalLegoFolder = "resources/final/";
    public static void main(String[] args) {
        generateOuputs();
    }

    

    public static void generateOuputs() {
        File folder = new File(inFolder);
        File[] inputs = folder.listFiles();
        for (File f : inputs) {
            System.out.println(f.getName().split("[.]")[0]);
            try {
                runLego(f.getName().split("[.]")[0]);
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

    public static void runLego(String name) throws IOException {
        String inFile = inFolder + name + ".png";
        String midFile = midFolder + name + "_temporary" + ".png";
        String compareFile = compareFolder + name + "_compare" + ".png";
        String finalFile = finalLegoFolder + name + "_final" + ".png";
        System.out.println("-----------------------------------------------");
        Palette paletteGoal = createPaletteFromFile("legopalette.txt");
        Palette paletteAttempt = readPng(inFile);
        System.out.println("Total Lego Colors = " + paletteGoal.size());
        System.out.println("PNG = " + name);
        System.out.println("First Iteration Picture Colors = " + paletteAttempt.size());
        paletteAttempt.reduceDots();
        System.out.println("Second Iteration Picture Colors = " + paletteAttempt.size());
        simplifyPng(inFile, midFile, paletteAttempt);
        simplifyPng(midFile, finalFile, paletteGoal);
        comparePng(inFile, midFile, finalFile, compareFile);
        Palette paletteSuccess = readPngStrict(finalFile);
        System.out.println("Best Match has " + paletteSuccess.count() + " dots costing " + paletteSuccess.calculateCost() + " euro.");
        paletteSuccess.display();
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

    public static Palette readPngStrict(String name) throws IOException {
        File file = new File(name);
        BufferedImage image = ImageIO.read(file);
        int w = image.getWidth();
        int h = image.getHeight();
        Palette palette = new Palette();
        for(int i=0;i<h;i++) {
            for(int j=0;j<w;j++) {
                palette.addPixelStrict(new SimpleColor(image.getRGB(j, i)));
            }
        }
        return palette;
    }

    public static Palette readPng(String name) throws IOException {
        File file = new File(name);
        BufferedImage image = ImageIO.read(file);
        int w = image.getWidth();
        int h = image.getHeight();
        System.out.println(w + "x" + h);
        Palette palette = new Palette();
        for(int i=0;i<h;i++) {
            for(int j=0;j<w;j++) {
                palette.addPixel(new SimpleColor(image.getRGB(j, i)));
            }
        }
        return palette;
    }
}


