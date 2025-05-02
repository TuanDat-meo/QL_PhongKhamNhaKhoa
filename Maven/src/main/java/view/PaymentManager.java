package view;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
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
        tableModel = new DefaultTableModel(new Object[]{"Mã SP", "Tên SP", "Số lượng", "Đơn giá", "Mã BHYT", "Mức BHYT", "Giảm giá", "Thành tiền", "Tổng tiền"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTotal.add(new JLabel("Tổng tiền:"));
        panelTotal.add(txtTotal);
        add(panelTotal, BorderLayout.SOUTH);
        JPanel panelButtons = new JPanel();
        btnThem = new JButton("Thêm");
        btnPay = new JButton("Thanh toán");
        panelButtons.add(btnThem);
        panelButtons.add(btnPay);
        add(panelButtons, BorderLayout.SOUTH);
        btnThem.addActionListener(e -> themSanPham());
        btnPay.addActionListener(e -> processPayment());
    }
    private void themSanPham() {
        try {
            String maSP = txtMaSP.getText();
            String tenSP = txtTenSP.getText();
            int soLuong = Integer.parseInt(txtSoLuong.getText().replace(",", ""));
            double donGia = Double.parseDouble(txtDonGia.getText().replace(",", ""));
            String maBHYT = txtMaBHYT.getText();
            int mucBHYT = Integer.parseInt((String) cbxBHYT.getSelectedItem());
            double giamGia = tinhGiamGia(mucBHYT);
            double thanhTien = soLuong * donGia * (1 - giamGia);
            tableModel.addRow(new Object[]{maSP, tenSP, soLuong, donGia, maBHYT, mucBHYT, (giamGia * 100) + "%", thanhTien, formatter.format(tongTien + thanhTien)});
            tongTien += thanhTien;
            txtTotal.setText(formatter.format(tongTien));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void processPayment() {
        String method = (String) cmbPaymentMethod.getSelectedItem();
        if (method.equals("Chuyển khoản") || method.equals("Ví điện tử")) {
            showQRDialog(tongTien);
        } else if (method.equals("POS")) {
            showPOSTransaction();
        } else {
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            resetData();
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
        Timer timer = new Timer(200, e -> {
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
}