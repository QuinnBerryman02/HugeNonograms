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
    int vx=bx,vy=by;
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
        g.drawImage(lhi, bx, by+hintH, bx+hintW, by+hintH+viewH, 0, 0, lhi.getWidth(), vccH, Color.magenta, getContentPane());
        g.drawImage(thi, bx+hintW, by, bx+hintW+viewW, by+hintH, 0, 0, vccW, thi.getHeight(), Color.magenta, getContentPane());
        g.drawImage(image, vx+hintW, vy+hintH, vx+hintW+viewW, vy+hintH+viewH, 0, 0, vccW, vccH, Color.magenta, getContentPane());
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
        repaint();
    }
}  