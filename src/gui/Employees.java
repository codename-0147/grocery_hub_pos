/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui;

import static gui.SignIn.logger;
import java.awt.Frame;
import model.MySQL;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.util.logging.Level;

/**
 *
 * @author barth
 */
public class Employees extends javax.swing.JPanel {
    private Home home;
    private static HashMap<String, String> employeeTypeMap = new HashMap<>();
    private static HashMap<String, String> employeeGenderMap = new HashMap<>();

    /**
     * Creates new form Customers
     */
    public Employees(Home home) {
        initComponents();
        this.home = home;
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        employeesTable.setDefaultRenderer(Object.class, renderer);
        loadTypes();
        loadGenders();
        loadEmployees("first_name", "ASC", searchField.getText());
    }
    
    private void loadTypes() {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `employee_type`");
            Vector<String> vector = new Vector<>();
            vector.add("Select");
            
            while (resultSet.next()) {
                vector.add(resultSet.getString("name"));
                employeeTypeMap.put(resultSet.getString("name"), resultSet.getString("id"));
            }
            
            DefaultComboBoxModel model = new DefaultComboBoxModel(vector);
            typeComboBox.setModel(model);
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
            
        }
    }
    
    private void loadGenders() {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `gender`");
            Vector<String> vector = new Vector<>();
            vector.add("Select");
            
            while (resultSet.next()) {
                vector.add(resultSet.getString("name"));
                employeeGenderMap.put(resultSet.getString("name"), resultSet.getString("id"));
            }
            
            DefaultComboBoxModel model = new DefaultComboBoxModel(vector);
            genderComboBox.setModel(model);
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }
    
    private void loadEmployees(String column, String orderBy, String email) {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `employee` "
                    + "INNER JOIN `employee_type` ON `employee`.`employee_type_id` = `employee_type`.`id` "
                    + "INNER JOIN `gender` ON `employee`.`gender_id` = `gender`.`id` "
                    + "WHERE `email` LIKE '"+email+"%' ORDER BY `"+column+"` "+orderBy+"");
            DefaultTableModel model = (DefaultTableModel)employeesTable.getModel();
            model.setRowCount(0);
            
            while (resultSet.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(resultSet.getString("email"));
                vector.add(resultSet.getString("first_name"));
                vector.add(resultSet.getString("last_name"));
                vector.add(resultSet.getString("nic"));
                vector.add(resultSet.getString("mobile"));
                vector.add(resultSet.getString("password"));
                vector.add(resultSet.getString("gender.name"));
                vector.add(resultSet.getString("employee_type.name"));
                model.addRow(vector);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }
    
    private void search() {
        if (sortByComboBox.getSelectedIndex() == 0) {
            loadEmployees("first_name", "ASC", searchField.getText());
        }else if (sortByComboBox.getSelectedIndex() == 1) {
            loadEmployees("first_name", "DESC", searchField.getText());
        }
    }
    
    private void reset () {
        sortByComboBox.setSelectedIndex(0);
        searchField.setText("");
        emailField.setText("");
        emailField.setEditable(true);
        emailField.grabFocus();
        firstNameField.setText("");
        lastNameField.setText("");
        nicField.setText("");
        mobileField.setText("");
        passwordField.setText("");
        genderComboBox.setSelectedIndex(0);
        typeComboBox.setSelectedIndex(0);
        addButton.setEnabled(true);
        employeesTable.clearSelection();
        search();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        emailField = new javax.swing.JTextField();
        firstNameField = new javax.swing.JTextField();
        addButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        lastNameField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        nicField = new javax.swing.JTextField();
        resetButton = new javax.swing.JButton();
        mobileField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        genderComboBox = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        showPasswordButton = new javax.swing.JButton();
        passwordField = new javax.swing.JPasswordField();
        jScrollPane1 = new javax.swing.JScrollPane();
        employeesTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        sortByComboBox = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        closeLabel = new javax.swing.JLabel();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel1.setText("Email :");

        jLabel2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel2.setText("First Name :");

        emailField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        firstNameField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        addButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/add_icon.png"))); // NOI18N
        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        updateButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        updateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/update-icon.png"))); // NOI18N
        updateButton.setText("Update");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        deleteButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/delete-icon.png"))); // NOI18N
        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel5.setText("Last Name :");

        lastNameField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel6.setText("NIC :");

        nicField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        resetButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        resetButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/reset-icon.png"))); // NOI18N
        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        mobileField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel8.setText("Mobile :");

        jLabel9.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel9.setText("Password :");

        genderComboBox.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        genderComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel10.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel10.setText("Gender :");

        typeComboBox.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        typeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel11.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel11.setText("Type :");

        showPasswordButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/show-icon.png"))); // NOI18N
        showPasswordButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                showPasswordButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                showPasswordButtonMouseReleased(evt);
            }
        });

        passwordField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emailField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(firstNameField)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(98, 98, 98)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(deleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(updateButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(resetButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(typeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(genderComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(mobileField)
                            .addComponent(nicField)
                            .addComponent(lastNameField)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(passwordField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(showPasswordButton)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(firstNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lastNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(nicField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(mobileField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(showPasswordButton)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(genderComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateButton)
                    .addComponent(addButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteButton)
                    .addComponent(resetButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        employeesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Email", "First Name", "Last Name", "NIC", "Mobile", "Password", "Gender", "Type"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        employeesTable.getTableHeader().setReorderingAllowed(false);
        employeesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                employeesTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(employeesTable);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 153, 255));
        jLabel3.setText("Employees");

        jLabel4.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel4.setText("Search Email :");

        searchField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchFieldKeyReleased(evt);
            }
        });

        sortByComboBox.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        sortByComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name ASC", "Name DESC" }));
        sortByComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sortByComboBoxItemStateChanged(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel7.setText("Sort By :");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(searchField)
                            .addComponent(sortByComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sortByComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        closeLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/close-icon.png"))); // NOI18N
        closeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                closeLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(closeLabel)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(closeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        String email = emailField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String nic = nicField.getText();
        String mobile = mobileField.getText();
        String password = String.valueOf(passwordField.getPassword());
        String gender = String.valueOf(genderComboBox.getSelectedItem());
        String type = String.valueOf(typeComboBox.getSelectedItem());
        
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an email", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (!email.matches("^(?=.{1,64}@)[A-Za-z0-9\\+_-]+(\\.[A-Za-z0-9\\+_-]+)*@"
                + "[^-][A-Za-z0-9\\+-]+(\\.[A-Za-z0-9\\+-]+)*(\\.[A-Za-z]{2,})$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (firstName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a first name", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (!firstName.matches("^[A-Za-z]+$")) {
            JOptionPane.showMessageDialog(this, "Invalid first name", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a last name", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (!lastName.matches("^[A-Za-z]+$")) {
            JOptionPane.showMessageDialog(this, "Invalid last name", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (nic.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a NIC", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (!nic.matches("^(?:\\d{9}[vVxX]|\\d{12})$")) {
            JOptionPane.showMessageDialog(this, "Invalid NIC", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (mobile.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a mobile number", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (!mobile.matches("^(?:0|94|\\+94|0094)?(?:(11|21|23|24|25|26|27|31|32|33|34|35|36|37|38|41|45|47|51|52|"
                + "54|55|57|63|65|66|67|81|91)(0|2|3|4|5|7|9)|7(0|1|2|4|5|6|7|8)\\d)\\d{6}$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid mobile number", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a password", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,10}$")) {
            JOptionPane.showMessageDialog(this, "Password must be minimum eight and maximum 10 characters, "
                    + "at least one uppercase letter, one lowercase letter, "
                    + "one number and one special character", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (gender.equals("Select")) {
            JOptionPane.showMessageDialog(this, "Please select a gender", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (type.equals("Select")) {
            JOptionPane.showMessageDialog(this, "Please select an employee type", "Warning", JOptionPane.WARNING_MESSAGE);
        }else {
            try {
                ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `employee` "
                        + "WHERE `email` = '"+email+"' OR `nic` = '"+nic+"' OR `mobile` = '"+mobile+"'");
                
                if (resultSet.next()) {
                    JOptionPane.showMessageDialog(this, "An employee is already registered with the same email and or "
                            + "NIC and or mobile", "Warning", JOptionPane.WARNING_MESSAGE);
                }else {
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    
                    MySQL.executeIUD("INSERT INTO `employee`(`email`, `password`, `first_name`, `last_name`, `nic`, `mobile`, "
                            + "`date_registered`, `employee_type_id`, `gender_id`) "
                            + "VALUES('"+email+"', '"+password+"', '"+firstName+"', '"+lastName+"', '"+nic+"', '"+mobile+"', "
                            + "'"+sdf.format(date)+"', '"+employeeTypeMap.get(type)+"', '"+employeeGenderMap.get(gender)+"')");
                    loadEmployees("first_name", "ASC", searchField.getText());
                    reset();
                    logger.log(Level.INFO, "Successfully added employee: " + email);
                    JOptionPane.showMessageDialog(this, "Employee added successfully", "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                //e.printStackTrace();
                logger.log(Level.WARNING, "Exception", e);
            }
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void employeesTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_employeesTableMouseClicked
        int row = employeesTable.getSelectedRow();
        String email = String.valueOf(employeesTable.getValueAt(row, 0));
        emailField.setText(email);
        emailField.setEditable(false);
        String firstName = String.valueOf(employeesTable.getValueAt(row, 1));
        firstNameField.setText(firstName);
        String lastName = String.valueOf(employeesTable.getValueAt(row, 2));
        lastNameField.setText(lastName);
        String nic = String.valueOf(employeesTable.getValueAt(row, 3));
        nicField.setText(nic);
        String mobile = String.valueOf(employeesTable.getValueAt(row, 4));
        mobileField.setText(mobile);
        String password = String.valueOf(employeesTable.getValueAt(row, 5));
        passwordField.setText(password);
        String gender = String.valueOf(employeesTable.getValueAt(row, 6));
        genderComboBox.setSelectedItem(gender);
        String type = String.valueOf(employeesTable.getValueAt(row, 7));
        typeComboBox.setSelectedItem(type);
        addButton.setEnabled(false);
        
        if (evt.getClickCount() == 2) {
            int row1 = employeesTable.getSelectedRow();
            String email1 = String.valueOf(employeesTable.getValueAt(row1, 0));
            AddressView addressView = new AddressView((Frame) SwingUtilities.getWindowAncestor(this), true, email1);
            addressView.setVisible(true);
        }
    }//GEN-LAST:event_employeesTableMouseClicked

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        String email = emailField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String nic = nicField.getText();
        String mobile = mobileField.getText();
        String password = String.valueOf(passwordField.getPassword());
        String gender = String.valueOf(genderComboBox.getSelectedItem());
        String type = String.valueOf(typeComboBox.getSelectedItem());
        int row = employeesTable.getSelectedRow();
        
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to update", "Warning", JOptionPane.WARNING_MESSAGE);
        }else {
            String selectedFirstName = String.valueOf(employeesTable.getValueAt(row, 1));
            String selectedLastName = String.valueOf(employeesTable.getValueAt(row, 2));
            String selectedNic = String.valueOf(employeesTable.getValueAt(row, 3));
            String selectedMobile = String.valueOf(employeesTable.getValueAt(row, 4));
            String selectedPassword = String.valueOf(employeesTable.getValueAt(row, 5));
            String selectedGender = String.valueOf(employeesTable.getValueAt(row, 6));
            String selectedType = String.valueOf(employeesTable.getValueAt(row, 7));

            if (firstName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a first name", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (!firstName.matches("^[A-Za-z]+$")) {
                JOptionPane.showMessageDialog(this, "Invalid first name", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (lastName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a last name", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (!lastName.matches("^[A-Za-z]+$")) {
                JOptionPane.showMessageDialog(this, "Invalid last name", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (nic.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a NIC", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (!nic.matches("^(?:\\d{9}[vVxX]|\\d{12})$")) {
                JOptionPane.showMessageDialog(this, "Invalid NIC", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (mobile.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a mobile", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (!mobile.matches("^(?:0|94|\\+94|0094)?(?:(11|21|23|24|25|26|27|31|32|33|34|35|36|37|38|41|45|47|"
                    + "51|52|54|55|57|63|65|66|67|81|91)(0|2|3|4|5|7|9)|7(0|1|2|4|5|6|7|8)\\d)\\d{6}$")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid mobile", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a password", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,10}$")) {
                JOptionPane.showMessageDialog(this, "Password must be minimum eight and maximum 10 characters, "
                        + "at least one uppercase letter, one lowercase letter, "
                        + "one number and one special character", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (gender.equals("Select")) {
                JOptionPane.showMessageDialog(this, "Please select a gender", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (type.equals("Select")) {
                JOptionPane.showMessageDialog(this, "Please select an employee type", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (selectedFirstName.equals(firstName) && selectedLastName.equals(lastName) && selectedNic.equals(nic) && 
                    selectedMobile.equals(mobile) && selectedPassword.equals(password) && selectedGender.equals(gender) && 
                    selectedType.equals(type)) {
                JOptionPane.showMessageDialog(this, "Please change first name or last name or NIC or mobile or password or "
                        + "gender or type to update", "Warning", JOptionPane.WARNING_MESSAGE);
            }else {
                try {
                    ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `employee` "
                            + "WHERE `nic` = '"+nic+"' OR `mobile` = '"+mobile+"'");
                    boolean canUpdate = false;

                    if (resultSet.next()) {
                        if (!resultSet.getString("email").equals(email)) {
                            JOptionPane.showMessageDialog(this, "This mobile number and or NIC is already used", "Warning", JOptionPane.WARNING_MESSAGE);
                        }else {
                            canUpdate = true;
                        }
                    }else {
                        canUpdate = true;
                    }

                    if (canUpdate) {
                        MySQL.executeIUD("UPDATE `employee` SET `password` = '"+password+"', `first_name` = '"+firstName+"', "
                                + "`last_name` = '"+lastName+"', `nic` = '"+nic+"', `mobile` = '"+mobile+"', "
                                + "`employee_type_id` = '"+employeeTypeMap.get(type)+"', "
                                + "`gender_id` = '"+employeeGenderMap.get(gender)+"' WHERE `email` = '"+email+"'");
                        loadEmployees("first_name", "ASC", searchField.getText());
                        reset();
                        logger.log(Level.INFO, "Successfully updated details of employee: " + email);
                        JOptionPane.showMessageDialog(this, "Employee updated successfully", "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.log(Level.WARNING, "Exception", e);
                }
            }
        }
    }//GEN-LAST:event_updateButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int row = employeesTable.getSelectedRow();
        
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete", "Warning", JOptionPane.WARNING_MESSAGE);
        }else {
            int showConfirmMessage = JOptionPane.showConfirmDialog(this, "Do you want to delete selected employee?", "Warning", 
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (showConfirmMessage == JOptionPane.YES_OPTION) {
                String email = String.valueOf(employeesTable.getValueAt(row, 0));

                try {
                    MySQL.executeIUD("DELETE FROM `employee` WHERE `email` = '"+email+"'");
                    loadEmployees("first_name", "ASC", searchField.getText());
                    reset();
                    logger.log(Level.INFO, "Successfully deleted employee: " + email);
                    JOptionPane.showMessageDialog(this, "Employee deleted successfully", "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.log(Level.WARNING, "Exception", e);
                }
            }
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        reset();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void sortByComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sortByComboBoxItemStateChanged
        search();
    }//GEN-LAST:event_sortByComboBoxItemStateChanged

    private void searchFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchFieldKeyReleased
        search();
    }//GEN-LAST:event_searchFieldKeyReleased

    private void showPasswordButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showPasswordButtonMousePressed
        passwordField.setEchoChar('\u0000');
    }//GEN-LAST:event_showPasswordButtonMousePressed

    private void showPasswordButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showPasswordButtonMouseReleased
        passwordField.setEchoChar('\u2022');
    }//GEN-LAST:event_showPasswordButtonMouseReleased

    private void closeLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closeLabelMouseClicked
        this.home.removeEmployees();
    }//GEN-LAST:event_closeLabelMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JLabel closeLabel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextField emailField;
    private javax.swing.JTable employeesTable;
    private javax.swing.JTextField firstNameField;
    private javax.swing.JComboBox<String> genderComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField lastNameField;
    private javax.swing.JTextField mobileField;
    private javax.swing.JTextField nicField;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JButton resetButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JButton showPasswordButton;
    private javax.swing.JComboBox<String> sortByComboBox;
    private javax.swing.JComboBox<String> typeComboBox;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
