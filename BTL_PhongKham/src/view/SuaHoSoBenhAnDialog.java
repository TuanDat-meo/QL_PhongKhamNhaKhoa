// view/SuaHoSoBenhAnDialog.java
package view;

import controller.HoSoBenhAnController;
import controller.BenhNhanController;
import model.HoSoBenhAn;
import model.BenhNhan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class SuaHoSoBenhAnDialog extends JDialog {
    private HoSoBenhAnController hoSoBenhAnController;
    private BenhNhanController benhNhanController;
    private Map<String, Integer> tenBenhNhanToId;
    private HoSoBenhAnUI hoSoBenhAnUI;
    private int idHoSoCanSua;

    private JComboBox<String> cmbTenBenhNhanSua;
    private JTextField txtChuanDoanSua;
    private JTextArea txtGhiChuSua;
    private JScrollPane ghiChuScrollPaneSua;
    private JTextField txtNgayTaoSua;
    private JComboBox<String> cmbTrangThaiSua;

    public SuaHoSoBenhAnDialog(JFrame owner, String title, boolean modal,
                               HoSoBenhAnController hoSoBenhAnController,
                               BenhNhanController benhNhanController,
                               Map<String, Integer> tenBenhNhanToId,
                               HoSoBenhAnUI hoSoBenhAnUI,
                               int idHoSoCanSua,
                               String tenBenhNhanHienTai,
                               String chuanDoanHienTai,
                               String ghiChuHienTai,
                               Date ngayTaoHienTai,
                               String trangThaiHienTai) {
        super(owner, title, modal);
        this.hoSoBenhAnController = hoSoBenhAnController;
        this.benhNhanController = benhNhanController;
        this.tenBenhNhanToId = tenBenhNhanToId;
        this.hoSoBenhAnUI = hoSoBenhAnUI;
        this.idHoSoCanSua = idHoSoCanSua;

        setLayout(new GridLayout(7, 2, 5, 5));
        

        cmbTenBenhNhanSua = new JComboBox<>(tenBenhNhanToId.keySet().toArray(new String[0]));
        cmbTenBenhNhanSua.setSelectedItem(tenBenhNhanHienTai);
        txtChuanDoanSua = new JTextField(chuanDoanHienTai, 50);
        txtGhiChuSua = new JTextArea(ghiChuHienTai, 3, 50);
        ghiChuScrollPaneSua = new JScrollPane(txtGhiChuSua);
        txtNgayTaoSua = new JTextField(formatDate(ngayTaoHienTai), 15);
        cmbTrangThaiSua = new JComboBox<>(new String[]{"Mới", "Đang điều trị", "Hoàn tất"});
        cmbTrangThaiSua.setSelectedItem(trangThaiHienTai);

        add(new JLabel("Tên Bệnh nhân:"));
        add(cmbTenBenhNhanSua);
        add(new JLabel("Chuẩn đoán:"));
        add(txtChuanDoanSua);
        add(new JLabel("Ghi chú:"));
        add(ghiChuScrollPaneSua);
        add(new JLabel("Ngày tạo (yyyy-MM-dd):"));
        add(txtNgayTaoSua);
        add(new JLabel("Trạng thái:"));
        add(cmbTrangThaiSua);
        add(new JLabel("")); // Placeholder
        JButton btnLuuSua = new JButton("Lưu");
        btnLuuSua.addActionListener(e -> luuThayDoi());
        add(btnLuuSua);

        pack();
        setLocationRelativeTo(owner);
    }

    private String formatDate(Date date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(date);
        }
        return "";
    }

    private Date parseDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ (yyyy-MM-dd).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void luuThayDoi() {
        String tenBenhNhanMoi = (String) cmbTenBenhNhanSua.getSelectedItem();
        Integer idBenhNhanMoi = tenBenhNhanToId.get(tenBenhNhanMoi);
        if (idBenhNhanMoi == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy ID bệnh nhân.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String chuanDoanMoi = txtChuanDoanSua.getText();
        String ghiChuMoi = txtGhiChuSua.getText();
        Date ngayTaoMoi = parseDate(txtNgayTaoSua.getText());
        String trangThaiMoi = (String) cmbTrangThaiSua.getSelectedItem();

        if (ngayTaoMoi == null) return;

        HoSoBenhAn hoSoBenhAnDaSua = new HoSoBenhAn(idBenhNhanMoi, chuanDoanMoi, ghiChuMoi, ngayTaoMoi, trangThaiMoi);
        hoSoBenhAnDaSua.setIdHoSo(idHoSoCanSua);
        hoSoBenhAnController.suaHoSoBenhAn(hoSoBenhAnDaSua);
        hoSoBenhAnUI.lamMoiDanhSach();
        dispose();
        JOptionPane.showMessageDialog(this, "Sửa hồ sơ bệnh án thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
}