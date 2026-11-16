/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package project_qldiem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author LENOVO
 */
public class HeThong extends javax.swing.JPanel {

    /**
     * Creates new form HeThong
     */
    public HeThong() {
        initComponents();
        loadTK();
    }

    private void loadTK(){
        DefaultTableModel m=(DefaultTableModel)tbHeThong.getModel();
        m.setColumnIdentifiers(new Object[]{"Mã TK", "Tên đăng nhập", "Mật khẩu", "Vai trò", "Mã GV", "Kích hoạt"
        });

        String[] cols = {"CAST(ma_tk AS CHAR)", "ten_dn", "mat_khau","vai_tro", "COALESCE(ma_gv,'')", 
            "CAST(kich_hoat AS CHAR)"};

        int idx = Math.max(0, Math.min(btnTimKiem.getSelectedIndex(), cols.length - 1));
        String q = txtTimKiem.getText().trim();

        String sql = "SELECT ma_tk, ten_dn, mat_khau, vai_tro, ma_gv, kich_hoat "
           + "FROM tai_khoan"
           + (q.isEmpty() ? "" : " WHERE " + cols[idx] + " LIKE ?")
           + " ORDER BY ma_tk";

        try (Connection c = KNCSDL.getConnection();
            PreparedStatement p = c.prepareStatement(sql)) {

            if (!q.isEmpty()) {
            p.setString(1, "%" + q + "%");
            }

         try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    m.addRow(new Object[]{
                    r.getInt("ma_tk"),
                    r.getString("ten_dn"),
                      r.getString("mat_khau"),
                        r.getString("vai_tro"),
                       r.getString("ma_gv"),
                       r.getBoolean("kich_hoat") ? "Có" : "Không" });
                }
         }
        } catch (Exception e) {
            showError(e);}
    }
    private void ThemTK(){SqlTK(false);} 
    private void CapNhatTK(){SqlTK(true);}
    private void SqlTK(boolean update){
        try{String username=required(txtTenTK,"Tên tài khoản"),
                password=txtMatKhau.getText(),
                role=required(txtVaiTro,"Vai trò").trim().toUpperCase(java.util.Locale.ROOT),
                teacher=txtMaGV.getText().trim();
            if(!java.util.Arrays.asList("ADMIN","GIAO_VU","GIANG_VIEN").contains(role))
                throw new IllegalArgumentException("Vai trò hợp lệ: ADMIN, GIAO_VU hoặc GIANG_VIEN.");
            if(password.length()<6&&!update)throw new IllegalArgumentException("Mật khẩu cần ít nhất 6 ký tự.");
            if(role.equals("GIANG_VIEN")&&teacher.isEmpty())
                throw new IllegalArgumentException("Tài khoản giảng viên cần có mã giảng viên.");
            if(!role.equals("GIANG_VIEN"))teacher="";
            String sql;
            if(update){
                sql=password.isEmpty()?"UPDATE tai_khoan SET ten_dn=?,vai_tro=?,ma_gv=? "
                        + "WHERE ma_tk=?":"UPDATE tai_khoan SET ten_dn=?,vai_tro=?,ma_gv=?,mat_khau=? WHERE ma_tk=?";}
            else sql="INSERT INTO tai_khoan(ten_dn,mat_khau,vai_tro,ma_gv) VALUES(?,SHA2(?,256),?,NULLIF(?,''))";
            try(Connection c=KNCSDL.getConnection();PreparedStatement p=c.prepareStatement(sql)){
                if(update){p.setString(1,username);p.setString(2,role);
                p.setString(3,teacher);int n=4;if(!password.isEmpty())p.setString(n++,password);
                p.setInt(n,Integer.parseInt(required(txtMaTK,"Mã tài khoản")));}
                else{p.setString(1,username);p.setString(2,password);p.setString(3,role);p.setString(4,teacher);
                }p.executeUpdate();
            }loadTK();
            clearTK();
        }catch(Exception e){showError(e);}
    }
    private void XoaTK(){
        String id=txtMaTK.getText().trim();
        if(id.isEmpty()){warn("Chọn tài khoản cần xóa.");return;}
        if(txtTenTK.getText().trim().equals(MainMenu.tenDangNhap)){
            warn("Không thể xóa tài khoản đang đăng nhập.");return;}
        if(JOptionPane.showConfirmDialog(this,"Xóa tài khoản này?","Xác nhận",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)
            return;
        try(Connection c=KNCSDL.getConnection();
                PreparedStatement p=c.prepareStatement("DELETE FROM tai_khoan WHERE ma_tk=?")){
            p.setInt(1,Integer.parseInt(id));
            p.executeUpdate();loadTK();
            clearTK();}catch(Exception e){showError(e);}}
    private void selectTK(){
        int r=tbHeThong.getSelectedRow();
        txtMaTK.setText(String.valueOf(tbHeThong.getValueAt(r, 0)));
        txtTenTK.setText(String.valueOf(tbHeThong.getValueAt(r, 1)));
        txtMatKhau.setText(String.valueOf(tbHeThong.getValueAt(r, 2)));
        txtVaiTro.setText(String.valueOf(tbHeThong.getValueAt(r, 3)));
        txtMaGV.setText(String.valueOf(tbHeThong.getValueAt(r, 4)));}
    private void clearTK(){
        txtTenTK.setText("");
        txtMatKhau.setText("");
        txtVaiTro.setText("");
        txtMaTK.setText("");
        txtMaGV.setText("");
        tbHeThong.clearSelection();
    }
    private void TimTK(){
        String tuKhoa = txtTimKiem.getText().trim();
        String dieuKien;

    switch (btnTimKiem.getSelectedIndex()) {
        case 0: dieuKien = "ma_tk LIKE ?"; break;      
        case 1: dieuKien = "ten_dn LIKE ?"; break;    
        case 2: dieuKien = "vai_tro LIKE ?"; break;    
        case 3: dieuKien = "ma_gv LIKE ?"; break;     
        default: dieuKien = "ma_tk LIKE ?";
    }

    String sql = "SELECT ma_tk, ten_dn, mat_khau, vai_tro, ma_gv "
               + "FROM sinh_vien WHERE " + dieuKien + " ORDER BY ma_tk";

    DefaultTableModel model = (DefaultTableModel) tbHeThong.getModel();
    model.setRowCount(0);

    try (Connection c = KNCSDL.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, "%" + tuKhoa + "%");

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("ma_tk"),
                    rs.getString("ten_dn"),
                    rs.getString("mat_khau"),
                    rs.getDate("vai_tro"),
                    rs.getString("ma_gv"),
                });
            }
        }
    } catch (SQLException e) {
        showError(e);
    }
    }
    
    private String required(javax.swing.JTextField f,String name)
    {String s=f.getText().trim();if(s.isEmpty())
        throw new IllegalArgumentException(name+" không được để trống.");return s;}
    private void showError(Exception e){
        JOptionPane.showMessageDialog(this,e.getMessage(),"Không thể thực hiện thao tác",JOptionPane.ERROR_MESSAGE);}
    private void warn(String s){JOptionPane.showMessageDialog(this,s,"Thông báo",JOptionPane.WARNING_MESSAGE);}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tbHeThong = new javax.swing.JTable();
        txtTimKiem = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnTimKiem = new javax.swing.JComboBox<>();
        btnSua = new javax.swing.JButton();
        btnLamMoi = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnThem = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtTenTK = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtVaiTro = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtMaTK = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtMaGV = new javax.swing.JTextField();
        txtMatKhau = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();

        tbHeThong.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã tài khoản", "Tên tài khoản", "Mật khẩu", "Vai trò", "Mã giáo viên"
            }
        ));
        tbHeThong.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbHeThongMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbHeThong);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Tìm kiếm");

        btnTimKiem.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mã TK", "Tên TK", "Vai trò", "Mã GV" }));
        btnTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimKiemActionPerformed(evt);
            }
        });

        btnSua.setText("Sửa");
        btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaActionPerformed(evt);
            }
        });

        btnLamMoi.setText("Làm mới");
        btnLamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoiActionPerformed(evt);
            }
        });

        btnXoa.setText("Xóa");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        btnThem.setText("Thêm");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        jLabel2.setText("Tên tài khoản");

        jLabel4.setText("Vai trò");

        jLabel5.setText("Mã tài khoản");

        jLabel6.setText("Mã giáo viên");

        txtMaGV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaGVActionPerformed(evt);
            }
        });

        jLabel7.setText("Mật khẩu'");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnThem, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                            .addComponent(btnSua, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
                        .addGap(32, 32, 32)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnXoa, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                            .addComponent(btnLamMoi, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel6))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtMaGV, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtVaiTro, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel5)
                                        .addComponent(jLabel2))
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTenTK, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtMaTK, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(175, 175, 175))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMaTK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTenTK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(23, 23, 23)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtVaiTro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMaGV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(39, 39, 39)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnXoa)
                            .addComponent(btnThem))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSua)
                            .addComponent(btnLamMoi))))
                .addContainerGap(13, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtMaGVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaGVActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaGVActionPerformed

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        // TODO add your handling code here:
        ThemTK();
    }//GEN-LAST:event_btnThemActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        // TODO add your handling code here:
        CapNhatTK();
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        // TODO add your handling code here:
        XoaTK();
    }//GEN-LAST:event_btnXoaActionPerformed

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        clearTK();
    }//GEN-LAST:event_btnLamMoiActionPerformed

    private void tbHeThongMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbHeThongMouseClicked
        // TODO add your handling code here:
        selectTK();
    }//GEN-LAST:event_tbHeThongMouseClicked

    private void btnTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimKiemActionPerformed
        // TODO add your handling code here:
        TimTK();
    }//GEN-LAST:event_btnTimKiemActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JComboBox<String> btnTimKiem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbHeThong;
    private javax.swing.JTextField txtMaGV;
    private javax.swing.JTextField txtMaTK;
    private javax.swing.JTextField txtMatKhau;
    private javax.swing.JTextField txtTenTK;
    private javax.swing.JTextField txtTimKiem;
    private javax.swing.JTextField txtVaiTro;
    // End of variables declaration//GEN-END:variables
}
