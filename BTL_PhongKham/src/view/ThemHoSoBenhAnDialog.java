// view/ThemHoSoBenhAnDialog.java
package view;

import controller.HoSoBenhAnController;
import controller.BenhNhanController;
import model.HoSoBenhAn;
import model.BenhNhan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ThemHoSoBenhAnDialog extends JDialog {
    private HoSoBenhAnController hoSoBenhAnController;
    private BenhNhanController benhNhanController;
    private Map<String, Integer> tenBenhNhanToId;
    private HoSoBenhAnUI hoSoBenhAnUI;

    private JComboBox<String> cmbTenBenhNhan;
    private JTextField txtChuanDoan;
    private JTextArea txtGhiChu;
    private JTextField txtNgayTao;
    private JComboBox<String> cmbTrangThai;

    public ThemHoSoBenhAnDialog(JFrame owner, String title, boolean modal,
                                 HoSoBenhAnController hoSoBenhAnController,
                                 BenhNhanController benhNhanController,
                                 Map<String, Integer> tenBenhNhanToId,
                                 HoSoBenhAnUI hoSoBenhAnUI) {
        super(owner, title, modal);
        this.hoSoBenhAnController = hoSoBenhAnController;
        this.benhNhanController = benhNhanController;
        this.tenBenhNhanToId = tenBenhNhanToId;
        this.hoSoBenhAnUI = hoSoBenhAnUI;

        initComponents();
        loadDanhSachBenhNhanVaoComboBox();
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(new Dimension(400, 300));
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        JPanel contentPane = new JPanel(new GridLayout(6, 2, 5, 5));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblTenBenhNhan = new JLabel("Tên Bệnh nhân:");
        cmbTenBenhNhan = new JComboBox<>();
        JLabel lblChuanDoan = new JLabel("Chuẩn đoán:");
        txtChuanDoan = new JTextField(50);
        JLabel lblGhiChu = new JLabel("Ghi chú:");
        txtGhiChu = new JTextArea(3, 50);
        JScrollPane ghiChuScrollPane = new JScrollPane(txtGhiChu);
        JLabel lblNgayTao = new JLabel("Ngày tạo (yyyy-MM-dd):");
        txtNgayTao = new JTextField(formatDate(new Date()), 15);
        JLabel lblTrangThai = new JLabel("Trạng thái:");
        cmbTrangThai = new JComboBox<>(new String[]{"Mới", "Đang điều trị", "Hoàn tất"});

        contentPane.add(lblTenBenhNhan);
        contentPane.add(cmbTenBenhNhan);
        contentPane.add(lblChuanDoan);
        contentPane.add(txtChuanDoan);
        contentPane.add(lblGhiChu);
        contentPane.add(ghiChuScrollPane);
        contentPane.add(lblNgayTao);
        contentPane.add(txtNgayTao);
        contentPane.add(lblTrangThai);
        contentPane.add(cmbTrangThai);

        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnThem = new JButton("Thêm");
        btnThem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                themHoSoBenhAn();
            }
        });
        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        buttonPane.add(btnThem);
        buttonPane.add(btnHuy);

        setLayout(new BorderLayout());
        add(contentPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.SOUTH);
    }

    public void setCmbTenBenhNhanModel(DefaultComboBoxModel<String> model) {
        this.cmbTenBenhNhan.setModel(model);
        this.cmbTenBenhNhan.setSelectedIndex(-1);
    }

    private void loadDanhSachBenhNhanVaoComboBox() {
        try {
            java.util.List<BenhNhan> danhSachBenhNhan = benhNhanController.layDanhSachBenhNhan();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            for (BenhNhan bn : danhSachBenhNhan) {
                model.addElement(bn.getHoTen());
            }
            cmbTenBenhNhan.setModel(model);
            cmbTenBenhNhan.setSelectedIndex(-1);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách bệnh nhân: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
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

    private void themHoSoBenhAn() {
        String tenBenhNhan = (String) cmbTenBenhNhan.getSelectedItem();
        if (tenBenhNhan == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tên bệnh nhân.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Integer idBenhNhan = tenBenhNhanToId.get(tenBenhNhan);
        if (idBenhNhan == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy ID bệnh nhân.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String chuanDoan = txtChuanDoan.getText();
        String ghiChu = txtGhiChu.getText();
        Date ngayTao = parseDate(txtNgayTao.getText());
        String trangThai = (String) cmbTrangThai.getSelectedItem();

        if (ngayTao == null) return;

        HoSoBenhAn hoSoBenhAn = new HoSoBenhAn(idBenhNhan, chuanDoan, ghiChu, ngayTao, trangThai);
        hoSoBenhAnController.themHoSoBenhAn(hoSoBenhAn);
        hoSoBenhAnUI.lamMoiDanhSach();
        setVisible(false);
        clearFields();
        JOptionPane.showMessageDialog(hoSoBenhAnUI, "Thêm hồ sơ bệnh án thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearFields() {
        cmbTenBenhNhan.setSelectedIndex(-1);
        txtChuanDoan.setText("");
        txtGhiChu.setText("");
        txtNgayTao.setText(formatDate(new Date()));
        cmbTrangThai.setSelectedIndex(0);
    }
}