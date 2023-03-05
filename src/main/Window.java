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
            int imgW = image.getWidth();               
            int imgH = image.getHeight();
            int vccMax = (int)(scale * Math.max(imgW, imgH));   //maximum view cell count
            int rcx = Math.round((float)vx / viewW * vccMax);   //real cell x
            int rcy = Math.round((float)vy / viewH * vccMax);   //real cell y
            float ratioX = Math.min(1, (float)imgW / imgH / scale); //how much of the view the img
            float ratioY = Math.min(1, (float)imgH / imgW / scale); //can take up at this scale
            int bhx = bx + hintW;                       //base hint x
            int bhy = by + hintH;                       //base hint y
            int rsx = (int)((0.5f - ratioX/2) * viewW); //real start x
            int rex = (int)((0.5f + ratioX/2) * viewW); //real end x
            int riW = Math.min(-rcx+vccMax, imgW);      //real image width
            
            int rsy = (int)((0.5f - ratioY/2) * viewH); //real start y
            int rey = (int)((0.5f + ratioY/2) * viewH); //real end y
            int riH = Math.min(-rcy+vccMax, imgH);      //real image height

            //          image,  dst x0,      dst y0,    dst x1,     dst y1,     src x0,     src y0,     src x1,         src y1,             bgcolor,       contentpane
            g.drawImage(thi,    bhx+rsx,     by,        bhx+rex,    bhy,        -rcx,       0,          riW,            thi.getHeight(),    Color.magenta, getContentPane()); 
            g.drawImage(lhi,    bx,          bhy+rsy,   bhx,        bhy+rey,    0,          -rcy,       lhi.getWidth(), riH,                Color.magenta, getContentPane()); 
            g.drawImage(image,  bhx+rsx,     bhy+rsy,   bhx+rex,    bhy+rey,    -rcx,       -rcy,       riW,            riH,                Color.magenta, getContentPane());
            
            g.drawRect(bx, bhy, hintW, viewH);   //left hint box
            g.drawRect(bhx, by, viewW, hintH);   //top hint box
            g.drawRect(bhx, bhy, viewW, viewH); //main image box
        }
    }
}  

