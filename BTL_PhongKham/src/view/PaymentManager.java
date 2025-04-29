package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URI;
import java.text.DecimalFormat;

@SuppressWarnings("unused")
public class PaymentManager extends JFrame {
    private JTextField txtTotal, txtMaSP, txtTenSP, txtSoLuong, txtDonGia, txtMaBHYT;
    private JComboBox<String> cmbPaymentMethod, cbxBHYT;
    private JButton btnPay, btnThem;
    private JLabel lblQR;
    private JDialog qrDialog;
    private JTable table;
    private DefaultTableModel tableModel;
    private double tongTien = 0;
    private final DecimalFormat formatter = new DecimalFormat("#,### VNĐ");

    public PaymentManager() {
        setTitle("Quản Lý Thanh Toán");
        setSize(800, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel nhập liệu
        JPanel panelInput = new JPanel(new GridLayout(8, 2, 5, 5));
        panelInput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtMaSP = new JTextField();
        txtTenSP = new JTextField();
        txtSoLuong = new JTextField();
        txtDonGia = new JTextField();
        txtMaBHYT = new JTextField();
        txtTotal = new JTextField(10);
        txtTotal.setEditable(false);

        String[] bhytOptions = {"0", "1", "2", "3", "4", "5"};
        cbxBHYT = new JComboBox<>(bhytOptions);
        String[] paymentMethods = {"Tiền mặt", "Chuyển khoản", "Ví điện tử", "POS"};
        cmbPaymentMethod = new JComboBox<>(paymentMethods);

        panelInput.add(new JLabel("Mã SP:"));
        panelInput.add(txtMaSP);
        panelInput.add(new JLabel("Tên SP:"));
        panelInput.add(txtTenSP);
        panelInput.add(new JLabel("Số lượng:"));
        panelInput.add(txtSoLuong);
        panelInput.add(new JLabel("Đơn giá:"));
        panelInput.add(txtDonGia);
        panelInput.add(new JLabel("Mã BHYT:"));
        panelInput.add(txtMaBHYT);
        panelInput.add(new JLabel("Mức BHYT:"));
        panelInput.add(cbxBHYT);
        panelInput.add(new JLabel("Phương thức thanh toán:"));
        panelInput.add(cmbPaymentMethod);
        add(panelInput, BorderLayout.NORTH);

        // Bảng sản phẩm
        tableModel = new DefaultTableModel(new Object[]{"Mã SP", "Tên SP", "Số lượng", "Đơn giá", "Mã BHYT", "Mức BHYT", "Giảm giá", "Thành tiền", "Tổng tiền"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel tổng + nút
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTotal.add(new JLabel("Tổng tiền:"));
        panelTotal.add(txtTotal);

        JPanel panelButtons = new JPanel();
        btnThem = new JButton("Thêm");
        btnPay = new JButton("Thanh toán");
        panelButtons.add(btnThem);
        panelButtons.add(btnPay);

        // Gộp lại thành 1 panel
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(panelTotal, BorderLayout.NORTH);
        southPanel.add(panelButtons, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // Sự kiện
        btnThem.addActionListener(e -> themSanPham());
        btnPay.addActionListener(e -> processPayment());
    }

    private void themSanPham() {
        try {
            String maSP = txtMaSP.getText().trim();
            String tenSP = txtTenSP.getText().trim();
            String soLuongStr = txtSoLuong.getText().replaceAll("[^\\d]", "");
            String donGiaStr = txtDonGia.getText().replaceAll("[^\\d]", "");
            String maBHYT = txtMaBHYT.getText().trim();

            if (maSP.isEmpty() || tenSP.isEmpty() || soLuongStr.isEmpty() || donGiaStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin sản phẩm!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int soLuong = Integer.parseInt(soLuongStr);
            double donGia = Double.parseDouble(donGiaStr);
            int mucBHYT = Integer.parseInt((String) cbxBHYT.getSelectedItem());
            double giamGia = tinhGiamGia(mucBHYT);
            double thanhTien = soLuong * donGia * (1 - giamGia);

            tongTien += thanhTien;

            tableModel.addRow(new Object[]{
                maSP,
                tenSP,
                soLuong,
                formatter.format(donGia),
                maBHYT,
                mucBHYT,
                (int)(giamGia * 100) + "%",
                formatter.format(thanhTien),
                formatter.format(tongTien)
            });

            txtTotal.setText(formatter.format(tongTien));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processPayment() {
        // Kiểm tra xem các trường có được điền đầy đủ không
        if (isInputValid()) {
            String method = (String) cmbPaymentMethod.getSelectedItem();
            if (method.equals("Chuyển khoản")) {
                showTransferImage();  // Hiển thị ảnh chuyển khoản
            } else if (method.equals("Ví điện tử")) {
                openPaymentPage();  // Mở trang thanh toán ví điện tử
            } else if (method.equals("POS")) {
                showPOSTransaction(); // Chuyển đến máy POS
            } else {
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                resetData();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.WARNING_MESSAGE);
        }
    }
    private boolean isInputValid() {
        // Kiểm tra các trường cần thiết
        if (txtMaSP.getText().trim().isEmpty() ||
            txtTenSP.getText().trim().isEmpty() ||
            txtSoLuong.getText().trim().isEmpty() ||
            txtDonGia.getText().trim().isEmpty()) {
            return false;  // Nếu bất kỳ trường nào chưa nhập thì trả về false
        }

        // Nếu tất cả các trường đều hợp lệ, trả về true
        return true;
    }

    private void showTransferImage() {
        // Tạo cửa sổ JDialog để hiển thị ảnh
        JDialog transferDialog = new JDialog(this, "Chuyển Khoản", true);
        transferDialog.setSize(400, 400);
        transferDialog.setLayout(new BorderLayout());
        ImageIcon transferImage = new ImageIcon("https://qrcode.io.vn/api/generate/970422/023334639999?is_mask=1");  // Đường dẫn đến ảnh
        JLabel imageLabel = new JLabel(transferImage, SwingConstants.CENTER);
        transferDialog.add(imageLabel, BorderLayout.CENTER);

        // Thêm nút xác nhận hoặc đóng cửa sổ
        JButton btnClose = new JButton("Đã chuyển khoản");
        btnClose.addActionListener(e -> {
            transferDialog.dispose();  // Đóng cửa sổ khi người dùng xác nhận
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            resetData();  // Reset dữ liệu sau khi thanh toán thành công
        });
        transferDialog.add(btnClose, BorderLayout.SOUTH);

        transferDialog.setVisible(true);  // Hiển thị cửa sổ
    }

    private void openPaymentPage() {
        try {
            // URL của trang thanh toán ví điện tử
            String paymentUrl = "https://www.momo.vn/payment?amount=" + tongTien;
            
            // Mở trang web trong trình duyệt mặc định
            Desktop desktop = Desktop.getDesktop();
            URI uri = new URI(paymentUrl);
            desktop.browse(uri);
            
            // Thông báo người dùng rằng đang mở trang thanh toán
            JOptionPane.showMessageDialog(this, "Đang chuyển đến trang thanh toán ví điện tử...", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không thể mở trang thanh toán: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void showQRDialog(double amount) {
        qrDialog = new JDialog(this, "Quét mã QR để thanh toán", true);
        qrDialog.setSize(300, 300);
        qrDialog.setLayout(new BorderLayout());

        lblQR = new JLabel("QR Code: " + formatter.format(amount), SwingConstants.CENTER);
        qrDialog.add(lblQR, BorderLayout.CENTER);

        JButton btnConfirm = new JButton("Xác nhận thanh toán");
        btnConfirm.addActionListener(e -> {
            qrDialog.dispose();
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            resetData();
        });

        qrDialog.add(btnConfirm, BorderLayout.SOUTH);
        qrDialog.setVisible(true);
    }

    private void showPOSTransaction() {
        JOptionPane.showMessageDialog(this, "Đang chuyển đến máy POS...", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        Timer timer = new Timer(2000, e -> {
            JOptionPane.showMessageDialog(this, "Đã thanh toán thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            resetData();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private double tinhGiamGia(int mucBHYT) {
        switch (mucBHYT) {
            case 1: return 0.3;
            case 2: return 0.4;
            case 3: return 0.5;
            case 4: return 0.8;
            case 5: return 1.0;
            default: return 0.0;
        }
    }

    private void resetData() {
        tableModel.setRowCount(0);
        tongTien = 0;
        txtTotal.setText("0 VNĐ");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PaymentManager().setVisible(true));
    }
}
