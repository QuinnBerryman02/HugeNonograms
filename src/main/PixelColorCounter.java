package src.main;

import src.util.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
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
            reduceMatchCompare(f.getName().split("[.]")[0]);
        }
    }

    public static void reduceMatchCompare(String name) {
        String inFile = inFolder + name + ".png";
        String midFile = midFolder + name + "_temporary" + ".png";
        String finalFile = finalFolder + name + "_final" + ".png";
        String compareFile = compareFolder + name + "_compare" + ".png";
        BufferedImage img1 = stringToImage(inFile);
        BufferedImage img2,img3;
        System.out.println("-----------------------------------------------");
        Palette paletteGoal = readPaletteFile(matchingPalette);
        Palette paletteReduction = new Palette(img1, false);
        System.out.println("Total Goal Colors = " + paletteGoal.size());
        System.out.println("PNG = " + name);
        System.out.println("First Iteration Picture Colors = " + paletteReduction.size());
        paletteReduction.reduceDots();
        System.out.println("Second Iteration Picture Colors = " + paletteReduction.size());
        img2 = simplifyPng(img1, midFile, paletteReduction);  //simplify original
        img3 = simplifyPng(img2, finalFile, paletteGoal);  //match to desired
        comparePNGs(compareFile, img1, img2, img3);             //compare results
        Palette paletteResult = new Palette(img3, true);
        System.out.println("Best Match");
        paletteResult.display();
        System.out.println("\n-----------------------------------------------");
    }

    public static BufferedImage stringToImage(String name) {
        File file = new File(name);
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Palette readPaletteFile(String name) {
        File file=new File(name); 
        try {
            return new Palette(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage comparePNGs(String output, BufferedImage... images) {
        int w = images[0].getWidth();
        int h = images[0].getHeight();
        BufferedImage newImage = new BufferedImage(w*images.length, h, BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<h;i++) {
            for(int j=0;j<w;j++) {
                for(int a=0;a<images.length;a++) {
                    newImage.setRGB(j+a*w, i, images[a].getRGB(j, i));
                }
            }
        }
        try {
            File newFile = new File(output);
            ImageIO.write(newImage, "png", newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newImage;
    }

    public static BufferedImage simplifyPng(BufferedImage image, String output, Palette palette) {
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
        try {
            File newFile = new File(output);
            ImageIO.write(newImage, "png", newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newImage;
    }
}


