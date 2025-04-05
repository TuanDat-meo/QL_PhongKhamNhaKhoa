package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import controller.DoanhThuController;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class DoanhThuUI extends JPanel {
    private DoanhThuController qlDoanhThu;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtThang, txtNam;

    public DoanhThuUI() {
        qlDoanhThu = new DoanhThuController();

        setLayout(new BorderLayout());

        // Bảng hiển thị doanh thu
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"Tháng/Năm", "Tổng Doanh Thu"});
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Panel nhập tháng & năm
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        inputPanel.add(new JLabel("Tháng:"));
        txtThang = new JTextField(5);
        inputPanel.add(txtThang);

        inputPanel.add(new JLabel("Năm:"));
        txtNam = new JTextField(5);
        inputPanel.add(txtNam);

        JButton btnCapNhat = new JButton("Cập nhật doanh thu");
        inputPanel.add(btnCapNhat);

        // Sự kiện khi bấm cập nhật
        btnCapNhat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int thang = Integer.parseInt(txtThang.getText());
                    int nam = Integer.parseInt(txtNam.getText());

                    qlDoanhThu.capNhatDoanhThu(thang, nam);
                    loadDoanhThu(); // Cập nhật lại bảng
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(DoanhThuUI.this, "Vui lòng nhập đúng định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Thêm thành phần vào giao diện
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load dữ liệu khi mở giao diện
        loadDoanhThu();
    }

    private void loadDoanhThu() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ

        List<Object[]> doanhThuList = qlDoanhThu.hienThiDoanhThu(); // ĐẢM BẢO TRẢ VỀ DỮ LIỆU
        for (Object[] rowData : doanhThuList) {
            tableModel.addRow(rowData);
        }
    }

}
