/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui;

import static gui.SignIn.logger;
import java.awt.Color;
import java.awt.Frame;
import java.io.File;
import java.io.InputStream;
import model.MySQL;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.InvoiceItem;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author barth
 */
public class Invoice extends javax.swing.JPanel {
    private Home home;
    HashMap<String, InvoiceItem> invoiceItemMap = new HashMap<>();
    HashMap<String, String> paymentMethodMap = new HashMap<>();

    /**
     * Creates new form GRN
     */
    public Invoice(Home home) {
        initComponents();
        this.home = home;
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        invoiceItemTable.setDefaultRenderer(Object.class, renderer);
        employeeEmailLabel.setText(SignIn.getEmployeeEmail());
        generateInvoiceID();
        loadPaymentMethods();
    }
    
    private void generateInvoiceID() {
        long id = System.currentTimeMillis();
        invoiceIDField.setText(String.valueOf(id));
    }
    
    public JTextField getinvoiceIDField() {
        return invoiceIDField;
    }
    
    public JTextField getcustomerMobileField() {
        return customerMobileField;
    }
    
    public JLabel getcustomerNameLabel() {
        return customerNameLabel;
    }
    
    public JTextField getstockIDField() {
        return stockIDField;
    }
    
    public JTextField getstockBarcodeField() {
        return stockBarcodeField;
    }
    
    public JTextField getqtyFormattedField() {
        return qtyFormattedField;
    }
    
    public JLabel getavailableQtyLabel() {
        return availableQtyLabel;
    }
    
    public JLabel getbrandLabel() {
        return brandLabel;
    }
    
    public JLabel getproductNameLabel() {
        return productNameLabel;
    }
    
    public JLabel getmfdLabel() {
        return mfdLabel;
    }
    
    public JLabel getexpLabel() {
        return expLabel;
    }
    
    public JTextField getavailablePointsField() {
        return availablePointsField;
    }
    
    public JTextField getsellingPriceField() {
        return sellingPriceField;
    }
    
    public JLabel getstoreCreditLabel() {
        return storeCreditLabel;
    }
    
    private double total = 0;
    private double discount = 0;
    private double payment = 0;
    private boolean withdrawPoints = false;
    private double balance = 0;
    private String paymentMethod = "Select";
    private double newPoints = 0;
    private boolean storeCredits = false;
    private double newCredits = 0;
    
    private void calculate() {
        if (discountFormattedField.getText().isEmpty()) {
            discount = 0;
        }else {
            discount = Double.parseDouble(discountFormattedField.getText());
        }
        
        if (paymentFormattedField.getText().isEmpty()) {
            payment = 0;
        }else {
            payment = Double.parseDouble(paymentFormattedField.getText());
        }
        
        total = Double.parseDouble(totalLabel.getText());
        
        if (withdrawPointsCheckBox.isSelected()) {
            withdrawPoints = true;
        }else {
            withdrawPoints = false;
        }
        
        if (storeCreditCheckBox.isSelected()) {
            storeCredits = true;
        }else {
            storeCredits = false;
        }
        
        paymentMethod = String.valueOf(paymentMethodComboBox.getSelectedItem());
        total -= discount;
        
        if (total < 0) {
            //handled at discountFormattedFieldKeyReleased
        }else {
            if (withdrawPoints) {
                if (Double.parseDouble(availablePointsField.getText()) == total) {
                    newPoints = 0;
                    total = 0;
                }else if (Double.parseDouble(availablePointsField.getText()) < total) {
                    newPoints = 0;
                    total -= Double.parseDouble(availablePointsField.getText());
                }else {
                    newPoints = Double.parseDouble(availablePointsField.getText()) - total;
                    total = 0;
                }
            }else if (storeCredits) {
                if (Double.parseDouble(storeCreditLabel.getText()) == total) {
                    newCredits = 0;
                    total = 0;
                }else if (Double.parseDouble(storeCreditLabel.getText()) < total) {
                    newCredits = 0;
                    total -= Double.parseDouble(storeCreditLabel.getText());
                }else {
                    newCredits = Double.parseDouble(storeCreditLabel.getText()) - total;
                    total = 0;
                }
            }
        }
        
        if (paymentMethod.equals("Cash")) {
            paymentFormattedField.setEditable(true);
            balance = payment - total;
            
            if (balance < 0) {
                saveNPrintButton.setEnabled(false);
            }else {
                saveNPrintButton.setEnabled(true);
            }
        }else {
            payment = total;
            balance = 0;
            paymentFormattedField.setText(String.valueOf(payment));
            paymentFormattedField.setEditable(false);
            saveNPrintButton.setEnabled(true);
        }
        
        balanceLabel.setText(String.valueOf(balance));
    }
    
    private void loadInvoiceItems() {
        DefaultTableModel model = (DefaultTableModel)invoiceItemTable.getModel();
        model.setRowCount(0);
        total = 0;
        
        for (InvoiceItem invoiceItem : invoiceItemMap.values()) {
            Vector<String> vector = new Vector<>();
            vector.add(invoiceItem.getStockID());
            vector.add(invoiceItem.getBrand());
            vector.add(invoiceItem.getProductName());
            vector.add(invoiceItem.getQty());
            vector.add(invoiceItem.getSellingPrice());
            vector.add(invoiceItem.getMfd());
            vector.add(invoiceItem.getExp());
            double itemTotal = Double.parseDouble(invoiceItem.getQty()) * Double.parseDouble(invoiceItem.getSellingPrice());
            total += itemTotal;
            vector.add(String.valueOf(itemTotal));
            model.addRow(vector);
        }
        
        totalLabel.setText(String.valueOf(total));
        calculate();
    }
    
    private void loadPaymentMethods() {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `payment_method`");
            Vector<String> vector = new Vector<>();
            
            while (resultSet.next()) {
                vector.add(resultSet.getString("name"));
                paymentMethodMap.put(resultSet.getString("name"), resultSet.getString("id"));
            }
            
            DefaultComboBoxModel model = new DefaultComboBoxModel(vector);
            paymentMethodComboBox.setModel(model);
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }
    
    private void reset() {
        generateInvoiceID();
        customerMobileField.setText("");
        customerNameLabel.setText("CUSTOMER NAME");
        stockIDField.setText("");
        qtyFormattedField.setText("0");
        availableQtyLabel.setText("0");
        brandLabel.setText("BRAND NAME");
        productNameLabel.setText("PRODUCT NAME");
        mfdLabel.setText("PRODUCT MFD");
        expLabel.setText("PRODUCT EXP");
        availablePointsField.setText("");
        sellingPriceField.setText("");
        stockBarcodeField.setText("");
        DefaultTableModel model = (DefaultTableModel)invoiceItemTable.getModel();
        model.setRowCount(0);
        totalLabel.setText("0.00");
        discountFormattedField.setText("0");
        paymentMethodComboBox.setSelectedIndex(0);
        paymentFormattedField.setText("0");
        withdrawPointsCheckBox.setSelected(false);
        balanceLabel.setText("0.00");
        invoiceItemMap.clear();
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
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        employeeEmailLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        invoiceIDField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        customerMobileField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        stockIDField = new javax.swing.JTextField();
        selectStockButton = new javax.swing.JButton();
        qtyFormattedField = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        productNameLabel = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        selectCustomerButton = new javax.swing.JButton();
        customerNameLabel = new javax.swing.JLabel();
        resetButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        expLabel = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        sellingPriceField = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        availablePointsField = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        availableQtyLabel = new javax.swing.JLabel();
        stockBarcodeField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        brandLabel = new javax.swing.JLabel();
        mfdLabel = new javax.swing.JLabel();
        closeLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        invoiceItemTable = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        totalLabel = new javax.swing.JLabel();
        discountFormattedField = new javax.swing.JFormattedTextField();
        balanceLabel = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        saveNPrintButton = new javax.swing.JButton();
        paymentMethodComboBox = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        paymentFormattedField = new javax.swing.JFormattedTextField();
        jLabel27 = new javax.swing.JLabel();
        withdrawPointsCheckBox = new javax.swing.JCheckBox();
        jLabel28 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        storeCreditCheckBox = new javax.swing.JCheckBox();
        storeCreditLabel = new javax.swing.JLabel();

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 153, 255));
        jLabel1.setText("Invoice");

        jLabel2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Employee Email :");

        employeeEmailLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        employeeEmailLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        employeeEmailLabel.setText("EMPLOYEE EMAIL");
        employeeEmailLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        jLabel4.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel4.setText("Invoice ID :");

        invoiceIDField.setEditable(false);
        invoiceIDField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel5.setText("Customer Mobile :");

        customerMobileField.setEditable(false);
        customerMobileField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel6.setText("Stock ID :");

        stockIDField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        selectStockButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        selectStockButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/select-icon.png"))); // NOI18N
        selectStockButton.setText("Select Stock");
        selectStockButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectStockButtonActionPerformed(evt);
            }
        });

        qtyFormattedField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        qtyFormattedField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        qtyFormattedField.setText("0");
        qtyFormattedField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel7.setText("Quantity :");

        jLabel8.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel8.setText("Brand :");

        jLabel10.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel10.setText("MFD :");

        productNameLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        productNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        productNameLabel.setText("PRODUCT NAME");
        productNameLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        jLabel13.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Product :");

        selectCustomerButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        selectCustomerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/select-icon.png"))); // NOI18N
        selectCustomerButton.setText("Select Customer :");
        selectCustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectCustomerButtonActionPerformed(evt);
            }
        });

        customerNameLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        customerNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        customerNameLabel.setText("CUSTOMER NAME");
        customerNameLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        resetButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        resetButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/reset-icon.png"))); // NOI18N
        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        addButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/add_icon.png"))); // NOI18N
        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        expLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        expLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        expLabel.setText("PRODUCT EXP");
        expLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        jLabel11.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("EXP :");

        sellingPriceField.setEditable(false);
        sellingPriceField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        sellingPriceField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        sellingPriceField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        jLabel15.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel15.setText("Sell. Price :");

        availablePointsField.setEditable(false);
        availablePointsField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        availablePointsField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        availablePointsField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        jLabel23.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel23.setText("Avail. Points :");

        jLabel24.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel24.setText("Available Qty :");

        availableQtyLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        availableQtyLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        availableQtyLabel.setText("0");
        availableQtyLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        stockBarcodeField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        stockBarcodeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stockBarcodeFieldActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel3.setText("Barcode :");

        brandLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        brandLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        brandLabel.setText("BRAND NAME");
        brandLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        mfdLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        mfdLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mfdLabel.setText("PRODUCT MFD");
        mfdLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

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
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(closeLabel))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(customerMobileField, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(employeeEmailLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(invoiceIDField, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel6)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(selectCustomerButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(customerNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(availableQtyLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(selectStockButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(stockIDField)
                            .addComponent(qtyFormattedField, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(24, 24, 24)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel23)
                                            .addComponent(jLabel3))
                                        .addGap(5, 5, 5)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(stockBarcodeField, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                                            .addComponent(availablePointsField)))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(62, 62, 62)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(mfdLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                                            .addComponent(brandLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(productNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                    .addComponent(expLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(sellingPriceField)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(addButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(resetButton)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(closeLabel))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(employeeEmailLabel)
                    .addComponent(jLabel6)
                    .addComponent(stockIDField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(productNameLabel)
                    .addComponent(jLabel13)
                    .addComponent(brandLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(invoiceIDField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectStockButton)
                    .addComponent(jLabel10)
                    .addComponent(expLabel)
                    .addComponent(jLabel11)
                    .addComponent(mfdLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(customerMobileField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(qtyFormattedField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(sellingPriceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(availablePointsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(resetButton)
                        .addComponent(addButton)
                        .addComponent(stockBarcodeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(selectCustomerButton)
                        .addComponent(customerNameLabel)
                        .addComponent(jLabel24)
                        .addComponent(availableQtyLabel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        invoiceItemTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Stock ID", "Brand", "Product", "Quantity", "Selling Price", "MFD", "EXP", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        invoiceItemTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(invoiceItemTable);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        totalLabel.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        totalLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        totalLabel.setText("0.00");
        totalLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        discountFormattedField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        discountFormattedField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        discountFormattedField.setText("0");
        discountFormattedField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        discountFormattedField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                discountFormattedFieldKeyReleased(evt);
            }
        });

        balanceLabel.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        balanceLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        balanceLabel.setText("0.00");
        balanceLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        jLabel19.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel19.setText("Total :");

        jLabel20.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel20.setText("Discount :");

        jLabel21.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel21.setText("Balance :");

        saveNPrintButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        saveNPrintButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/print-icon.png"))); // NOI18N
        saveNPrintButton.setText("Save & Print");
        saveNPrintButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveNPrintButtonActionPerformed(evt);
            }
        });

        paymentMethodComboBox.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        paymentMethodComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        paymentMethodComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                paymentMethodComboBoxItemStateChanged(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel26.setText("Payment Method :");

        paymentFormattedField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        paymentFormattedField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        paymentFormattedField.setText("0");
        paymentFormattedField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        paymentFormattedField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                paymentFormattedFieldKeyReleased(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel27.setText("Payment :");

        withdrawPointsCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                withdrawPointsCheckBoxItemStateChanged(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel28.setText("Withdraw Points :");

        jLabel9.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel9.setText("Store Credit :");

        storeCreditCheckBox.setText("-");
        storeCreditCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                storeCreditCheckBoxItemStateChanged(evt);
            }
        });

        storeCreditLabel.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        storeCreditLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        storeCreditLabel.setText("0.00");
        storeCreditLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(saveNPrintButton))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(85, 85, 85)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(totalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel20))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(paymentMethodComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel27))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel28))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(withdrawPointsCheckBox)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel21))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(storeCreditCheckBox)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(storeCreditLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(balanceLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(paymentFormattedField, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                            .addComponent(discountFormattedField))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(discountFormattedField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(totalLabel)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(paymentMethodComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26)
                    .addComponent(paymentFormattedField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(balanceLabel)
                        .addComponent(jLabel21))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(withdrawPointsCheckBox)
                        .addComponent(jLabel28)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveNPrintButton)
                    .addComponent(jLabel9)
                    .addComponent(storeCreditCheckBox)
                    .addComponent(storeCreditLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void selectCustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectCustomerButtonActionPerformed
        SelectCustomer selectCustomer = new SelectCustomer((Frame) SwingUtilities.getWindowAncestor(this), true, this);
        selectCustomer.setVisible(true);
    }//GEN-LAST:event_selectCustomerButtonActionPerformed

    private void selectStockButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectStockButtonActionPerformed
        SelectStock selectStock = new SelectStock((Frame) SwingUtilities.getWindowAncestor(this), true, this);
        selectStock.setVisible(true);
    }//GEN-LAST:event_selectStockButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        String customerMobile = customerMobileField.getText();
        String stockID = stockIDField.getText();
        String stockBarcode = stockBarcodeField.getText();
        String brand = brandLabel.getText();
        String productName = productNameLabel.getText();
        String qty = qtyFormattedField.getText();
        String availableQty = availableQtyLabel.getText();
        String mfd = mfdLabel.getText();
        String exp = expLabel.getText();
        String sellingPrice = sellingPriceField.getText();
        
        if (customerMobile.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a customer", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (stockID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a stock", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (stockBarcode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a stock", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (qty.equals("0")) {
            JOptionPane.showMessageDialog(this, "Please enter a quantity", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (qty.equals("0.00")) {
            JOptionPane.showMessageDialog(this, "Please enter a quantity", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (qty.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a quantity", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (!qty.matches("^(10(\\.00?)?|[0-9](\\.\\d{1,2})?)$")) {
            JOptionPane.showMessageDialog(this, "INVALID quantity", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (Double.parseDouble(qty) > Double.parseDouble(availableQty)) {
            JOptionPane.showMessageDialog(this, "Quantity is above available quantity", "Warning", JOptionPane.WARNING_MESSAGE);
        }else {
            InvoiceItem invoiceItem = new InvoiceItem();
            invoiceItem.setStockID(stockID);
            invoiceItem.setBrand(brand);
            invoiceItem.setProductName(productName);
            invoiceItem.setQty(qty);
            invoiceItem.setMfd(mfd);
            invoiceItem.setExp(exp);
            invoiceItem.setSellingPrice(sellingPrice);
            invoiceItem.setStockBarcode(stockBarcode);

            if (invoiceItemMap.get(stockID) == null) {
                invoiceItemMap.put(stockID, invoiceItem);
            }else {
                InvoiceItem foundInvoiceItem = invoiceItemMap.get(stockID);
                int option = JOptionPane.showConfirmDialog(this, "Do you want to update the quantity of product: " 
                        + productName + "?", "Message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (option == JOptionPane.YES_OPTION) {
                    foundInvoiceItem.setQty(String.valueOf(Double.parseDouble(foundInvoiceItem.getQty()) + Double.parseDouble(qty)));
                }
            }
            loadInvoiceItems();
        }
        
        stockIDField.setText("");
        qtyFormattedField.setText("0");
        availableQtyLabel.setText("0");
        brandLabel.setText("BRAND NAME");
        productNameLabel.setText("PRODUCT NAME");
        mfdLabel.setText("PRODUCT MFD");
        expLabel.setText("PRODUCT EXP");
        sellingPriceField.setText("");
        stockBarcodeField.setText("");
    }//GEN-LAST:event_addButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        reset();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void discountFormattedFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_discountFormattedFieldKeyReleased
        String discount = String.valueOf(discountFormattedField.getText());
        
        if (discountFormattedField.getText() != null && discount.length() > 0) {
            double totalLabelText = Double.parseDouble(totalLabel.getText());
            double discountFieldText = Double.parseDouble(discountFormattedField.getText());
            
            if (totalLabelText < discountFieldText) {
                saveNPrintButton.setEnabled(false);
                balanceLabel.setText("INVALID");
                balanceLabel.setForeground(Color.red);
            }else if (!discountFormattedField.getText().matches("^(0|[1-9]\\d*)?(\\.\\d+)?(?<=\\d)$")) {
                saveNPrintButton.setEnabled(false);
                balanceLabel.setText("INVALID");
                balanceLabel.setForeground(Color.red);
            }else {
                saveNPrintButton.setEnabled(true);
                balanceLabel.setForeground(Color.white);
                calculate();
            }
        }else {
            saveNPrintButton.setEnabled(false);
            balanceLabel.setText("INVALID");
            balanceLabel.setForeground(Color.red);
        }
    }//GEN-LAST:event_discountFormattedFieldKeyReleased

    private void paymentMethodComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_paymentMethodComboBoxItemStateChanged
        calculate();
    }//GEN-LAST:event_paymentMethodComboBoxItemStateChanged

    private void paymentFormattedFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_paymentFormattedFieldKeyReleased
        String paymentFieldText = paymentFormattedField.getText();
        
        if (!paymentFieldText.matches("^(0|[1-9]\\d*)?(\\.\\d+)?(?<=\\d)$")) {
            balanceLabel.setText("INVALID");
            balanceLabel.setForeground(Color.red);
        }else {
            balanceLabel.setForeground(Color.white);
            calculate();
        }
    }//GEN-LAST:event_paymentFormattedFieldKeyReleased

    private void withdrawPointsCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_withdrawPointsCheckBoxItemStateChanged
        int rowCount = invoiceItemTable.getRowCount();
        
        if (rowCount == 0) {
            JOptionPane.showMessageDialog(this, "Please add item(s) to invoice", "Warning", JOptionPane.WARNING_MESSAGE);
        }else {
            calculate();
        }
    }//GEN-LAST:event_withdrawPointsCheckBoxItemStateChanged

    private void saveNPrintButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveNPrintButtonActionPerformed
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `invoice` WHERE `id` = '"+invoiceIDField.getText()+"'");
            
            if (resultSet.next()) {
                JOptionPane.showMessageDialog(this, "This invoice already exists", "Warning", JOptionPane.WARNING_MESSAGE);
            }else {
                String invoiceID = invoiceIDField.getText();
                String customerMobile = customerMobileField.getText();
                String employeeEmail = employeeEmailLabel.getText();
                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String paidAmount = paymentFormattedField.getText();
                String paymentMethodID = paymentMethodMap.get(String.valueOf(paymentMethodComboBox.getSelectedItem()));
                String discount = String.valueOf(discountFormattedField.getText());
                int rowCount = invoiceItemTable.getRowCount();

                if (rowCount == 0) {
                    JOptionPane.showMessageDialog(this, "Please add item(s) to invoice", "Warning", JOptionPane.WARNING_MESSAGE);
                }else {
                    MySQL.executeIUD("INSERT INTO `invoice` VALUES('"+invoiceID+"', '"+customerMobile+"', '"+employeeEmail+"', "
                        + "'"+dateTime+"', '"+paidAmount+"', '"+paymentMethodID+"', '"+discount+"', 1)");

                    for (InvoiceItem invoiceItem : invoiceItemMap.values()) {
                        MySQL.executeIUD("INSERT INTO `invoice_item`(`stock_id`, `qty`, `invoice_id`) "
                                + "VALUES('"+invoiceItem.getStockID()+"', '"+invoiceItem.getQty()+"', '"+invoiceID+"')");
                        MySQL.executeIUD("UPDATE `stock` SET `qty` = `qty`-'"+invoiceItem.getQty()+"' "
                                + "WHERE `id` = '"+invoiceItem.getStockID()+"'");
                    }

                    double points = Double.parseDouble(totalLabel.getText()) / 100;

                    if (withdrawPoints) {
                        newPoints += points;
                        MySQL.executeIUD("UPDATE `customer` SET `points` = '"+newPoints+"' "
                                + "WHERE `mobile` = '"+customerMobile+"'");
                    }else {
                        MySQL.executeIUD("UPDATE `customer` SET `points` = `points`+'"+points+"' "
                                + "WHERE `mobile` = '"+customerMobile+"'");
                    }
                    
                    if (storeCredits) {
                        MySQL.executeIUD("UPDATE `customer` SET `store_credit` = '"+newCredits+"' "
                                + "WHERE `mobile` = '"+customerMobile+"'");
                    }

                    InputStream path = this.getClass().getResourceAsStream("/reports/gh_invoice.jasper");
                    HashMap<String, Object> parameters = new HashMap<>();
                    parameters.put("Parameter1", invoiceIDField.getText());
                    parameters.put("Parameter2", employeeEmailLabel.getText());
                    parameters.put("Parameter3", customerMobileField.getText());
                    parameters.put("Parameter4", dateTime);
                    parameters.put("Parameter5", totalLabel.getText());
                    parameters.put("Parameter6", discountFormattedField.getText());
                    parameters.put("Parameter7", String.valueOf(paymentMethodComboBox.getSelectedItem()));
                    parameters.put("Parameter8", paymentFormattedField.getText());
                    parameters.put("Parameter9", balanceLabel.getText());
                    
                    String appDir = new File("").getAbsolutePath(); // Get the application's directory
                    String reportsFolder = appDir + File.separator + "ExportedReports"; // Main folder path
                    // Create the main "ExportedReports" folder if it doesn't exist
                    File mainDirectory = new File(reportsFolder);
                    if (!mainDirectory.exists()) {
                        mainDirectory.mkdirs();
                    }
                    // Create subfolder for "Invoice Reports" if it doesn't exist
                    String invoiceReportsFolder = reportsFolder + File.separator + "Invoice Reports";
                    File invoiceDirectory = new File(invoiceReportsFolder);
                    if (!invoiceDirectory.exists()) {
                        invoiceDirectory.mkdirs();
                    }
                    // Path to export the PDF file (inside "Invoice Reports" subfolder)
                    String outputPath = invoiceReportsFolder + File.separator + "Invoice_report_" + invoiceID + ".pdf";

                    JRTableModelDataSource dataSource = new JRTableModelDataSource(invoiceItemTable.getModel());
                    JasperPrint report = JasperFillManager.fillReport(path, parameters, dataSource);
                    JasperViewer.viewReport(report, false);
                    JasperPrintManager.printReport(report, false);
                    JasperExportManager.exportReportToPdfFile(report, outputPath);
                    reset();
                    logger.log(Level.INFO, "Successfully generated invoice: " + invoiceID);
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }//GEN-LAST:event_saveNPrintButtonActionPerformed

    private void stockBarcodeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stockBarcodeFieldActionPerformed
        String barcode = stockBarcodeField.getText();
        
        try {
            if (barcode.length() == 12) {
                ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `stock` "
                        + "INNER JOIN `product` ON `stock`.`product_id` = `product`.`id` "
                        + "INNER JOIN `brand` ON `product`.`brand_id` = `brand`.`id` "
                        + "WHERE `stock`.`barcode` = '"+barcode+"'");
                
                if (resultSet.next()) {
                    stockIDField.setText(resultSet.getString("stock.id"));
                    availableQtyLabel.setText(resultSet.getString("stock.qty"));
                    brandLabel.setText(resultSet.getString("brand.name"));
                    productNameLabel.setText(resultSet.getString("product.name"));
                    mfdLabel.setText(resultSet.getString("stock.mfd"));
                    expLabel.setText(resultSet.getString("stock.exp"));
                    sellingPriceField.setText(resultSet.getString("stock.price"));
                }else {
                    JOptionPane.showMessageDialog(this, "No product found", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }else {
                JOptionPane.showMessageDialog(this, "Invalid barcode", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }//GEN-LAST:event_stockBarcodeFieldActionPerformed

    private void closeLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closeLabelMouseClicked
        this.home.removeInvoice();
    }//GEN-LAST:event_closeLabelMouseClicked

    private void storeCreditCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_storeCreditCheckBoxItemStateChanged
        int rowCount = invoiceItemTable.getRowCount();
        
        if (rowCount == 0) {
            JOptionPane.showMessageDialog(this, "Please add item(s) to invoice", "Warning", JOptionPane.WARNING_MESSAGE);
        }else {
            calculate();
        }
    }//GEN-LAST:event_storeCreditCheckBoxItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JTextField availablePointsField;
    private javax.swing.JLabel availableQtyLabel;
    private javax.swing.JLabel balanceLabel;
    private javax.swing.JLabel brandLabel;
    private javax.swing.JLabel closeLabel;
    private javax.swing.JTextField customerMobileField;
    private javax.swing.JLabel customerNameLabel;
    private javax.swing.JFormattedTextField discountFormattedField;
    private javax.swing.JLabel employeeEmailLabel;
    private javax.swing.JLabel expLabel;
    private javax.swing.JTextField invoiceIDField;
    private javax.swing.JTable invoiceItemTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel mfdLabel;
    private javax.swing.JFormattedTextField paymentFormattedField;
    private javax.swing.JComboBox<String> paymentMethodComboBox;
    private javax.swing.JLabel productNameLabel;
    private javax.swing.JFormattedTextField qtyFormattedField;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton saveNPrintButton;
    private javax.swing.JButton selectCustomerButton;
    private javax.swing.JButton selectStockButton;
    private javax.swing.JTextField sellingPriceField;
    private javax.swing.JTextField stockBarcodeField;
    private javax.swing.JTextField stockIDField;
    private javax.swing.JCheckBox storeCreditCheckBox;
    private javax.swing.JLabel storeCreditLabel;
    private javax.swing.JLabel totalLabel;
    private javax.swing.JCheckBox withdrawPointsCheckBox;
    // End of variables declaration//GEN-END:variables
}
