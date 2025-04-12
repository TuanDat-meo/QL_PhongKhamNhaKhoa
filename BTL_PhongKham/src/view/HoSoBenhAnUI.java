package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import controller.DieuTriController;
import controller.HoSoBenhAnController;
import model.DieuTri;
import model.HoSoBenhAn;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;
public class HoSoBenhAnUI extends JFrame {
    private JTable hoSoTable;
    private DefaultTableModel tableModel;
    private HoSoBenhAnController qlHoSoBenhAn;
    private DieuTriController qlDieuTri;
    private JTextField searchField;

    public HoSoBenhAnUI() {
        this.qlHoSoBenhAn = new HoSoBenhAnController();
        this.qlDieuTri = new DieuTriController();
        setTitle("Hồ Sơ Bệnh Án");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel();
        hoSoTable = new JTable(tableModel);
        tableModel.addColumn("ID Hồ Sơ");
        tableModel.addColumn("ID Bệnh Nhân");
        tableModel.addColumn("Chẩn Đoán");
        tableModel.addColumn("Ghi Chú");
        tableModel.addColumn("Ngày Tạo");
        tableModel.addColumn("Trạng Thái");

        JScrollPane scrollPane = new JScrollPane(hoSoTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Thêm");
        JButton deleteButton = new JButton("Xóa");
        JButton editButton = new JButton("Sửa");
        searchField = new JTextField(20);

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(new JLabel("Tìm kiếm:"));
        buttonPanel.add(searchField);

        add(buttonPanel, BorderLayout.NORTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddDialog();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteHoSo();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEditDialog();
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterData(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterData(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterData(searchField.getText());
            }
        });

        hoSoTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = hoSoTable.getSelectedRow();
                int col = hoSoTable.getSelectedColumn();
                if (row >= 0 && col == 5) { // Kiểm tra nếu cột được nhấn là "Trạng Thái" (cột thứ 5)
                    int idHoSo = (int) tableModel.getValueAt(row, 0);
                    showDieuTri(idHoSo);
                }
            }
        });
    }

    public void loadHoSoBenhAn() {
        try {
            List<HoSoBenhAn> hoSoList = qlHoSoBenhAn.getAllHoSoBenhAn();
            tableModel.setRowCount(0);
            for (HoSoBenhAn hoSo : hoSoList) {
                tableModel.addRow(new Object[]{
                    hoSo.getIdHoSo(),
                    hoSo.getIdBenhNhan(),
                    hoSo.getChuanDoan(),
                    hoSo.getGhiChu(),
                    hoSo.getNgayTao(),
                    hoSo.getTrangThai()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void filterData(String searchText) {
        try {
            List<HoSoBenhAn> hoSoList = qlHoSoBenhAn.searchHoSoBenhAn(searchText);
            tableModel.setRowCount(0);
            for (HoSoBenhAn hoSo : hoSoList) {
                tableModel.addRow(new Object[]{
                    hoSo.getIdHoSo(),
                    hoSo.getIdBenhNhan(),
                    hoSo.getChuanDoan(),
                    hoSo.getGhiChu(),
                    hoSo.getNgayTao(),
                    hoSo.getTrangThai()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lọc dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showDieuTri(int idHoSo) {
        try {
            List<DieuTri> dieuTriList = qlDieuTri.getDieuTriByHoSoId(idHoSo);
            if (dieuTriList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có thông tin điều trị cho hồ sơ này.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JDialog dialog = new JDialog(this, "Thông tin điều trị", true);
                dialog.setLayout(new BorderLayout());

                JTextArea textArea = new JTextArea();
                textArea.setEditable(false);
                for (DieuTri dieuTri : dieuTriList) {
                    textArea.append("ID Điều Trị: " + dieuTri.getIdDieuTri() + "\n");
                    textArea.append("ID Bác Sĩ: " + dieuTri.getIdBacSi() + "\n");
                    textArea.append("Mô Tả: " + dieuTri.getMoTa() + "\n");
                    textArea.append("Ngày Điều Trị: " + dieuTri.getNgayDieuTri() + "\n\n");
                }
                dialog.add(new JScrollPane(textArea), BorderLayout.CENTER);

                dialog.setSize(400, 300);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu điều trị: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    private void showAddDialog() {
        JDialog dialog = new JDialog(this, "Thêm Hồ Sơ Bệnh Án", true);
        dialog.setLayout(new GridLayout(7, 2));

        JTextField idBenhNhanField = new JTextField();
        JTextField chuanDoanField = new JTextField();
        JTextArea ghiChuArea = new JTextArea();
        JTextField ngayTaoField = new JTextField();
        JTextField trangThaiField = new JTextField();

        dialog.add(new JLabel("ID Bệnh Nhân:"));
        dialog.add(idBenhNhanField);
        dialog.add(new JLabel("Chẩn Đoán:"));
        dialog.add(chuanDoanField);
        dialog.add(new JLabel("Ghi Chú:"));
        dialog.add(new JScrollPane(ghiChuArea));
        dialog.add(new JLabel("Ngày Tạo (YYYY-MM-DD):"));
        dialog.add(ngayTaoField);
        dialog.add(new JLabel("Trạng Thái:"));
        dialog.add(trangThaiField);

        JButton saveButton = new JButton("Lưu");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    HoSoBenhAn hoSo = new HoSoBenhAn();
                    hoSo.setIdBenhNhan(Integer.parseInt(idBenhNhanField.getText()));
                    hoSo.setChuanDoan(chuanDoanField.getText());
                    hoSo.setGhiChu(ghiChuArea.getText());
                    hoSo.setNgayTao(Date.valueOf(ngayTaoField.getText()));
                    hoSo.setTrangThai(trangThaiField.getText());

                    if (qlHoSoBenhAn.addHoSoBenhAn(hoSo)) {
                        loadHoSoBenhAn();
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(HoSoBenhAnUI.this, "Thêm hồ sơ bệnh án thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException | IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(HoSoBenhAnUI.this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        dialog.add(saveButton);

        JButton cancelButton = new JButton("Hủy");
        cancelButton.addActionListener(e -> dialog.dispose());
        dialog.add(cancelButton);

        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    private void showEditDialog() {
        int selectedRow = hoSoTable.getSelectedRow();
        if (selectedRow >= 0) {
            int idHoSo = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                List<HoSoBenhAn> hoSoList = qlHoSoBenhAn.getHoSoBenhAnByBenhNhanId((int) tableModel.getValueAt(selectedRow, 1));
                HoSoBenhAn hoSo = hoSoList.stream().filter(h -> h.getIdHoSo() == idHoSo).findFirst().orElse(null);
                if (hoSo != null) {
                    JDialog dialog = new JDialog(this, "Sửa Hồ Sơ Bệnh Án", true);
                    dialog.setLayout(new GridLayout(7, 2));
                    JTextField idBenhNhanField = new JTextField(String.valueOf(hoSo.getIdBenhNhan()));
                    JTextField chuanDoanField = new JTextField(hoSo.getChuanDoan());
                    JTextArea ghiChuArea = new JTextArea(hoSo.getGhiChu());
                    JTextField ngayTaoField = new JTextField(hoSo.getNgayTao().toString());
                    JTextField trangThaiField = new JTextField(hoSo.getTrangThai());
                    dialog.add(new JLabel("ID Bệnh Nhân:"));
                    dialog.add(idBenhNhanField);
                    dialog.add(new JLabel("Chẩn Đoán:"));
                    dialog.add(chuanDoanField);
                    dialog.add(new JLabel("Ghi Chú:"));
                    dialog.add(new JScrollPane(ghiChuArea));
                    dialog.add(new JLabel("Ngày Tạo (YYYY-MM-DD):"));
                    dialog.add(ngayTaoField);
                    dialog.add(new JLabel("Trạng Thái:"));
                    dialog.add(trangThaiField);
                    JButton saveButton = new JButton("Lưu");
                    saveButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                hoSo.setIdBenhNhan(Integer.parseInt(idBenhNhanField.getText()));
                                hoSo.setChuanDoan(chuanDoanField.getText());
                                hoSo.setGhiChu(ghiChuArea.getText());
                                hoSo.setNgayTao(Date.valueOf(ngayTaoField.getText()));
                                hoSo.setTrangThai(trangThaiField.getText());
                                if (qlHoSoBenhAn.updateHoSoBenhAn(hoSo)) {
                                    loadHoSoBenhAn();
                                    dialog.dispose();
                                } else {
                                    JOptionPane.showMessageDialog(HoSoBenhAnUI.this, "Sửa hồ sơ bệnh án thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (SQLException | IllegalArgumentException ex) {
                                JOptionPane.showMessageDialog(HoSoBenhAnUI.this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                                ex.printStackTrace();
                            }
                        }
                    });
                    dialog.add(saveButton);
                    JButton cancelButton = new JButton("Hủy");
                    cancelButton.addActionListener(e -> dialog.dispose());
                    dialog.add(cancelButton);
                    dialog.setSize(400, 300);
                    dialog.setLocationRelativeTo(HoSoBenhAnUI.this);
                    dialog.setVisible(true);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(HoSoBenhAnUI.this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Chọn một hàng để sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void deleteHoSo() {
        int selectedRow = hoSoTable.getSelectedRow();
        if (selectedRow >= 0) {
            int idHoSo = (int) tableModel.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa hồ sơ này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    if (qlHoSoBenhAn.deleteHoSoBenhAn(idHoSo)) {
                        loadHoSoBenhAn();
                    } else {
                        JOptionPane.showMessageDialog(this, "Xóa hồ sơ bệnh án thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Chọn một hàng để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HoSoBenhAnUI ui = new HoSoBenhAnUI();
            ui.setVisible(true);
            ui.loadHoSoBenhAn();

            ui.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    try {
                        ui.qlHoSoBenhAn.closeConnection();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }
}