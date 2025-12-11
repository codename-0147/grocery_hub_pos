/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package gui;

import static gui.SignIn.logger;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import model.MySQL;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author barth
 */
public class BrandsNProducts extends javax.swing.JDialog {
    private ProductsNStock pns;
    HashMap<String, String> brandMap = new HashMap<>();

    /**
     * Creates new form BrandsNProducts
     */
    public BrandsNProducts(java.awt.Frame parent, boolean modal, ProductsNStock pns) {
        super(parent, modal);
        this.pns = pns;
        initComponents();
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        brandsTable.setDefaultRenderer(Object.class, renderer);
        productsTable.setDefaultRenderer(Object.class, renderer);
        loadProducts();
        loadBrands();
        loadBrandsComboBox();
    }
    
    private void loadProducts() {
        try {
            String query = "SELECT * FROM `product` INNER JOIN `brand` "
                    + "ON `product`.`brand_id` = `brand`.`id`";
            String sort = String.valueOf(productSortByComboBox.getSelectedItem());
            
            if (sort.equals("Name ASC")) {
                query += " ORDER BY `product`.`name` ASC";
            }else if (sort.equals("Name DESC")) {
                query += " ORDER BY `product`.`name` DESC";
            }else if (sort.equals("Brand ASC")) {
                query += " ORDER BY `brand`.`name` ASC";
            }else if (sort.equals("Brand DESC")) {
                query += " ORDER BY `brand`.`name` DESC";
            }
            
            ResultSet resultSet = MySQL.executeSearch(query);
            DefaultTableModel model = (DefaultTableModel)productsTable.getModel();
            model.setRowCount(0);
            
            while (resultSet.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(resultSet.getString("product.id"));
                vector.add(resultSet.getString("brand.name"));
                vector.add(resultSet.getString("product.name"));
                vector.add(resultSet.getString("product.upc"));
                model.addRow(vector);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }
    
    private void loadBrands() {
        try {
            String query = "SELECT * FROM `brand`";
            String sort = String.valueOf(brandSortByComboBox.getSelectedItem());
            
            if (sort.equals("Name ASC")) {
                query += " ORDER BY `name` ASC";
            }else if (sort.equals("Name DESC")) {
                query += " ORDER BY `name` DESC";
            }
            
            ResultSet resultSet = MySQL.executeSearch(query);
            DefaultTableModel model = (DefaultTableModel)brandsTable.getModel();
            model.setRowCount(0);
            
            while (resultSet.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(resultSet.getString("id"));
                vector.add(resultSet.getString("name"));
                model.addRow(vector);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }
    
    private void loadBrandsComboBox() {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `brand`");
            Vector<String> vector = new Vector<>();
            vector.add("Select");
            
            while (resultSet.next()) {
                vector.add(resultSet.getString("name"));
                brandMap.put(resultSet.getString("name"), resultSet.getString("id"));
            }
            
            DefaultComboBoxModel model = new DefaultComboBoxModel(vector);
            brandComboBox.setModel(model);
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }
    
    private void resetBrands() {
        brandNameField.setText("");
        brandSortByComboBox.setSelectedIndex(0);
        brandsTable.clearSelection();
        addBrandButton.setEnabled(true);
        loadBrands();
    }
    
    private void resetProducts() {
        productIDField.setText("");
        productNameField.setText("");
        productUPCField.setText("");
        brandComboBox.setSelectedIndex(0);
        productSortByComboBox.setSelectedIndex(0);
        productsTable.clearSelection();
        productIDField.setEditable(true);
        addProductButton.setEnabled(true);
        loadProducts();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        brandNameField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        brandsTable = new javax.swing.JTable();
        addBrandButton = new javax.swing.JButton();
        updateBrandButton = new javax.swing.JButton();
        deleteBrandButton = new javax.swing.JButton();
        resetBrandButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        brandSortByComboBox = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        productIDField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        productNameField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        productUPCField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        productsTable = new javax.swing.JTable();
        resetProductButton = new javax.swing.JButton();
        updateProductButton = new javax.swing.JButton();
        addProductButton = new javax.swing.JButton();
        deleteProductButton = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        productSortByComboBox = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        brandComboBox = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Brands & Products");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 153, 255));
        jLabel1.setText("Brands");

        jLabel2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel2.setText("Name :");

        brandNameField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        brandsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        brandsTable.getTableHeader().setReorderingAllowed(false);
        brandsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                brandsTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(brandsTable);

        addBrandButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        addBrandButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/add_icon.png"))); // NOI18N
        addBrandButton.setText("Add");
        addBrandButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBrandButtonActionPerformed(evt);
            }
        });

        updateBrandButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        updateBrandButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/update-icon.png"))); // NOI18N
        updateBrandButton.setText("Update");
        updateBrandButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateBrandButtonActionPerformed(evt);
            }
        });

        deleteBrandButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        deleteBrandButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/delete-icon.png"))); // NOI18N
        deleteBrandButton.setText("Delete");
        deleteBrandButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBrandButtonActionPerformed(evt);
            }
        });

        resetBrandButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        resetBrandButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/reset-icon.png"))); // NOI18N
        resetBrandButton.setText("Reset");
        resetBrandButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetBrandButtonActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel7.setText("Sort By :");

        brandSortByComboBox.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        brandSortByComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name ASC", "Name DESC" }));
        brandSortByComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                brandSortByComboBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(addBrandButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(deleteBrandButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(resetBrandButton, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(118, 118, 118)
                                .addComponent(updateBrandButton))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(brandNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(brandSortByComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(brandNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(brandSortByComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(addBrandButton)
                            .addComponent(updateBrandButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(deleteBrandButton)
                            .addComponent(resetBrandButton)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 153, 255));
        jLabel3.setText("Products");

        jLabel4.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel4.setText("ID :");

        productIDField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel5.setText("Name :");

        productNameField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel6.setText("UPC :");

        productUPCField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        productsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Brand", "Name", "UPC"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        productsTable.getTableHeader().setReorderingAllowed(false);
        productsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                productsTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(productsTable);

        resetProductButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        resetProductButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/reset-icon.png"))); // NOI18N
        resetProductButton.setText("Reset");
        resetProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetProductButtonActionPerformed(evt);
            }
        });

        updateProductButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        updateProductButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/update-icon.png"))); // NOI18N
        updateProductButton.setText("Update");
        updateProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateProductButtonActionPerformed(evt);
            }
        });

        addProductButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        addProductButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/add_icon.png"))); // NOI18N
        addProductButton.setText("Add");
        addProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addProductButtonActionPerformed(evt);
            }
        });

        deleteProductButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        deleteProductButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/delete-icon.png"))); // NOI18N
        deleteProductButton.setText("Delete");
        deleteProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteProductButtonActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel8.setText("Sort By :");

        productSortByComboBox.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        productSortByComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name ASC", "Name DESC", "Brand ASC", "Brand DESC" }));
        productSortByComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                productSortByComboBoxItemStateChanged(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel9.setText("Brand :");

        brandComboBox.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        brandComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(productNameField, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(productUPCField, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(brandComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(productSortByComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 176, Short.MAX_VALUE)
                                    .addComponent(productIDField)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(deleteProductButton, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                                    .addComponent(addProductButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(updateProductButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(resetProductButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(productIDField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(productNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(productUPCField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(brandComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(productSortByComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(addProductButton)
                            .addComponent(updateProductButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(deleteProductButton)
                            .addComponent(resetProductButton)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void addBrandButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBrandButtonActionPerformed
        String brand = brandNameField.getText();
        
        if (brand.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a brand name", "Warning", JOptionPane.WARNING_MESSAGE);
        }else {
            try {
                ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `brand` WHERE `name` = '"+brand+"'");
                
                if (resultSet.next()) {
                    JOptionPane.showMessageDialog(this, "This brand already added", "Warning", JOptionPane.WARNING_MESSAGE);
                }else {
                    MySQL.executeIUD("INSERT INTO `brand`(`name`) VALUES('"+brand+"')");
                    loadBrandsComboBox();
                    resetBrands();
                    pns.loadStock();
                    logger.log(Level.INFO, "Successfully added new brand: " + brand);
                    JOptionPane.showMessageDialog(this, "New brand added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                //e.printStackTrace();
                logger.log(Level.WARNING, "Exception", e);
            }
        }
    }//GEN-LAST:event_addBrandButtonActionPerformed

    private void updateBrandButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateBrandButtonActionPerformed
        int row = brandsTable.getSelectedRow();
        
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a brand to update", "Warning", JOptionPane.WARNING_MESSAGE);
        }else {
            String brand = brandNameField.getText();
            String selectedBrand = String.valueOf(brandsTable.getValueAt(row, 1));
            
            if (brand.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a brand name", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (selectedBrand.equals(brand)) {
                JOptionPane.showMessageDialog(this, "Please change name to update", "Warning", JOptionPane.WARNING_MESSAGE);
            }else {
                try {
                    ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `brand` WHERE `name` = '"+brand+"'");

                    if (resultSet.next()) {
                        JOptionPane.showMessageDialog(this, "This brand already added", "Warning", JOptionPane.WARNING_MESSAGE);
                    }else {
                        MySQL.executeIUD("UPDATE `brand` SET `name` = '"+brand+"' "
                                + "WHERE `id` = '"+String.valueOf(brandsTable.getValueAt(row, 0))+"'");
                        loadBrandsComboBox();
                        resetBrands();
                        pns.loadStock();
                        logger.log(Level.INFO, "Successfully updated brand: " + brand);
                        JOptionPane.showMessageDialog(this, "Brand updated successfully", "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.log(Level.WARNING, "Exception", e);
                }
            }
        }
    }//GEN-LAST:event_updateBrandButtonActionPerformed

    private void deleteBrandButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBrandButtonActionPerformed
        int row = brandsTable.getSelectedRow();
        
        try {
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a brand to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            }else {
                String brand = brandNameField.getText();
                
                int showConfirmMessage = JOptionPane.showConfirmDialog(this, "Do you want to delete selected brand?", "Warning", 
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (showConfirmMessage == JOptionPane.YES_OPTION) {
                    MySQL.executeIUD("DELETE FROM `brand` WHERE `id` = '"+String.valueOf(brandsTable.getValueAt(row, 0))+"'");
                    loadBrandsComboBox();
                    resetBrands();
                    pns.loadStock();
                    logger.log(Level.INFO, "Successfully deleted brand: " + brand);
                    JOptionPane.showMessageDialog(this, "Brand deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }//GEN-LAST:event_deleteBrandButtonActionPerformed

    private void resetBrandButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBrandButtonActionPerformed
        resetBrands();
    }//GEN-LAST:event_resetBrandButtonActionPerformed

    private void brandSortByComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_brandSortByComboBoxItemStateChanged
        loadBrands();
    }//GEN-LAST:event_brandSortByComboBoxItemStateChanged

    private void brandsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_brandsTableMouseClicked
        int row  = brandsTable.getSelectedRow();
        brandNameField.setText(String.valueOf(brandsTable.getValueAt(row, 1)));
        addBrandButton.setEnabled(false);
    }//GEN-LAST:event_brandsTableMouseClicked

    private void addProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addProductButtonActionPerformed
        String id = productIDField.getText();
        String upc = productUPCField.getText();
        String brand = String.valueOf(brandComboBox.getSelectedItem());
        String productName = productNameField.getText();
        
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a product ID", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (productName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a product name", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (upc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a product UPC", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (brand.equals("Select")) {
            JOptionPane.showMessageDialog(this, "Please select a brand", "Warning", JOptionPane.WARNING_MESSAGE);
        }else {
            try {
                ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `product` WHERE `id` = '"+id+"' "
                        + "OR `upc` = '"+upc+"' OR (`name` = '"+productName+"' AND `brand_id` = '"+brandMap.get(brand)+"')");
                
                if (resultSet.next()) {
                    JOptionPane.showMessageDialog(this, "This product already added", "Warning", JOptionPane.WARNING_MESSAGE);
                }else {
                    MySQL.executeIUD("INSERT INTO `product`(`id`, `name`, `brand_id`, `upc`) "
                            + "VALUES('"+id+"', '"+productName+"', '"+brandMap.get(brand)+"', '"+upc+"')");
                    resetProducts();
                    pns.loadProducts();
                    pns.loadStock();
                    logger.log(Level.INFO, "Successfully added new product: " + id);
                    JOptionPane.showMessageDialog(this, "New product added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                //e.printStackTrace();
                logger.log(Level.WARNING, "Exception", e);
            }
        }
    }//GEN-LAST:event_addProductButtonActionPerformed

    private void updateProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateProductButtonActionPerformed
        String id = productIDField.getText();
        String upc = productUPCField.getText();
        String brand = String.valueOf(brandComboBox.getSelectedItem());
        String productName = productNameField.getText();
        int row = productsTable.getSelectedRow();
        
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to update", "Warning", JOptionPane.WARNING_MESSAGE);
        }else {
            String selectedBrand = String.valueOf(productsTable.getValueAt(row, 1));
            String selectedName = String.valueOf(productsTable.getValueAt(row, 2));
            String selectedUPC = String.valueOf(productsTable.getValueAt(row, 3));
            
            if (productName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a product name", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (upc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a product UPC", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (brand.equals("Select")) {
                JOptionPane.showMessageDialog(this, "Please select a brand", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (selectedName.equals(productName) && selectedUPC.equals(upc) && selectedBrand.equals(brand)) {
                JOptionPane.showMessageDialog(this, "Please change name or upc or brand to update", "Warning", 
                        JOptionPane.WARNING_MESSAGE);
            }else {
                try {
                    //ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `product` WHERE `upc` = '"+upc+"' "
                    //        + "OR (`name` = '"+productName+"' AND `brand_id` = '"+brandMap.get(brand)+"')");
                    
                    ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `product` WHERE `name` = '"+productName+"' "
                            + "AND `brand_id` = '"+brandMap.get(brand)+"'");
                    
                    if (resultSet.next()) {
                        JOptionPane.showMessageDialog(this, "This product already added", "Warning", JOptionPane.WARNING_MESSAGE);
                    }else {
                        MySQL.executeIUD("UPDATE `product` SET `name` = '"+productName+"', `upc` = '"+upc+"', "
                                + "`brand_id` = '"+brandMap.get(brand)+"' WHERE `id` = '"+id+"'");
                        resetProducts();
                        pns.loadProducts();
                        pns.loadStock();
                        logger.log(Level.INFO, "Successfully updated product: " + id);
                        JOptionPane.showMessageDialog(this, "Product updated successfully", "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.log(Level.WARNING, "Exception", e);
                }
            }
        }
    }//GEN-LAST:event_updateProductButtonActionPerformed

    private void deleteProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteProductButtonActionPerformed
        int row = productsTable.getSelectedRow();
        
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete", "Warning", JOptionPane.WARNING_MESSAGE);
        }else {
            int showConfirmMessage = JOptionPane.showConfirmDialog(this, "Do you want to delete selected product?", "Warning", 
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (showConfirmMessage == JOptionPane.YES_OPTION) {
                String id = productIDField.getText();
            
                try {
                    MySQL.executeIUD("DELETE FROM `product` WHERE `id` = '"+id+"'");
                    resetProducts();
                    pns.loadProducts();
                    pns.loadStock();
                    logger.log(Level.INFO, "Successfully deleted product: " + id);
                    JOptionPane.showMessageDialog(this, "Product deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.log(Level.WARNING, "Exception", e);
                }
            }
        }
    }//GEN-LAST:event_deleteProductButtonActionPerformed

    private void resetProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetProductButtonActionPerformed
        resetProducts();
    }//GEN-LAST:event_resetProductButtonActionPerformed

    private void productsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_productsTableMouseClicked
        int row  = productsTable.getSelectedRow();
        productIDField.setText(String.valueOf(productsTable.getValueAt(row, 0)));
        brandComboBox.setSelectedItem(String.valueOf(productsTable.getValueAt(row, 1)));
        productNameField.setText(String.valueOf(productsTable.getValueAt(row, 2)));
        productUPCField.setText(String.valueOf(productsTable.getValueAt(row, 3)));
        productIDField.setEditable(false);
        addProductButton.setEnabled(false);
    }//GEN-LAST:event_productsTableMouseClicked

    private void productSortByComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_productSortByComboBoxItemStateChanged
        loadProducts();
    }//GEN-LAST:event_productSortByComboBoxItemStateChanged

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBrandButton;
    private javax.swing.JButton addProductButton;
    private javax.swing.JComboBox<String> brandComboBox;
    private javax.swing.JTextField brandNameField;
    private javax.swing.JComboBox<String> brandSortByComboBox;
    private javax.swing.JTable brandsTable;
    private javax.swing.JButton deleteBrandButton;
    private javax.swing.JButton deleteProductButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField productIDField;
    private javax.swing.JTextField productNameField;
    private javax.swing.JComboBox<String> productSortByComboBox;
    private javax.swing.JTextField productUPCField;
    private javax.swing.JTable productsTable;
    private javax.swing.JButton resetBrandButton;
    private javax.swing.JButton resetProductButton;
    private javax.swing.JButton updateBrandButton;
    private javax.swing.JButton updateProductButton;
    // End of variables declaration//GEN-END:variables
}
