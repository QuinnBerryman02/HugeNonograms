package src.main;

import javax.swing.*;

import src.main.Nonogram.Hints;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Window extends JFrame implements MouseListener, MouseMotionListener, MouseWheelListener {  
    final static int CELL_SIZE = 10;

    Nonogram nonogram;
    int bx=50,by=50;
    int mx,my;
    float scale = 1;

    public Window(Nonogram nono) {
        super("Nonogram Program");
        nonogram = nono;
        Hints hints = nonogram.getHints();
        bx = ((int)(-hints.maxW * CELL_SIZE * scale) + bx);
        by = ((int)(-hints.maxH * CELL_SIZE * scale) + by);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000,1000);
        setLayout(null);
        setVisible(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Hints hints = nonogram.getHints();
        g.setFont(new Font("my font",Font.ITALIC,CELL_SIZE));
        int w = (int)(nonogram.W * CELL_SIZE * scale);
        int h = (int)(nonogram.H * CELL_SIZE * scale);
        int hw = (int)(hints.maxW * CELL_SIZE * scale);
        int hh = (int)(hints.maxH * CELL_SIZE * scale);
        BufferedImage lhi = nonogram.leftHintImage;
        BufferedImage thi = nonogram.topHintImage;
        BufferedImage image = nonogram.image; 
        // image, dst rect, src rect, bgcolor, contentpane
        g.drawImage(lhi, bx, by+hh, bx+hw, by+hh+h, 0, 0, lhi.getWidth(), lhi.getHeight(), Color.magenta, getContentPane());
        g.drawImage(thi, bx+hw, by, bx+hw+w, by+hh, 0, 0, thi.getWidth(), thi.getHeight(), Color.magenta, getContentPane());
        g.drawImage(image, bx+hw, by+hh, bx+hw+w, by+hh+h, 0, 0, image.getWidth(), image.getHeight(), Color.magenta, getContentPane());
        g.drawRect(bx, by+hh, hw, h);   //left hint box
        g.drawRect(bx+hw, by, w, hh);   //top hint box
        g.drawRect(bx+hw, by+hh, w, h); //main image box
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
        bx += dx;
        by += dy;
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
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(e.getPreciseWheelRotation() < 0) scale*=1.05;
        if(e.getPreciseWheelRotation() > 0) scale*=0.95;
        repaint();
    }
}  