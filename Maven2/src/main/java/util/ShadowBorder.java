package util;
import javax.swing.border.Border;
import java.awt.*;

public class ShadowBorder implements Border {
    private int shadowSize = 5;
    private Color shadowColor = new Color(0, 0, 0, 50);
    private int shadowOpacity = 30;
    private int cornerRadius = 12;

    public ShadowBorder() {
    }

    public ShadowBorder(int shadowSize) {
        this.shadowSize = shadowSize;
    }

    public ShadowBorder(int shadowSize, int cornerRadius) {
        this.shadowSize = shadowSize;
        this.cornerRadius = cornerRadius;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Create gradual shadow
        for (int i = 0; i < shadowSize; i++) {
            int alpha = shadowOpacity * (shadowSize - i) / shadowSize;
            g2.setColor(new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), alpha));
            
            // Draw rounded rectangle with shadow
            g2.drawRoundRect(
                x + i, 
                y + i, 
                width - (i * 2) - 1, 
                height - (i * 2) - 1, 
                cornerRadius, 
                cornerRadius);
        }

        // Draw the actual border (white background)
        g2.setColor(c.getBackground());
        g2.fillRoundRect(
            x + shadowSize, 
            y + shadowSize, 
            width - (shadowSize * 2), 
            height - (shadowSize * 2), 
            cornerRadius, 
            cornerRadius);
            
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(shadowSize, shadowSize, shadowSize * 2, shadowSize * 2);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
