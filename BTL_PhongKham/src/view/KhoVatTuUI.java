package view;

import javax.swing.*;

import controller.KhoVatTuController;

import java.awt.*;
import java.util.List;

public class KhoVatTuUI extends JPanel {
    private KhoVatTuController khoVatTu;
    private JTextArea textArea;
    private JTextField txtTen, txtSoLuong, txtDonVi, txtIdNCC, txtIdVatTu;

    public KhoVatTuUI() {
        khoVatTu = new KhoVatTuController();
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Tên vật tư:"));
        txtTen = new JTextField();
        panel.add(txtTen);

        panel.add(new JLabel("Số lượng:"));
        txtSoLuong = new JTextField();
        panel.add(txtSoLuong);

        panel.add(new JLabel("Đơn vị tính:"));
        txtDonVi = new JTextField();
        panel.add(txtDonVi);

        panel.add(new JLabel("ID Nhà cung cấp:"));
        txtIdNCC = new JTextField();
        panel.add(txtIdNCC);

        panel.add(new JLabel("ID vật tư (cập nhật/xóa):"));
        txtIdVatTu = new JTextField();
        panel.add(txtIdVatTu);

        add(panel, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel();
        JButton btnThem = new JButton("Thêm");
        JButton btnCapNhat = new JButton("Cập nhật");
        JButton btnXoa = new JButton("Xóa");
        JButton btnHienThi = new JButton("Hiển thị");

        btnThem.addActionListener(e -> themVatTu());
        btnCapNhat.addActionListener(e -> capNhatVatTu());
        btnXoa.addActionListener(e -> xoaVatTu());
        btnHienThi.addActionListener(e -> hienThiDanhSach());

        btnPanel.add(btnThem);
        btnPanel.add(btnCapNhat);
        btnPanel.add(btnXoa);
        btnPanel.add(btnHienThi);

        add(btnPanel, BorderLayout.SOUTH);
    }

    private void themVatTu() {
        try {
            String ten = txtTen.getText();
            int soLuong = Integer.parseInt(txtSoLuong.getText());
            String donVi = txtDonVi.getText();
            int idNCC = Integer.parseInt(txtIdNCC.getText());

            if (khoVatTu.themVatTu(ten, soLuong, donVi, idNCC)) {
                JOptionPane.showMessageDialog(this, "Thêm vật tư thành công");
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập dữ liệu hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhatVatTu() {
        try {
            int idVatTu = Integer.parseInt(txtIdVatTu.getText());
            String ten = txtTen.getText();
            int soLuong = Integer.parseInt(txtSoLuong.getText());
            String donVi = txtDonVi.getText();
            int idNCC = Integer.parseInt(txtIdNCC.getText());

            if (khoVatTu.capNhatVatTu(idVatTu, ten, soLuong, donVi, idNCC)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công");
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập dữ liệu hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaVatTu() {
        try {
            int idVatTu = Integer.parseInt(txtIdVatTu.getText());
            if (khoVatTu.xoaVatTu(idVatTu)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công");
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ID hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hienThiDanhSach() {
        List<String> danhSach = khoVatTu.layDanhSachVatTu();
        textArea.setText(String.join("\n", danhSach));
    }
}
