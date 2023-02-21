package src.main;

import javax.swing.*;

import java.awt.image.BufferedImage;
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
    int bx=50,by=50;
    int viewW = 700;
    int viewH = 700;
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
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        //g.setFont(new Font("my font",Font.ITALIC,CELL_SIZE));
        BufferedImage lhi = nonogram.leftHintImage;
        BufferedImage thi = nonogram.topHintImage;
        BufferedImage image = nonogram.image; 
        // image, dst rect, src rect, bgcolor, contentpane
        int vccW = Math.round(scale * image.getWidth());
        int vccH = Math.round(scale * image.getHeight());
        System.out.println("scale: " + scale);
        System.out.println("width: " + (float)image.getWidth());
        System.out.println("heigth: " + (float)image.getHeight());
        System.out.println("w/h: " + (image.getWidth()/(float)image.getHeight()));
        System.out.println("h/w: " + (image.getHeight()/(float)image.getWidth()));
        int realVx = Math.round((float)vx / viewW * vccW);
        int realVy = Math.round((float)vy / viewH * vccH);
        if ((vx <= 0) && (vy <= 0) && (vx >= -viewW*((1.0f / scale) - 1.0f)) && (vy >= -viewW*((1.0f / scale) - 1.0f))) {
            g.setColor(Color.green);
        } else {
            g.setColor(Color.red);
        }
        g.fillRect(bx, by, hintW, hintH);
        g.setColor(Color.black);
        System.out.println((vx <= 0));
        System.out.println((vy <= 0));
        System.out.println(vx >= -viewW*((1.0f / scale) - 1.0f));
        System.out.println(vy >= -viewW*((1.0f / scale) - 1.0f));
        System.out.println("vx: " + -vx + " -> " + (-((float)vx / viewW * vccW)+vccW));
        System.out.println("vy: " + -vy + " -> " + (-((float)vy / viewH * vccH)+vccH));
        g.drawImage(lhi, bx, by+hintH, bx+hintW, by+hintH+viewH, 0, -realVy, lhi.getWidth(), -realVy+vccH, Color.magenta, getContentPane());
        g.drawImage(thi, bx+hintW, by, bx+hintW+viewW, by+hintH, -realVx, 0, -realVx+vccW, thi.getHeight(), Color.magenta, getContentPane());
        g.drawImage(image, bx+hintW, by+hintH, bx+hintW+viewW, by+hintH+viewH, -realVx, -realVy, -realVx+vccW, -realVy+vccH, Color.magenta, getContentPane());
        g.drawRect(bx, by+hintH, hintW, viewH);   //left hint box
        g.drawRect(bx+hintW, by, viewW, hintH);   //top hint box
        g.drawRect(bx+hintW, by+hintH, viewW, viewH); //main image box
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
        vx = Math.round(Math.min(vx, limitL));
        vy = Math.round(Math.min(vy, limitT));
        vx = Math.round(Math.max(vx, limitR));
        vy = Math.round(Math.max(vy, limitB));
        vx += dx;
        vy += dy;
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
        System.out.println(scale);
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
}  