package util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class RoundedPanel extends JPanel {
    private int cornerRadius;
    private boolean shadowEnabled;
    public RoundedPanel(int radius, boolean shadow) {
        super();
        this.cornerRadius = radius;
        this.shadowEnabled = shadow;
        setOpaque(false);
    }
    public RoundedPanel(int radius, LayoutManager layout) {
        super(layout);
        this.cornerRadius = radius;
        setOpaque(false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw shadow if enabled
        if (shadowEnabled) {
            for (int i = 0; i < 4; i++) {
                g2d.setColor(new Color(0, 0, 0, 10 - i * 2));
                g2d.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, cornerRadius, cornerRadius);
            }
        }
        
        g2d.setColor(getBackground());
        g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, cornerRadius, cornerRadius);
        g2d.dispose();
    }
}
