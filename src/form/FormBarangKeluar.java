/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package form;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Ridho
 */
public class FormBarangKeluar extends javax.swing.JDialog {

    /**
     * Creates new form FormBarangKeluar
     */
    
    public Statement st;
    public ResultSet rs;
    private int IDSupplier;
    private int KodeBarang;
    Connection connect = utils.connection.OpenConnection();
    private boolean isUpdate = false;
    
    public FormBarangKeluar(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        GetTransactionData();
        getOptionSupplier();
        getOptionBarang();
    }
    
    private void getOptionBarang(){
        try {
            String query = "SELECT * FROM barang";
            st = connect.createStatement();
            rs = st.executeQuery(query);
        
            while(rs.next()){
                jComboBox2.addItem(rs.getString("NamaBarang"));
            }
            
        
        }catch(SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void getOptionSupplier(){
        try {
            String query = "SELECT * FROM supplier";
            st = connect.createStatement();
            rs = st.executeQuery(query);
        
            while(rs.next()){
                jComboBox1.addItem(rs.getString("NamaSupplier"));
            }
            
        
        }catch(SQLException ex) {
            ex.printStackTrace();
        }
        
    }
    
    private void GetTransactionData(){
        try{
            String query = "SELECT * FROM transaksi AS TR\n" +
                            "LEFT JOIN barang AS BR ON TR.IDBarang = BR.KodeBarang\n" +
                            "LEFT JOIN supplier AS SP ON TR.IDSupplier = SP.IDSupplier\n" +
                            "WHERE TR.JenisTransaksi = 'keluar'";
            st = connect.createStatement();
            rs = st.executeQuery(query);
            
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID Transaksi");
            model.addColumn("Nama Supplier");
            model.addColumn("Nama Barang");
            model.addColumn("Tanggal");
            model.addColumn("Jumlah");
            model.addColumn("Total Harga");

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("TR.IDTransaksi"),
                    rs.getString("SP.NamaSupplier"),
                    rs.getString("BR.NamaBarang"),
                    rs.getDate("TR.TanggalTransaksi"),
                    rs.getInt("TR.JumlahBarang"),
                    rs.getInt("TR.TotalHarga")
                };
                model.addRow(row);
            }

            jTable1.setModel(model);
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void clearForm() {
                jButton1.setText("Simpan");
                isUpdate = false;
                jComboBox1.setEnabled(true);
                jComboBox2.setEnabled(true);
                jTextField1.setText("");
                jComboBox1.setSelectedIndex(0);
                jComboBox2.setSelectedIndex(0);
                jSpinner1.setValue(0);
    }
    
private void createTransaction() {
    try {
        String hargaBarang = "SELECT Harga FROM barang WHERE KodeBarang = '" + KodeBarang + "'";
        
        st = connect.createStatement();
        rs = st.executeQuery(hargaBarang);
        
        int totalHarga = 0;
        int harga = 0;
        int quantity = (int) jSpinner1.getValue();
        while(rs.next()){
            harga = rs.getInt("Harga");
        }
        
        totalHarga = harga * quantity;
        
        String insertQuery = "INSERT INTO transaksi (IDSupplier, IDBarang, JenisTransaksi, TanggalTransaksi, JumlahBarang, TotalHarga) "
                + "VALUES (" + IDSupplier + ", " + KodeBarang + ", 'keluar', NOW(), " + jSpinner1.getValue() + ", "+ totalHarga + ")";
        st.executeUpdate(insertQuery);
        
        String updateStockQuery = "UPDATE barang SET JumlahStok = JumlahStok - " + quantity + " WHERE KodeBarang = " + KodeBarang;
        st.executeUpdate(updateStockQuery);

        clearForm();
        GetTransactionData();
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}

private void updateTransaction(int transactionID) {
    try {
        String hargaBarang = "SELECT Harga FROM barang WHERE KodeBarang = '" + KodeBarang + "'";
        st = connect.createStatement();
        rs = st.executeQuery(hargaBarang);

        int totalHarga = 0;
        int harga = 0;
        int quantity = (int) jSpinner1.getValue();
        while (rs.next()) {
            harga = rs.getInt("Harga");
        }

        totalHarga = harga * quantity;
        
        // Calculate the old quantity and totalHarga from the existing transaction
        String getOldTransactionData = "SELECT JumlahBarang, TotalHarga FROM transaksi WHERE IDTransaksi = " + transactionID;
        rs = st.executeQuery(getOldTransactionData);
        
        int oldQuantity = 0;
        int oldTotalHarga = 0;
        if (rs.next()) {
            oldQuantity = rs.getInt("JumlahBarang");
            oldTotalHarga = rs.getInt("TotalHarga");
        }
        
        // Update stock in barang table (reduce stock)
        String updateStockQuery = "UPDATE barang SET JumlahStok = JumlahStok + " + oldQuantity + " - " + quantity +
                                 " WHERE KodeBarang = " + KodeBarang;
        st.executeUpdate(updateStockQuery);

        // Update transaction in transaksi table
        String updateQuery = "UPDATE transaksi SET IDSupplier = " + IDSupplier + ", IDBarang = " + KodeBarang + ", "
                + "JumlahBarang = " + jSpinner1.getValue() + ", TotalHarga = " + totalHarga + " WHERE IDTransaksi = " + transactionID;
        st.executeUpdate(updateQuery);

        clearForm();
        GetTransactionData();
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}



private void deleteTransaction(int transactionID) {
    try {
        String updateStockQuery = "UPDATE barang SET JumlahStok = JumlahStok + (SELECT JumlahBarang FROM transaksi WHERE IDTransaksi = " + transactionID + ") WHERE KodeBarang = " + KodeBarang;
        st.executeUpdate(updateStockQuery);

        String deleteQuery = "DELETE FROM transaksi WHERE IDTransaksi = " + transactionID;
        st.executeUpdate(deleteQuery);

        clearForm();
        GetTransactionData();
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label1 = new java.awt.Label();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        label1.setAlignment(java.awt.Label.CENTER);
        label1.setBackground(new java.awt.Color(0, 114, 206));
        label1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        label1.setForeground(new java.awt.Color(235, 235, 235));
        label1.setText("Kelola Barang Keluar");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID Transaksi", "Nama Supplier", "Nama Barang", "Tanggal", "Jumlah Barang", "Total Harga"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setText("ID Transaksi");

        jLabel2.setText("Nama Supplier");

        jTextField1.setEnabled(false);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pilih Supplier" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pilih Barang" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jLabel3.setText("Nama Barang");

        jLabel4.setText("Jumlah Barang");

        jButton1.setText("Simpan");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Bersihkan");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Hapus");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, 743, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 3, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.Alignment.LEADING, 0, 100, Short.MAX_VALUE)
                                .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.LEADING))
                            .addComponent(jLabel3))
                        .addGap(37, 37, 37)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel2))
                            .addComponent(jLabel4)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton2)
                            .addComponent(jButton3))))
                .addGap(23, 23, 23))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
        try{
        String selectedBarang = jComboBox2.getSelectedItem().toString();
        
        String query = "SELECT KodeBarang FROM barang WHERE NamaBarang = '" + selectedBarang + "'";
        st = connect.createStatement();
        rs = st.executeQuery(query);

        if (rs.next()) {
            KodeBarang = rs.getInt("KodeBarang");
        }
        
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
        // TODO add your handling code here:
        try{
        String selectedSupplier = jComboBox1.getSelectedItem().toString();
        
        String query = "SELECT IDSupplier FROM supplier WHERE NamaSupplier = '" + selectedSupplier + "'";
        st = connect.createStatement();
        rs = st.executeQuery(query);

        if (rs.next()) {
            IDSupplier = rs.getInt("IDSupplier");
        }
        
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        int selectedRow = jTable1.getSelectedRow();
    
        if (selectedRow != -1) {
            jTextField1.setText(jTable1.getValueAt(selectedRow, 0).toString()); // Assuming ID Transaksi is in the first column
            jComboBox1.setSelectedItem(jTable1.getValueAt(selectedRow, 1).toString()); // Assuming Nama Supplier is in the second column
            jComboBox2.setSelectedItem(jTable1.getValueAt(selectedRow, 2).toString()); // Assuming Nama Barang is in the third column
            jSpinner1.setValue(Integer.parseInt(jTable1.getValueAt(selectedRow, 4).toString())); // Assuming Jumlah Barang is in the fifth column
        }
        
        isUpdate = true;
        jButton1.setText("Edit");
        jTextField1.setEnabled(false);
        jComboBox1.setEnabled(false);
        jComboBox2.setEnabled(false);
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
         if (!isUpdate) {
            createTransaction();
        } else {
            int transactionID = Integer.parseInt(jTextField1.getText());
            updateTransaction(transactionID);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:

        clearForm();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        int opsi = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (opsi == JOptionPane.YES_OPTION) {
                int transactionID = Integer.parseInt(jTextField1.getText());
                deleteTransaction(transactionID);
                GetTransactionData();
                clearForm();
            }
        if (!jTextField1.getText().isEmpty()) {
        
    }
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormBarangKeluar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormBarangKeluar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormBarangKeluar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormBarangKeluar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FormBarangKeluar dialog = new FormBarangKeluar(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private java.awt.Label label1;
    // End of variables declaration//GEN-END:variables
}
