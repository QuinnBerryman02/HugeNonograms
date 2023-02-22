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
    JLabel label;
    int bx=50,by=50;
    int viewW = 600;
    int viewH = 600;
    int hintW = 300;
    int hintH = 300;
    int vx=0,vy=0;
    int mx,my;
    float scale = 1f;

    public Window(Nonogram nono) {
        super("Nonogram Program");
        nonogram = nono;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize);
        setLayout(null);
        setVisible(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        label = new JLabel("Sample text");
        label.setFont(label.getFont().deriveFont(20.0F));
        label.setForeground(Color.black);
        label.setSize(new Dimension(1000, 50));
        getContentPane().add(label, BorderLayout.NORTH);
        panel = new DrawPanel();
        getContentPane().add(panel);
        panel.setVisible(true);
        panel.setSize(screenSize);
        validate();
        System.out.println(label);
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
        int dx = newx - mx;
        int dy = newy - my;
        float limitL = 0;
        float limitT = 0;
        float limitR = -viewW*((1.0f / scale) - 1.0f);
        float limitB = -viewW*((1.0f / scale) - 1.0f);
        vx += dx;
        vy += dy;
        vx = Math.round(Math.min(vx, limitL));
        vy = Math.round(Math.min(vy, limitT));
        vx = Math.round(Math.max(vx, limitR));
        vy = Math.round(Math.max(vy, limitB));
        mx = newx;
        my = newy;
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
        float limit = 1f / Math.max(nonogram.W, nonogram.H);
        scale = Math.min(scale, 1.0f);
        scale = Math.max(scale, limit);
        float limitL = 0;
        float limitT = 0;
        float limitR = -viewW*((1.0f / scale) - 1.0f);
        float limitB = -viewW*((1.0f / scale) - 1.0f);
        vx = Math.round(Math.min(vx, limitL));
        vy = Math.round(Math.min(vy, limitT));
        vx = Math.round(Math.max(vx, limitR));
        vy = Math.round(Math.max(vy, limitB));
        repaint();
    }

    class DrawPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            BufferedImage lhi = nonogram.leftHintImage;
            BufferedImage thi = nonogram.topHintImage;
            BufferedImage image = nonogram.image; 
            int vccW = Math.round(scale * image.getWidth());
            int vccH = Math.round(scale * image.getHeight());
            int vccMax = Math.max(vccW, vccH);
            int realVx = Math.round((float)vx / viewW * vccW);
            int realVy = Math.round((float)vy / viewH * vccH);
            // if(vccMax > image.getWidth()) {
            //     //center image horizontally
            //     g.drawImage(lhi, bx, by+hintH, bx+hintW, by+hintH+viewH, 0, -realVy, lhi.getWidth(), -realVy+vccH, Color.magenta, getContentPane());
            //     //bx+hintW = box start
            //     //bx+hintW+viewW = box end
            //     //viewW = box width
            //     //bx+hintW+viewW/2 = image center
            //     //og vccW / max vcc * box width = image width
            //     //image start = image center - imagewidth/2 
            //     //image end = image center + imagewidth/2 
            //     //bx+hintW+viewW/2 - (og vccW / max vcc * box width)/2

            //     //imagewidth = 1 when vccMax = i.w
            //     //imagewidth = i.w/i.h when vccMax = i.h = vccMax / scale ie scale = 1
            //     //imagewidth = i.w / vccMax
            //     //vccW = scale * i.w
            //     //vccH = scale * i.h
            //     //vccMax = Math.max(vccW, vccH);
            //     int imageCenterX = bx+hintW+viewW/2;
            //     int imageWidth = (int)((float)image.getWidth() / vccMax * viewW);
            //     g.drawImage(thi, imageCenterX-imageWidth/2, by, imageCenterX+imageWidth/2, by+hintH, 0, 0, image.getWidth(), thi.getHeight(), Color.magenta, getContentPane());
            //     g.drawImage(image, imageCenterX-imageWidth/2, by+hintH, imageCenterX+imageWidth/2, by+hintH+viewH, -realVx, -realVy, -realVx+vccW, -realVy+vccH, Color.magenta, getContentPane());
            // } else if(vccMax > image.getHeight()) {
            //     //center image vertically
            //     g.drawImage(thi, bx+hintW, by, bx+hintW+viewW, by+hintH, -realVx, 0, -realVx+vccW, thi.getHeight(), Color.magenta, getContentPane());
            //     int imageCenterY = by+hintH+viewH/2;
            //     int imageHeight = (int)((float)image.getHeight() / vccMax * viewH);
            //     g.drawImage(lhi, bx, imageCenterY-imageHeight/2, bx+hintW, imageCenterY+imageHeight/2, 0, -realVy, lhi.getWidth(), -realVy+vccH, Color.magenta, getContentPane());
            //     g.drawImage(image, bx+hintW, imageCenterY-imageHeight/2, bx+hintW+viewW, imageCenterY+imageHeight/2, -realVx, -realVy, -realVx+vccW, -realVy+vccH, Color.magenta, getContentPane());
            // } else {
                vccW = vccMax;
                vccH = vccMax;
                g.drawImage(lhi, bx, by+hintH, bx+hintW, by+hintH+viewH, 0, -realVy, lhi.getWidth(), -realVy+vccH, Color.magenta, getContentPane());
                g.drawImage(thi, bx+hintW, by, bx+hintW+viewW, by+hintH, -realVx, 0, -realVx+vccW, thi.getHeight(), Color.magenta, getContentPane());   
                g.drawImage(image, bx+hintW, by+hintH, bx+hintW+viewW, by+hintH+viewH, -realVx, -realVy, -realVx+vccW, -realVy+vccH, Color.magenta, getContentPane());
            // }
            // image, dst rect, src rect, bgcolor, contentpane
            g.drawRect(bx, by+hintH, hintW, viewH);   //left hint box
            g.drawRect(bx+hintW, by, viewW, hintH);   //top hint box
            g.drawRect(bx+hintW, by+hintH, viewW, viewH); //main image box
            label.setText("vccW: " + vccW + " , vccH: " + vccH + ", vccMax: " + vccMax + ", guess: " + (image.getHeight() / (float)image.getWidth() / scale * viewW));
        }
    }
}  

