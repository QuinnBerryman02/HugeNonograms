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
        float ratioY = 1.0f / scale;
        float ratioX = 1.0f / scale;
        if(nonogram.H < nonogram.W) {
            ratioY = (float)nonogram.H / nonogram.W / scale;
        } else if (nonogram.W < nonogram.H) {
            ratioX = (float)nonogram.W / nonogram.H / scale;
        }
        float limitL = 0;
        float limitT = 0;
        float limitR = (-ratioX + 1) * viewW;
        float limitB = (-ratioY + 1) * viewH;
        
        vx += dx;
        vy += dy;
        vx = Math.round(Math.min(vx, limitL));
        vy = Math.round(Math.min(vy, limitT));
        vx = Math.round(Math.max(vx, limitR));
        vy = Math.round(Math.max(vy, limitB));
        mx = newx;
        my = newy;
        if(ratioX < 1) vx = 0;
        if(ratioY < 1) vy = 0;
        
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
        float ratioY = 1.0f / scale;
        float ratioX = 1.0f / scale;
        if(nonogram.H < nonogram.W) {
            ratioY = (float)nonogram.H / nonogram.W / scale;
        } else if (nonogram.W < nonogram.H) {
            ratioX = (float)nonogram.W / nonogram.H / scale;
        }
        float limitL = 0;
        float limitT = 0;
        float limitR = (-ratioX + 1) * viewW;
        float limitB = (-ratioY + 1) * viewH;
        vx = Math.round(Math.min(vx, limitL));
        vy = Math.round(Math.min(vy, limitT));
        vx = Math.round(Math.max(vx, limitR));
        vy = Math.round(Math.max(vy, limitB));
        if(ratioX < 1) vx = 0;
        if(ratioY < 1) vy = 0;
        
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
            int realVx = Math.round((float)vx / viewW * vccMax);
            int realVy = Math.round((float)vy / viewH * vccMax);
            if(image.getWidth() < image.getHeight()) {
                float ratio = (float)image.getWidth() / image.getHeight() / scale;
                float start = 0.5f - ratio/2;
                float end = 0.5f + ratio/2;
                if(ratio >= 1.0f) {
                    g.drawImage(thi,    bx+hintW,           by,         bx+hintW+viewW,     by+hintH,       -realVx,    0,          -realVx+vccH,   thi.getHeight(),            Color.magenta, getContentPane()); 
                    g.drawImage(lhi,    bx,                 by+hintH,   bx+hintW,           by+hintH+viewH, 0,          -realVy,    lhi.getWidth(), -realVy+vccH,               Color.magenta, getContentPane()); 
                    g.drawImage(image,  bx+hintW,           by+hintH,   bx+hintW+viewW,     by+hintH+viewH, -realVx,    -realVy,    -realVx+vccH,   -realVy+vccH,               Color.magenta, getContentPane());
                } else {
                    int realStart = (int)(start * viewH);
                    int realEnd = (int)(end * viewH);
                    g.drawImage(thi,    bx+hintW+realStart, by,         bx+hintW+realEnd,   by+hintH,       -realVx,    0,          -realVx+image.getWidth(),   thi.getHeight(),            Color.magenta, getContentPane());  
                    g.drawImage(lhi,    bx,                 by+hintH,   bx+hintW,           by+hintH+viewH, 0,          -realVy,    lhi.getWidth(),             -realVy+vccH,  Color.magenta, getContentPane());
                    g.drawImage(image,  bx+hintW+realStart, by+hintH,   bx+hintW+realEnd,   by+hintH+viewH, -realVx,    -realVy,    -realVx+image.getWidth(),   -realVy+vccH,  Color.magenta, getContentPane());
                }
            }
            if(image.getHeight() < image.getWidth()) {
                float ratio = (float)image.getHeight() / image.getWidth() / scale;
                float start = 0.5f - ratio/2;
                float end = 0.5f + ratio/2;
                if(ratio >= 1.0f) {
                    g.drawImage(thi,    bx+hintW,   by,                 bx+hintW+viewW, by+hintH,           -realVx,    0,          -realVx+vccW,   thi.getHeight(),            Color.magenta, getContentPane()); 
                    g.drawImage(lhi,    bx,         by+hintH,           bx+hintW,       by+hintH+viewH,     0,          -realVy,    lhi.getWidth(), -realVy+vccW,               Color.magenta, getContentPane());  
                    g.drawImage(image,  bx+hintW,   by+hintH,           bx+hintW+viewW, by+hintH+viewH,     -realVx,    -realVy,    -realVx+vccW,   -realVy+vccW,               Color.magenta, getContentPane());
                } else {
                    int realStart = (int)(start * viewH);
                    int realEnd = (int)(end * viewH);
                    g.drawImage(thi,    bx+hintW,   by,                 bx+hintW+viewW, by+hintH,           -realVx,    0,          -realVx+vccW,   thi.getHeight(),            Color.magenta, getContentPane()); 
                    g.drawImage(lhi,    bx,         by+hintH+realStart, bx+hintW,       by+hintH+realEnd,   0,          -realVy,    lhi.getWidth(), -realVy+image.getHeight(),  Color.magenta, getContentPane());  
                    g.drawImage(image,  bx+hintW,   by+hintH+realStart, bx+hintW+viewW, by+hintH+realEnd,   -realVx,    -realVy,    -realVx+vccW,   -realVy+image.getHeight(),  Color.magenta, getContentPane());
                }
                
            }
            // g.drawImage(lhi, bx, by+hintH, bx+hintW, by+hintH+viewH, 0, -realVy, lhi.getWidth(), -realVy+vccH, Color.magenta, getContentPane());
            // g.drawImage(thi, bx+hintW, by, bx+hintW+viewW, by+hintH, -realVx, 0, -realVx+vccW, thi.getHeight(), Color.magenta, getContentPane());   
            // g.drawImage(image, bx+hintW, by+hintH, bx+hintW+viewW, by+hintH+viewH, -realVx, -realVy, -realVx+vccW, -realVy+vccH, Color.magenta, getContentPane());
            // image, dst rect, src rect, bgcolor, contentpane
            g.drawRect(bx, by+hintH, hintW, viewH);   //left hint box
            g.drawRect(bx+hintW, by, viewW, hintH);   //top hint box
            g.drawRect(bx+hintW, by+hintH, viewW, viewH); //main image box
            label.setText("vx: " + vx + " , vy: " + vy + ", realVx: " + realVx + ", realVy: " + realVy);
        }
    }
}  

