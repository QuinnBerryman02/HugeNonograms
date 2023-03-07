package src.main;

import javax.swing.*;

import java.awt.image.BufferedImage;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.Toolkit;

public class Window extends JFrame implements MouseListener, MouseMotionListener, MouseWheelListener {  
    Nonogram nonogram;
    DrawPanel panel;
    JTextArea label;
    int bx=50,by=50;    //base coordinate to start drawing
    int viewW = 600;    //size of the nonogram view
    int viewH = 600;    
    int hintW = 300;    //size of the hint boxes
    int hintH = 300;
    int vx=0,vy=0;      //offset of the image 
    int mx,my;          //tracker of mouse position for dragging 
    float scale = 1f;   //tracker of scale for the mousewheel listener
    int cells = 1;      //maximum amount of cells that can be displayed in the view

    public Window(Nonogram nono) {
        super("Nonogram Program");
        nonogram = nono;
        cells = Math.max(nonogram.W, nonogram.H);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize);
        setLayout(null);
        setVisible(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        label = new JTextArea("Sample text");
        label.setFont(label.getFont().deriveFont(20.0F));
        label.setForeground(Color.black);
        label.setSize(new Dimension(1000, 150));
        getContentPane().add(label, BorderLayout.NORTH);
        panel = new DrawPanel();
        getContentPane().add(panel);
        panel.setVisible(true);
        panel.setSize(screenSize);
        validate();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        int newx = e.getX();
        int newy = e.getY();
        vx -= (newx - mx);
        vy -= (newy - my);

        float ratioY = (float)nonogram.H / cells;
        float ratioX = (float)nonogram.W / cells;

        vx = (int)Math.max(vx, 0);                      //left
        vy = (int)Math.max(vy, 0);                      //top
        vx = (int)Math.min(vx, (ratioX - 1) * viewW);   //right
        vy = (int)Math.min(vy, (ratioY - 1) * viewH);   //bottom

        mx = newx;
        my = newy;

        if(ratioX < 1) vx = 0;        //if image isnt zoomed in enough for equal aspect ratio
        if(ratioY < 1) vy = 0;        //set the offset to 0
        
        repaint();  
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mx = e.getX();
        my = e.getY();

    }

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) { //decreases on zoom in
        if(e.getPreciseWheelRotation() > 0) scale*=1.05;
        if(e.getPreciseWheelRotation() < 0) scale*=0.95;
        int maxDim = Math.max(nonogram.W, nonogram.H);
        scale = Math.min(1, scale);
        scale = Math.max(1.0f / maxDim, scale);
        cells = (int)(scale * maxDim);

        float ratioY = (float)nonogram.H / cells;
        float ratioX = (float)nonogram.W / cells;

        vx = (int)Math.max(vx, 0);                      //left
        vy = (int)Math.max(vy, 0);                      //top
        vx = (int)Math.min(vx, (ratioX - 1) * viewW);   //right
        vy = (int)Math.min(vy, (ratioY - 1) * viewH);   //bottom

        if(ratioX < 1) vx = 0;        //if image isnt zoomed in enough for equal aspect ratio
        if(ratioY < 1) vy = 0;        //set the offset to 0
        
        repaint();
    }

    class DrawPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            long start = System.nanoTime();
            super.paintComponent(g);
            BufferedImage lhi = nonogram.leftHintImage;
            BufferedImage thi = nonogram.topHintImage;
            BufferedImage image = nonogram.image; 

            int imgW = image.getWidth();               
            int imgH = image.getHeight();
            int bhx = bx + hintW;                               //base hint x
            int bhy = by + hintH;                               //base hint y  
            float ratioX = Math.min(1, (float)imgW / cells);    //how much of the view the img
            float ratioY = Math.min(1, (float)imgH / cells);    //can take up at this scale

            int ssX = Math.round((float)vx / viewW * cells);    //src start X
            int ssY = Math.round((float)vy / viewH * cells);    //src start Y
            int seX = Math.min(ssX+cells, imgW);                //src end X
            int seY = Math.min(ssY+cells, imgH);                //src end Y
            
            int dsX = (int)((0.5f - ratioX/2) * viewW);         //dst start X
            int dsY = (int)((0.5f - ratioY/2) * viewH);         //dst start Y
            int deX = (int)((0.5f + ratioX/2) * viewW);         //dst end X
            int deY = (int)((0.5f + ratioY/2) * viewH);         //dst end Y
            
            //          image,  dst x0,      dst y0,    dst x1,     dst y1,     src x0,     src y0,     src x1,         src y1,             bgcolor,       contentpane
            g.drawImage(thi,    bhx+dsX,     by,        bhx+deX,    bhy,        ssX,        0,          seX,            thi.getHeight(),    Color.magenta, getContentPane()); 
            g.drawImage(lhi,    bx,          bhy+dsY,   bhx,        bhy+deY,    0,          ssY,        lhi.getWidth(), seY,                Color.magenta, getContentPane()); 
            g.drawImage(image,  bhx+dsX,     bhy+dsY,   bhx+deX,    bhy+deY,    ssX,        ssY,        seX,            seY,                Color.magenta, getContentPane());
            
            g.drawRect(bx, bhy, hintW, viewH);   //left hint box
            g.drawRect(bhx, by, viewW, hintH);   //top hint box
            g.drawRect(bhx, bhy, viewW, viewH); //main image box
            
            long end = System.nanoTime();
            long length = end - start;
            String s1 = "Paint took: " + (length / 1000000) + " ms";
            String s2 = "ssX: " + (ssX) + " seX: " + seX;
            String s3 = "ssY: " + (ssY) + " seY: " + seY;
            String s4 = "scale: " + scale + " -> discreteScale: " + ((float)cells / Math.max(nonogram.W, nonogram.H)) + " rawCells: " + (scale * Math.max(nonogram.W, nonogram.H)) + " -> cells: " + cells;
            String s5 = "seX/W: " + ((float)seX / imgW) + " seY/H: " + ((float)seY / imgH);
            //label.setText();
            label.setText(s1 + "\n" + s2 + "\n" + s3 + "\n" + s4 + "\n" + s5);
        }
    }
}  

