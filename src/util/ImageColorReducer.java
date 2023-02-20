package src.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageColorReducer {
    static String inputFolder = "resources/input/";
    static String middleFolder= "resources/middle/";
    static String finalFolder = "resources/final/";
    static String compareFolder = "resources/compare/";
    static String matchingPalette = "legopalette.txt";
    
    public static void main(String[] args) {
        generateOuputsForInputs(inputFolder);
    }

    public static void generateOuputsForInputs(String foldername) {
        File folder = new File(foldername);
        File[] inputs = folder.listFiles();
        for (File f : inputs) {
            reduceMatchCompare(f.getName());
        }
    }

    public static void reduceMatchCompare(String filename) {
        String inputFilename = inputFolder + filename;
        String middleFilename = middleFolder + filename;
        String finalFilename = finalFolder + filename;
        String compareFilename = compareFolder + filename;
        BufferedImage originalImage = openStringAsImage(inputFilename);
        BufferedImage reducedImage, forcedImage;
        Palette paletteGoal = new Palette(matchingPalette);
        Palette paletteReduction = new Palette(originalImage, false);
        System.out.println("-----------------------------------------------");
        System.out.println("PNG = " + filename + " " + originalImage.getWidth() + "x" + originalImage.getHeight());
        System.out.println("Total Goal Colors = " + paletteGoal.size());
        paletteGoal.show();
        int orginalSize = paletteReduction.size();
        System.out.println("Original Picture Colors = " + orginalSize);
        paletteReduction.show();
        paletteReduction.reduceDots();
        int reducedSize = paletteReduction.size();
        System.out.println("Reduced Picture Colors = " + reducedSize + " -- efficiency: " + (orginalSize * 100f / reducedSize) + "%");
        paletteReduction.show();
        reducedImage = colorImageByPalette(originalImage, middleFilename, paletteReduction);
        forcedImage = colorImageByPalette(reducedImage, finalFilename, paletteGoal);
        compareImages(compareFilename, originalImage, reducedImage, forcedImage);        
        Palette paletteResult = new Palette(forcedImage, true);
        System.out.println("Force Fit Picture Colors = " + paletteResult.size());
        paletteResult.show();
        System.out.println("-----------------------------------------------");
    }

    public static BufferedImage openStringAsImage(String filename) {
        File file = new File(filename);
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage compareImages(String name, BufferedImage... images) {
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
            File newFile = new File(name);
            ImageIO.write(newImage, "png", newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newImage;
    }

    public static BufferedImage colorImageByPalette(BufferedImage image, String name, Palette palette) {
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
            File newFile = new File(name);
            ImageIO.write(newImage, "png", newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newImage;
    }
}


