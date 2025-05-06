package util;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedButtonUI extends BasicButtonUI {
    private int cornerRadius;

    public RoundedButtonUI(int radius) {
        this.cornerRadius = radius;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AbstractButton button = (AbstractButton) c;
        ButtonModel model = button.getModel();

        // Paint background
        g2.setColor(button.getBackground());
        
        // Check if button is pressed
        if (model.isPressed()) {
            g2.setColor(button.getBackground().darker());
        }
        
        // Draw rounded rectangle
        g2.fill(new RoundRectangle2D.Double(0, 0, c.getWidth(), c.getHeight(), cornerRadius, cornerRadius));

        // Draw border if border painted
        if (button.isBorderPainted()) {
            g2.setColor(button.getForeground().darker());
            g2.draw(new RoundRectangle2D.Double(0, 0, c.getWidth() - 1, c.getHeight() - 1, cornerRadius, cornerRadius));
        }

        // Paint the text and icon
        super.paint(g2, c);
        g2.dispose();
    }

    @Override
    protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        // Center text
        AbstractButton button = (AbstractButton) c;
        ButtonModel model = button.getModel();
        FontMetrics fm = g.getFontMetrics();
        
        int mnemonicIndex = button.getDisplayedMnemonicIndex();
        
        if (model.isEnabled()) {
            g.setColor(button.getForeground());
            g.drawString(text, textRect.x, textRect.y + fm.getAscent());
        } else {
            g.setColor(button.getForeground().brighter());
            g.drawString(text, textRect.x, textRect.y + fm.getAscent());
        }
    }
}