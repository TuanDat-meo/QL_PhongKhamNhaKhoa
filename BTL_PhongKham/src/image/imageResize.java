package image;

import javax.swing.*;
import java.awt.*;

public class imageResize {
    public static ImageIcon resizeImageIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage(); // Lấy ảnh gốc
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH); // Resize ảnh
        return new ImageIcon(resizedImg); // Tạo ImageIcon mới
    }
}
