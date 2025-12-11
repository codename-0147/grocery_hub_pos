/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui;

import static gui.SignIn.logger;
import model.GmailOAuthService;
import java.awt.Frame;
import java.io.File;
import java.io.InputStream;
import model.MySQL;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.ButtonModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.CustomerReturnItem;
import model.SupplierReturnItem;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JasperViewer;
import java.util.logging.Level;

/**
 *
 * @author barth
 */
public class Returns extends javax.swing.JPanel {
    private Home home;
    HashMap<String, String> customerReturnReasonMap = new HashMap<>();
    HashMap<String, String> customerReturnTypeMap = new HashMap<>();
    HashMap<String, String> customerReturnMethodMap = new HashMap<>();
    HashMap<String, CustomerReturnItem> customerReturnItemMap = new HashMap<>();
    HashMap<String, String> invoiceStatusMap = new HashMap<>();
    HashMap<String, String> suppliersMap = new HashMap<>();
    HashMap<String, String> supplierReturnReasonMap = new HashMap<>();
    HashMap<String, SupplierReturnItem> supplierReturnItemMap = new HashMap<>();

    /**
     * Creates new form Overview
     */
    public Returns(Home home) {
        initComponents();
        this.home = home;
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        invoiceItemTable.setDefaultRenderer(Object.class, renderer);
        customerReturnItemTable.setDefaultRenderer(Object.class, renderer);
        supReturnInvoiceTable.setDefaultRenderer(Object.class, renderer);
        employeeEmailLabel1.setText(SignIn.getEmployeeEmail());
        employeeEmailLabel2.setText(SignIn.getEmployeeEmail());
        generateReturnReceiptID();
        generateReturnInvoiceID();
        loadCustomerReturnReasons();
        loadCustomerReturnTypes();
        loadCustomerReturnMethods();
        loadInvoiceStatuses();
        loadSuppliers();
        loadSupplierReturnReasons();
        loadSupplierReturnInvoices();
        checkGuiAccess(SignIn.getEmployeeEmail());
    }
    
    private void checkGuiAccess(String email) {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `employee` INNER JOIN `employee_type` "
                    + "ON `employee`.`employee_type_id` = `employee_type`.`id` "
                    + "WHERE `employee`.`email` = '"+email+"' AND `employee_type`.`name` = 'Cashier'");
            
            if (resultSet.next()) {
                jTabbedPane1.setEnabledAt(1, false);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }

    private double total = 0;
    
    private void generateReturnReceiptID() {
        long id = System.currentTimeMillis();
        receiptIDField.setText(String.valueOf(id));
    }
    
    private void generateReturnInvoiceID() {
        long id = System.currentTimeMillis();
        returnInvoiceIDField.setText(String.valueOf(id));
    }
    
    public JLabel getinvoiceEmpEmailLabel() {
        return invoiceEmpEmailLabel;
    }
    
    public JLabel getinvoiceIDLabel() {
        return invoiceIDLabel;
    }
    
    public JLabel getcustomerMobileLabel() {
        return customerMobileLabel;
    }
    
    public JLabel getinvoiceDateLabel() {
        return invoiceDateLabel;
    }
    
    public JLabel getpaidAmountLabel() {
        return paidAmountLabel;
    }
    
    public JTable getinvoiceItemTable() {
        return invoiceItemTable;
    }
    
    public JLabel getreturningStockIDLabel() {
        return returningStockIDLabel;
    }
    
    public JTextField getstockBarcodeField() {
        return stockBarcodeField;
    }
    
    public JLabel getsupplierEmailLabel() {
        return supplierEmailLabel;
    }
    
    public JLabel getproductNameLabel2() {
        return productNameLabel2;
    }
    
    public JLabel getquantityLabel() {
        return quantityLabel;
    }
    
    public JLabel getmfdLabel() {
        return mfdLabel;
    }
    
    public JLabel getexpLabel() {
        return expLabel;
    }
    
    public JLabel getbuyingPriceLabel() {
        return buyingPriceLabel;
    }
    
    private double returnTotal = 0;
    
    private void loadCustomerReturnReasons() {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `customer_return_reason`");
            Vector<String> vector = new Vector<>();
            vector.add("Select");
            
            while (resultSet.next()) {
                vector.add(resultSet.getString("name"));
                customerReturnReasonMap.put(resultSet.getString("name"), resultSet.getString("id"));
            }
            
            DefaultComboBoxModel model = new DefaultComboBoxModel(vector);
            reasonComboBox1.setModel(model);
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }
    
    private void loadCustomerReturnTypes() {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `customer_return_type`");
            
            while (resultSet.next()) {
                customerReturnTypeMap.put(resultSet.getString("name"), resultSet.getString("id"));
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }
    
    private void loadCustomerReturnMethods() {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `customer_return_method`");
            
            while (resultSet.next()) {
                customerReturnMethodMap.put(resultSet.getString("name"), resultSet.getString("id"));
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }
    
    private void loadCustomerReturnItems() {
        DefaultTableModel model = (DefaultTableModel) customerReturnItemTable.getModel();
        model.setRowCount(0);
        returnTotal = 0;
        
        for (CustomerReturnItem customerReturnItem : customerReturnItemMap.values()) {
            Vector<String> vector = new Vector<>();
            vector.add(customerReturnItem.getStockID());
            vector.add(customerReturnItem.getProductName());
            vector.add(customerReturnItem.getQty());
            vector.add(customerReturnItem.getPrice());
            double itemTotal = Double.parseDouble(customerReturnItem.getQty()) * Double.parseDouble(customerReturnItem.getPrice());
            returnTotal += itemTotal;
            vector.add(String.valueOf(itemTotal));
            model.addRow(vector);
        }
        
        returnTotalLabel.setText(String.valueOf(returnTotal));
    }
    
    private void loadInvoiceStatuses() {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `invoice_status`");
                    
            while (resultSet.next()) {
                invoiceStatusMap.put(resultSet.getString("name"), resultSet.getString("id"));
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }
    
    private double getAlreadyReturnedQty(String invoiceId, String stockId) {
        double returnedQty = 0.0;
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT IFNULL(SUM(`qty`), 0) AS total_returned "
                + "FROM `customer_return_item` INNER JOIN `customer_return_receipt` "
                + "ON `customer_return_item`.`customer_return_receipt_id` = `customer_return_receipt`.`id` "
                + "INNER JOIN `invoice` ON `customer_return_receipt`.`invoice_id` = `invoice`.`id` "
                + "WHERE `invoice`.`id` = '"+invoiceId+"' AND `customer_return_item`.`stock_id` = '"+stockId+"'");
            if (resultSet.next()) {
                returnedQty = resultSet.getDouble("total_returned");
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Failed to get returned qty", e);
        }
        return returnedQty;
    }
    
    private void resetCustomerReturns() {
        invoiceEmpEmailLabel.setText("EMP. EMAIL");
        invoiceIDLabel.setText("ID");
        customerMobileLabel.setText("C. MOBILE");
        invoiceDateLabel.setText("DATE");
        paidAmountLabel.setText("VALUE");
        DefaultTableModel model = (DefaultTableModel) invoiceItemTable.getModel();
        model.setRowCount(0);
        generateReturnReceiptID();
        buttonGroup1.clearSelection();
        buttonGroup2.clearSelection();
        qtyFormattedField1.setText("0");
        productNameLabel1.setText("PRODUCT NAME");
        productPriceLabel.setText("0.00");
        reasonComboBox1.setSelectedIndex(0);
        returnTotalLabel.setText("0.00");
        DefaultTableModel model1 = (DefaultTableModel) customerReturnItemTable.getModel();
        model1.setRowCount(0);
        addButton1.setEnabled(true);
        typeFullRadioButton.setEnabled(true);
        typePartialRadioButton.setEnabled(true);
        customerReturnItemMap.clear();
    }
    
    private void loadSuppliers() {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `supplier`");
            Vector<String> vector = new Vector<>();
            vector.add("Select");
            
            while (resultSet.next()) {
                vector.add(resultSet.getString("first_name") + " " + resultSet.getString("last_name"));
                suppliersMap.put(resultSet.getString("email"), resultSet.getString("mobile"));
            }
            
            DefaultComboBoxModel model = new DefaultComboBoxModel(vector);
            supplierComboBox.setModel(model);
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }
    
    private void loadSupplierReturnReasons() {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `supplier_return_reason`");
            Vector<String> vector = new Vector<>();
            vector.add("Select");
            
            while (resultSet.next()) {
                vector.add(resultSet.getString("name"));
                supplierReturnReasonMap.put(resultSet.getString("name"), resultSet.getString("id"));
            }
            
            DefaultComboBoxModel model = new DefaultComboBoxModel(vector);
            reasonComboBox2.setModel(model);
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }
    
    private void loadSupplierReturnItems() {
        DefaultTableModel model = (DefaultTableModel) supplierReturnItemTable.getModel();
        model.setRowCount(0);
        total = 0;
        
        for (SupplierReturnItem supplierReturnItem : supplierReturnItemMap.values()) {
            Vector<String> vector = new Vector<>();
            vector.add(supplierReturnItem.getProductName());
            vector.add(supplierReturnItem.getMfd());
            vector.add(supplierReturnItem.getExp());
            vector.add(supplierReturnItem.getBuyingPrice());
            vector.add(supplierReturnItem.getQty());
            double itemTotal = Double.parseDouble(supplierReturnItem.getQty()) * 
                    Double.parseDouble(supplierReturnItem.getBuyingPrice());
            total += itemTotal;
            vector.add(String.valueOf(itemTotal));
            model.addRow(vector);
        }
        
        totalLabel.setText(String.valueOf(total));
    }
    
    private void loadSupplierReturnInvoices(){
        try {
            String query = "SELECT * FROM `return_invoice` INNER JOIN `supplier` "
                    + "ON `return_invoice`.`supplier_mobile` = `supplier`.`mobile` INNER JOIN `supplier_return_reason` "
                    + "ON `return_invoice`.`supplier_return_reason_id` = `supplier_return_reason`.`id` "
                    + "INNER JOIN `return_invoice_status` "
                    + "ON `return_invoice`.`return_invoice_status_id` = `return_invoice_status`.`id` WHERE ";
            
            Date from = null;
            Date to = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            
            if (fromDateChooser.getDate() != null) {
                from = fromDateChooser.getDate();
                query += "`return_invoice`.`date` > '"+format.format(from)+"' AND ";
            }
            
            if (toDateChooser.getDate() != null) {
                to = toDateChooser.getDate();
                query += "`return_invoice`.`date` < '"+format.format(to)+"' AND ";
            }
            
            String sort = String.valueOf(sortByComboBox.getSelectedItem());
            
            if (sort.equals("Date ASC")) {
                query += "ORDER BY ";
                query = query.replace("WHERE ORDER BY ", "ORDER BY `return_invoice`.`date` ASC");
                query = query.replace("AND ORDER BY ", "ORDER BY `return_invoice`.`date` ASC");
            }else if (sort.equals("Date DESC")) {
                query += "ORDER BY ";
                query = query.replace("WHERE ORDER BY ", "ORDER BY `return_invoice`.`date` DESC");
                query = query.replace("AND ORDER BY ", "ORDER BY `return_invoice`.`date` DESC");
            }else if (sort.equals("Reason ASC")) {
                query += "ORDER BY ";
                query = query.replace("WHERE ORDER BY ", 
                        "ORDER BY `supplier_return_reason`.`name` ASC, `return_invoice`.`date` DESC");
                query = query.replace("AND ORDER BY ", 
                        "ORDER BY `supplier_return_reason`.`name` ASC, `return_invoice`.`date` DESC");
            }else if (sort.equals("Reason DESC")) {
                query += "ORDER BY ";
                query = query.replace("WHERE ORDER BY ", 
                        "ORDER BY `supplier_return_reason`.`name` DESC, `return_invoice`.`date` DESC");
                query = query.replace("AND ORDER BY ", 
                        "ORDER BY `supplier_return_reason`.`name` DESC, `return_invoice`.`date` DESC");
            }else if (sort.equals("Supplier ASC")) {
                query += "ORDER BY ";
                query = query.replace("WHERE ORDER BY ", 
                        "ORDER BY `supplier`.`first_name` ASC, `return_invoice`.`date` DESC");
                query = query.replace("AND ORDER BY ", 
                        "ORDER BY `supplier`.`first_name` ASC, `return_invoice`.`date` DESC");
            }else if (sort.equals("Supplier DESC")) {
                query += "ORDER BY ";
                query = query.replace("WHERE ORDER BY ", 
                        "ORDER BY `supplier`.`first_name` DESC, `return_invoice`.`date` DESC");
                query = query.replace("AND ORDER BY ", 
                        "ORDER BY `supplier`.`first_name` DESC, `return_invoice`.`date` DESC");
            }else if (sort.equals("Pending")) {
                query += "`return_invoice_status`.`name` = 'Pending' ORDER BY `return_invoice`.`date` DESC";
            }else if (sort.equals("Approved")){
                query += "`return_invoice_status`.`name` = 'Approved' ORDER BY `return_invoice`.`date` DESC";
            }
            
            ResultSet resultSet = MySQL.executeSearch(query);
            DefaultTableModel model = (DefaultTableModel)supReturnInvoiceTable.getModel();
            model.setRowCount(0);
            
            while (resultSet.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(resultSet.getString("return_invoice.id"));
                vector.add(resultSet.getString("supplier_return_reason.name"));
                vector.add(resultSet.getString("supplier.first_name") + " " + resultSet.getString("supplier.last_name"));
                vector.add(resultSet.getString("return_invoice.date"));
                vector.add(resultSet.getString("return_invoice_status.name"));
                model.addRow(vector);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }
    
    private void resetSupplierReturns() {
        returnInvoiceIDField.setText("");
        returningStockIDLabel.setText("R. STOCK ID");
        stockBarcodeField.setText("");
        supplierComboBox.setSelectedIndex(0);
        supplierEmailLabel.setText("SUP. EMAIL");
        productNameLabel2.setText("PRODUCT");
        quantityLabel.setText("QUANTITY");
        reasonComboBox2.setSelectedIndex(0);
        mfdLabel.setText("MFD");
        expLabel.setText("EXP");
        buyingPriceLabel.setText("BUYING PRICE");
        totalLabel.setText("0.00");
        DefaultTableModel model = (DefaultTableModel)supplierReturnItemTable.getModel();
        model.setRowCount(0);
        generateReturnInvoiceID();
        supplierReturnItemMap.clear();
        supReturnInvoiceTable.clearSelection();
        fromDateChooser.setDate(null);
        toDateChooser.setDate(null);
        sortByComboBox.setSelectedIndex(0);
        loadSupplierReturnInvoices();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        customerReturnsPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        invoiceIDLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        invoiceDateLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        paidAmountLabel = new javax.swing.JLabel();
        selectInvoiceButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        invoiceItemTable = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        addButton1 = new javax.swing.JButton();
        confirmButton = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        productPriceLabel = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        productNameLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        customerReturnItemTable = new javax.swing.JTable();
        jLabel20 = new javax.swing.JLabel();
        methodRefundRadioButton = new javax.swing.JRadioButton();
        methodCreditRadioButton = new javax.swing.JRadioButton();
        invoiceEmpEmailLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        closeLabel1 = new javax.swing.JLabel();
        customerMobileLabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        employeeEmailLabel1 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        receiptIDField = new javax.swing.JTextField();
        qtyFormattedField1 = new javax.swing.JFormattedTextField();
        jLabel25 = new javax.swing.JLabel();
        reasonComboBox1 = new javax.swing.JComboBox<>();
        returnTotalLabel = new javax.swing.JLabel();
        typeFullRadioButton = new javax.swing.JRadioButton();
        typePartialRadioButton = new javax.swing.JRadioButton();
        methodExchangeRadioButton = new javax.swing.JRadioButton();
        resetButton1 = new javax.swing.JButton();
        supplierReturnsPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        employeeEmailLabel2 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        returningStockIDLabel = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        stockBarcodeField = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        supplierEmailLabel = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        productNameLabel2 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        quantityLabel = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        reasonComboBox2 = new javax.swing.JComboBox<>();
        submitButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        supReturnInvoiceTable = new javax.swing.JTable();
        jLabel15 = new javax.swing.JLabel();
        returnInvoiceIDField = new javax.swing.JTextField();
        selectReturnStockButton = new javax.swing.JButton();
        closeLabel2 = new javax.swing.JLabel();
        buyingPriceLabel = new javax.swing.JLabel();
        expLabel = new javax.swing.JLabel();
        mfdLabel = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        sortByComboBox = new javax.swing.JComboBox<>();
        resetButton2 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        supplierReturnItemTable = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        addButton2 = new javax.swing.JButton();
        supplierComboBox = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        fromDateChooser = new com.toedter.calendar.JDateChooser();
        jLabel27 = new javax.swing.JLabel();
        toDateChooser = new com.toedter.calendar.JDateChooser();
        findButton = new javax.swing.JButton();
        totalLabel = new javax.swing.JLabel();

        customerReturnsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 153, 255));
        jLabel3.setText("Customer Returns");

        jLabel1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel1.setText("Invoice ID :");

        invoiceIDLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        invoiceIDLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        invoiceIDLabel.setText("ID");
        invoiceIDLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        jLabel5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel5.setText("C. Mobile :");

        jLabel7.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel7.setText("Date :");

        invoiceDateLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        invoiceDateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        invoiceDateLabel.setText("DATE");
        invoiceDateLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        jLabel9.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel9.setText("Paid :");

        paidAmountLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        paidAmountLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        paidAmountLabel.setText("VALUE");
        paidAmountLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        selectInvoiceButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        selectInvoiceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/select-icon.png"))); // NOI18N
        selectInvoiceButton.setText("Select Invoice");
        selectInvoiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectInvoiceButtonActionPerformed(evt);
            }
        });

        invoiceItemTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Stock ID", "Product Name", "Qty", "Price", "MFD", "EXP"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        invoiceItemTable.getTableHeader().setReorderingAllowed(false);
        invoiceItemTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                invoiceItemTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(invoiceItemTable);

        jLabel11.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel11.setText("Type :");

        jLabel13.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel13.setText("Qty :");

        jLabel14.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel14.setText("R. Total :");

        addButton1.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        addButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/add_icon.png"))); // NOI18N
        addButton1.setText("Add");
        addButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButton1ActionPerformed(evt);
            }
        });

        confirmButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        confirmButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/confirm-icon.png"))); // NOI18N
        confirmButton.setText("Confirm");
        confirmButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmButtonActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel16.setText("Price :");

        productPriceLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        productPriceLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        productPriceLabel.setText("0.00");
        productPriceLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        jLabel18.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel18.setText("Product :");

        productNameLabel1.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        productNameLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        productNameLabel1.setText("PRODUCT NAME");
        productNameLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        customerReturnItemTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Stock ID", "Product Name", "Qty", "Price", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        customerReturnItemTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(customerReturnItemTable);

        jLabel20.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel20.setText("Method :");

        buttonGroup2.add(methodRefundRadioButton);
        methodRefundRadioButton.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        methodRefundRadioButton.setText("Refund");
        methodRefundRadioButton.setActionCommand("Refund");

        buttonGroup2.add(methodCreditRadioButton);
        methodCreditRadioButton.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        methodCreditRadioButton.setText("Credit");
        methodCreditRadioButton.setActionCommand("Store Credit");

        invoiceEmpEmailLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        invoiceEmpEmailLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        invoiceEmpEmailLabel.setText("EMP. EMAIL");
        invoiceEmpEmailLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        jLabel6.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel6.setText("E. Email :");

        closeLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/close-icon.png"))); // NOI18N
        closeLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                closeLabel1MouseClicked(evt);
            }
        });

        customerMobileLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        customerMobileLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        customerMobileLabel.setText("C. MOBILE");
        customerMobileLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        jLabel8.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel8.setText("E. Email :");

        employeeEmailLabel1.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        employeeEmailLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        employeeEmailLabel1.setText("EMP. EMAIL");
        employeeEmailLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        jLabel21.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel21.setText("Receipt ID :");

        receiptIDField.setEditable(false);
        receiptIDField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        qtyFormattedField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        qtyFormattedField1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        qtyFormattedField1.setText("0");
        qtyFormattedField1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel25.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel25.setText("Reason :");

        reasonComboBox1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        reasonComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        returnTotalLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        returnTotalLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        returnTotalLabel.setText("0.00");
        returnTotalLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        buttonGroup1.add(typeFullRadioButton);
        typeFullRadioButton.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        typeFullRadioButton.setText("Full");
        typeFullRadioButton.setActionCommand("Full");
        typeFullRadioButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                typeFullRadioButtonMouseClicked(evt);
            }
        });

        buttonGroup1.add(typePartialRadioButton);
        typePartialRadioButton.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        typePartialRadioButton.setText("Partial");
        typePartialRadioButton.setActionCommand("Partial");
        typePartialRadioButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                typePartialRadioButtonMouseClicked(evt);
            }
        });

        buttonGroup2.add(methodExchangeRadioButton);
        methodExchangeRadioButton.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        methodExchangeRadioButton.setText("Exchange");
        methodExchangeRadioButton.setActionCommand("Exchange");

        resetButton1.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        resetButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/reset-icon.png"))); // NOI18N
        resetButton1.setText("Reset");
        resetButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout customerReturnsPanelLayout = new javax.swing.GroupLayout(customerReturnsPanel);
        customerReturnsPanel.setLayout(customerReturnsPanelLayout);
        customerReturnsPanelLayout.setHorizontalGroup(
            customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customerReturnsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(customerReturnsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(closeLabel1))
                    .addComponent(jScrollPane2)
                    .addGroup(customerReturnsPanelLayout.createSequentialGroup()
                        .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(customerReturnsPanelLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel14))
                            .addGroup(customerReturnsPanelLayout.createSequentialGroup()
                                .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(employeeEmailLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(customerReturnsPanelLayout.createSequentialGroup()
                                        .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel20)
                                            .addComponent(jLabel8)
                                            .addComponent(jLabel21)
                                            .addComponent(jLabel11)
                                            .addComponent(jLabel13))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(qtyFormattedField1, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                                                .addComponent(receiptIDField)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, customerReturnsPanelLayout.createSequentialGroup()
                                                    .addComponent(methodExchangeRadioButton)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(methodRefundRadioButton)))
                                            .addGroup(customerReturnsPanelLayout.createSequentialGroup()
                                                .addComponent(typeFullRadioButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(typePartialRadioButton)))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(customerReturnsPanelLayout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING)))
                                    .addGroup(customerReturnsPanelLayout.createSequentialGroup()
                                        .addComponent(methodCreditRadioButton)
                                        .addGap(0, 0, Short.MAX_VALUE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(reasonComboBox1, javax.swing.GroupLayout.Alignment.LEADING, 0, 187, Short.MAX_VALUE)
                            .addComponent(productPriceLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(productNameLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(returnTotalLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(addButton1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(confirmButton, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(resetButton1, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(customerReturnsPanelLayout.createSequentialGroup()
                        .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(customerReturnsPanelLayout.createSequentialGroup()
                                    .addGap(38, 38, 38)
                                    .addComponent(jLabel9)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(paidAmountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(customerReturnsPanelLayout.createSequentialGroup()
                                    .addGap(34, 34, 34)
                                    .addComponent(jLabel7)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(invoiceDateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(customerReturnsPanelLayout.createSequentialGroup()
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(invoiceIDLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(customerReturnsPanelLayout.createSequentialGroup()
                                    .addGap(4, 4, 4)
                                    .addComponent(jLabel5)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(customerMobileLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(customerReturnsPanelLayout.createSequentialGroup()
                                    .addGap(17, 17, 17)
                                    .addComponent(jLabel6)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(invoiceEmpEmailLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(selectInvoiceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 544, Short.MAX_VALUE)))
                .addContainerGap())
        );
        customerReturnsPanelLayout.setVerticalGroup(
            customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customerReturnsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(closeLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(customerReturnsPanelLayout.createSequentialGroup()
                        .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(invoiceEmpEmailLabel)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(invoiceIDLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(customerMobileLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(invoiceDateLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(paidAmountLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectInvoiceButton))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(employeeEmailLabel1)
                    .addComponent(productNameLabel1)
                    .addComponent(jLabel18)
                    .addComponent(addButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(productPriceLabel)
                    .addComponent(receiptIDField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(resetButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(typeFullRadioButton)
                        .addComponent(typePartialRadioButton))
                    .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(reasonComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel25)
                        .addComponent(confirmButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(returnTotalLabel)
                    .addComponent(jLabel13)
                    .addComponent(qtyFormattedField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(customerReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(methodRefundRadioButton)
                    .addComponent(methodCreditRadioButton)
                    .addComponent(methodExchangeRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Customer", customerReturnsPanel);

        supplierReturnsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel4.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 153, 255));
        jLabel4.setText("Supplier Returns");

        jLabel2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel2.setText("E. Email :");

        employeeEmailLabel2.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        employeeEmailLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        employeeEmailLabel2.setText("EMP. EMAIL");
        employeeEmailLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        jLabel10.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel10.setText("R. I. ID :");

        returningStockIDLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        returningStockIDLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        returningStockIDLabel.setText("R. STOCK ID");
        returningStockIDLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        jLabel17.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel17.setText("Barcode :");

        stockBarcodeField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        stockBarcodeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stockBarcodeFieldActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel19.setText("Sup. Name :");

        jLabel22.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel22.setText("Sup. Email :");

        supplierEmailLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        supplierEmailLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        supplierEmailLabel.setText("SUP. EMAIL");
        supplierEmailLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        jLabel24.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel24.setText("Product :");

        productNameLabel2.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        productNameLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        productNameLabel2.setText("PRODUCT");
        productNameLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        jLabel26.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel26.setText("Quantity :");

        quantityLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        quantityLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        quantityLabel.setText("QUANTITY");
        quantityLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        jLabel28.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel28.setText("MFD :");

        jLabel30.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel30.setText("EXP :");

        jLabel32.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel32.setText("Price :");

        jLabel34.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel34.setText("Total :");

        jLabel35.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel35.setText("Reason :");

        reasonComboBox2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        reasonComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        submitButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        submitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/submit-icon.png"))); // NOI18N
        submitButton.setText("Submit Request");
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        supReturnInvoiceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "R. I. ID", "Reason", "Supplier", "Date", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        supReturnInvoiceTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(supReturnInvoiceTable);

        jLabel15.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel15.setText("R. Stock ID :");

        returnInvoiceIDField.setEditable(false);
        returnInvoiceIDField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        selectReturnStockButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        selectReturnStockButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/select-icon.png"))); // NOI18N
        selectReturnStockButton.setText("Select Return Stock");
        selectReturnStockButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectReturnStockButtonActionPerformed(evt);
            }
        });

        closeLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/close-icon.png"))); // NOI18N
        closeLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                closeLabel2MouseClicked(evt);
            }
        });

        buyingPriceLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        buyingPriceLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        buyingPriceLabel.setText("BUYING PRICE");
        buyingPriceLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        expLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        expLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        expLabel.setText("EXP");
        expLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        mfdLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        mfdLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mfdLabel.setText("MFD");
        mfdLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        jLabel37.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel37.setText("Sort By :");

        sortByComboBox.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        sortByComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Date DESC", "Date ASC", "Reason ASC", "Reason DESC", "Supplier ASC", "Supplier DESC", "Pending", "Approved" }));

        resetButton2.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        resetButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/reset-icon.png"))); // NOI18N
        resetButton2.setText("Reset");
        resetButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButton2ActionPerformed(evt);
            }
        });

        supplierReturnItemTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product Name", "MFD", "EXP", "Price", "Quantity", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        supplierReturnItemTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(supplierReturnItemTable);

        jLabel12.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(51, 153, 255));
        jLabel12.setText("Return Invoices");

        addButton2.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        addButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/add_icon.png"))); // NOI18N
        addButton2.setText("Add");
        addButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButton2ActionPerformed(evt);
            }
        });

        supplierComboBox.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        supplierComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        supplierComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                supplierComboBoxItemStateChanged(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel23.setText("From");

        fromDateChooser.setDateFormatString("yyyy-MM-dd");

        jLabel27.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel27.setText("To");

        toDateChooser.setDateFormatString("yyyy-MM-dd");

        findButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        findButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/search-icon.png"))); // NOI18N
        findButton.setText("Find");
        findButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findButtonActionPerformed(evt);
            }
        });

        totalLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        totalLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        totalLabel.setText("0.00");
        totalLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        javax.swing.GroupLayout supplierReturnsPanelLayout = new javax.swing.GroupLayout(supplierReturnsPanel);
        supplierReturnsPanel.setLayout(supplierReturnsPanelLayout);
        supplierReturnsPanelLayout.setHorizontalGroup(
            supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(supplierReturnsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(supplierReturnsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(closeLabel2))
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(supplierReturnsPanelLayout.createSequentialGroup()
                        .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(selectReturnStockButton)
                            .addGroup(supplierReturnsPanelLayout.createSequentialGroup()
                                .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel15)
                                        .addComponent(jLabel17))
                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(employeeEmailLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(returnInvoiceIDField, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(returningStockIDLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(stockBarcodeField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(supplierReturnsPanelLayout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(reasonComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 29, Short.MAX_VALUE)
                                .addComponent(addButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(submitButton))
                            .addGroup(supplierReturnsPanelLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(supplierReturnsPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel19)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(supplierComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(supplierReturnsPanelLayout.createSequentialGroup()
                                        .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel24)
                                            .addComponent(jLabel22)
                                            .addComponent(jLabel26))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(supplierEmailLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(productNameLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(quantityLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(18, 18, 18)
                                .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel32)
                                    .addComponent(jLabel34)
                                    .addComponent(jLabel30)
                                    .addComponent(jLabel28))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(buyingPriceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(expLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(mfdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(totalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(supplierReturnsPanelLayout.createSequentialGroup()
                        .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addGroup(supplierReturnsPanelLayout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fromDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(toDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel37)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sortByComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(findButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(resetButton2)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        supplierReturnsPanelLayout.setVerticalGroup(
            supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(supplierReturnsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(closeLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(supplierReturnsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(8, 8, 8)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel34)
                            .addComponent(jLabel17)
                            .addComponent(stockBarcodeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26)
                            .addComponent(quantityLabel)
                            .addComponent(totalLabel)))
                    .addGroup(supplierReturnsPanelLayout.createSequentialGroup()
                        .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(employeeEmailLabel2)
                            .addComponent(jLabel2)
                            .addComponent(jLabel19)
                            .addComponent(supplierComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28)
                            .addComponent(mfdLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(returnInvoiceIDField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22)
                            .addComponent(supplierEmailLabel)
                            .addComponent(jLabel30)
                            .addComponent(expLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(returningStockIDLabel)
                            .addComponent(jLabel24)
                            .addComponent(productNameLabel2)
                            .addComponent(jLabel32)
                            .addComponent(buyingPriceLabel))
                        .addGap(34, 34, 34)))
                .addGap(12, 12, 12)
                .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectReturnStockButton)
                    .addComponent(jLabel35)
                    .addComponent(submitButton)
                    .addComponent(reasonComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(supplierReturnsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel37)
                        .addComponent(sortByComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(resetButton2)
                        .addComponent(jLabel23)
                        .addComponent(jLabel27)
                        .addComponent(findButton))
                    .addComponent(fromDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(toDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Supplier", supplierReturnsPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void closeLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closeLabel1MouseClicked
        this.home.removeReturns();
    }//GEN-LAST:event_closeLabel1MouseClicked

    private void closeLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closeLabel2MouseClicked
        this.home.removeReturns();
    }//GEN-LAST:event_closeLabel2MouseClicked

    private void selectInvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectInvoiceButtonActionPerformed
        SelectInvoice selectInvoice = new SelectInvoice((Frame) SwingUtilities.getWindowAncestor(this), true, this);
        selectInvoice.setVisible(true);
    }//GEN-LAST:event_selectInvoiceButtonActionPerformed

    private void invoiceItemTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_invoiceItemTableMouseClicked
        ButtonModel type = buttonGroup1.getSelection();
        
        if (type == null) {
            JOptionPane.showMessageDialog(this, "Please select a type", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (type.getActionCommand().equals("Full")) {
            JOptionPane.showMessageDialog(this, "Cannot select items when the return type is Full", "Warning", 
                    JOptionPane.WARNING_MESSAGE);
        }else if (type.getActionCommand().equals("Partial")) {
            int row = invoiceItemTable.getSelectedRow();
            addButton1.setEnabled(true);

            productNameLabel1.setText(String.valueOf(invoiceItemTable.getValueAt(row, 1)));
            productPriceLabel.setText(String.valueOf(invoiceItemTable.getValueAt(row, 3)));
            qtyFormattedField1.setText(String.valueOf(invoiceItemTable.getValueAt(row, 2)));
        }        
    }//GEN-LAST:event_invoiceItemTableMouseClicked

    private void addButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButton1ActionPerformed
        ButtonModel type = buttonGroup1.getSelection();
    
        if (invoiceItemTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Please select an invoice", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (type == null) {
            JOptionPane.showMessageDialog(this, "Please select a type", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            try {
                String invoiceId = invoiceIDLabel.getText();

                if (type.getActionCommand().equals("Full")) {
                    ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `invoice` "
                            + "INNER JOIN `invoice_item` ON `invoice`.`id` = `invoice_item`.`invoice_id` "
                            + "INNER JOIN `stock` ON `invoice_item`.`stock_id` = `stock`.`id` "
                            + "INNER JOIN `product` ON `stock`.`product_id` = `product`.`id` "
                            + "WHERE `invoice`.`id` = '" + invoiceId + "'");

                    while (resultSet.next()) {
                        String stockId = resultSet.getString("stock.id");
                        double invoiceQty = resultSet.getDouble("invoice_item.qty");
                        double alreadyReturnedQty = getAlreadyReturnedQty(invoiceId, stockId);

                        // Skip if already fully returned
                        if (alreadyReturnedQty >= invoiceQty) {
                            continue;
                        }

                        CustomerReturnItem customerReturnItem = new CustomerReturnItem();
                        customerReturnItem.setStockID(stockId);
                        customerReturnItem.setProductName(resultSet.getString("product.name"));
                        customerReturnItem.setPrice(resultSet.getString("stock.price"));
                        customerReturnItem.setQty(String.valueOf(invoiceQty - alreadyReturnedQty)); // Only return the remaining qty
                        customerReturnItemMap.put(stockId, customerReturnItem);
                    }

                    loadCustomerReturnItems();
                    addButton1.setEnabled(false);
                    typePartialRadioButton.setEnabled(false);

                } else if (type.getActionCommand().equals("Partial")) {
                    int row = invoiceItemTable.getSelectedRow();
                    if (row == -1) {
                        JOptionPane.showMessageDialog(this, "Please select a product item", "Warning", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    String stockID = String.valueOf(invoiceItemTable.getValueAt(row, 0));
                    String returnItemQty = qtyFormattedField1.getText();
                    String invoiceItemQty = String.valueOf(invoiceItemTable.getValueAt(row, 2));

                    if (returnItemQty.isEmpty() || returnItemQty.equals("0") || returnItemQty.equals("0.00")) {
                        JOptionPane.showMessageDialog(this, "Please enter a quantity", "Warning", JOptionPane.WARNING_MESSAGE);
                    } else if (!returnItemQty.matches("^\\d+(\\.\\d{1,2})?$")) {
                        JOptionPane.showMessageDialog(this, "INVALID quantity", "Warning", JOptionPane.WARNING_MESSAGE);
                    } else {
                        double invoiceQtyD = Double.parseDouble(invoiceItemQty);
                        double returnQtyD = Double.parseDouble(returnItemQty);
                        double alreadyReturnedQty = getAlreadyReturnedQty(invoiceId, stockID);

                        if (alreadyReturnedQty >= invoiceQtyD) {
                            JOptionPane.showMessageDialog(this, "This item has already been fully returned", "Warning", JOptionPane.WARNING_MESSAGE);
                        } else if (returnQtyD + alreadyReturnedQty > invoiceQtyD) {
                            JOptionPane.showMessageDialog(this, "Quantity exceeds available to return", "Warning", JOptionPane.WARNING_MESSAGE);
                        } else {
                            if (customerReturnItemMap.get(stockID) == null) {
                                CustomerReturnItem customerReturnItem = new CustomerReturnItem();
                                customerReturnItem.setStockID(stockID);
                                customerReturnItem.setProductName(String.valueOf(invoiceItemTable.getValueAt(row, 1)));
                                customerReturnItem.setPrice(String.valueOf(invoiceItemTable.getValueAt(row, 3)));
                                customerReturnItem.setQty(returnItemQty);
                                customerReturnItemMap.put(stockID, customerReturnItem);
                            } else {
                                CustomerReturnItem foundReturnItem = customerReturnItemMap.get(stockID);

                                int option = JOptionPane.showConfirmDialog(this, "Do you want to update the quantity of "
                                        + "product: " + String.valueOf(invoiceItemTable.getValueAt(row, 1)), "Message",
                                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                                if (option == JOptionPane.YES_OPTION) {
                                    double currentQty = Double.parseDouble(foundReturnItem.getQty());
                                    if (returnQtyD + currentQty + alreadyReturnedQty > invoiceQtyD) {
                                        JOptionPane.showMessageDialog(this, "Quantity exceeds invoice item quantity", "Warning", JOptionPane.WARNING_MESSAGE);
                                    } else {
                                        foundReturnItem.setQty(String.valueOf(currentQty + returnQtyD));
                                    }
                                }
                            }

                            loadCustomerReturnItems();

                            invoiceItemTable.clearSelection();
                            productNameLabel1.setText("PRODUCT NAME");
                            productPriceLabel.setText("0.00");
                            qtyFormattedField1.setText("0");
                            typeFullRadioButton.setEnabled(false);
                        }
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
                logger.log(Level.WARNING, "Exception", e);
            }
        }
    }//GEN-LAST:event_addButton1ActionPerformed

    private void confirmButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmButtonActionPerformed
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `customer_return_receipt` "
                    + "WHERE `id` = '"+receiptIDField.getText()+"'");
            
            if (resultSet.next()) {
                JOptionPane.showMessageDialog(this, "This return receipt already exists", "Warning", JOptionPane.WARNING_MESSAGE);
            }else {
                String receiptID = receiptIDField.getText();
                String returnTotal = returnTotalLabel.getText();
                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                ButtonModel returnType = buttonGroup1.getSelection();
                ButtonModel returnMethod = buttonGroup2.getSelection();
                String invoiceID = invoiceIDLabel.getText();
                String employeeEmail = employeeEmailLabel1.getText();
                int rowCount = customerReturnItemTable.getRowCount();
                
                if (rowCount == 0) {
                    JOptionPane.showMessageDialog(this, "Please add items(s) to return receipt", 
                            "Warning", JOptionPane.WARNING_MESSAGE);
                }else if (returnMethod == null) {
                    JOptionPane.showMessageDialog(this, "Please select a return method", "Warning", JOptionPane.WARNING_MESSAGE);
                }else if (reasonComboBox1.getSelectedItem().equals("Select")) {
                    JOptionPane.showMessageDialog(this, "Please select a return reason", "Warning", JOptionPane.WARNING_MESSAGE);
                }else if (returnMethod.getActionCommand().equals("Exchange") && 
                        reasonComboBox1.getSelectedItem().equals("Incorrect Item(s)")) {
                    JOptionPane.showMessageDialog(this, "Invalid return method and reason selection", 
                            "Warning", JOptionPane.WARNING_MESSAGE);
                }else if (returnMethod.getActionCommand().equals("Exchange") && 
                        reasonComboBox1.getSelectedItem().equals("Changed Mind")) {
                    JOptionPane.showMessageDialog(this, "Invalid return method and reason selection", 
                            "Warning", JOptionPane.WARNING_MESSAGE);
                }else {
                    MySQL.executeIUD("INSERT INTO `customer_return_receipt` VALUES('"+receiptID+"', '"+returnTotal+"', "
                        + "'"+dateTime+"', '"+customerReturnReasonMap.get(String.valueOf(reasonComboBox1.getSelectedItem()))+"', "
                            + "'"+customerReturnTypeMap.get(returnType.getActionCommand())+"', "
                                + "'"+customerReturnMethodMap.get(returnMethod.getActionCommand())+"', "
                                    + "'"+invoiceID+"', '"+employeeEmail+"')");
                    
                    for (CustomerReturnItem returnItem : customerReturnItemMap.values()) {
                        if ((returnMethod.getActionCommand().equals("Exchange") && 
                                reasonComboBox1.getSelectedItem().equals("Defective Product(s)")) || 
                                (returnMethod.getActionCommand().equals("Exchange") && 
                                reasonComboBox1.getSelectedItem().equals("Damaged Product(s)")) || 
                                (returnMethod.getActionCommand().equals("Exchange") && 
                                reasonComboBox1.getSelectedItem().equals("Expired Item(s)"))) {
                            MySQL.executeIUD("INSERT INTO `customer_return_item`(`customer_return_receipt_id`, `qty`, "
                                + "`stock_id`) VALUES('"+receiptID+"', '"+returnItem.getQty()+"', "
                                        + "'"+returnItem.getStockID()+"')");
                            
                            ResultSet resultSet1 = MySQL.executeSearch("SELECT * FROM `returning_stock` "
                                    + "WHERE `stock_id` = '"+returnItem.getStockID()+"' "
                                            + "AND `returning_stock_status_id` = 1");
                            
                            if (resultSet1.next()) {
                                MySQL.executeIUD("UPDATE `returning_stock` SET `qty` = `qty`+'"+returnItem.getQty()+"' "
                                        + "WHERE `stock_id` = '"+returnItem.getStockID()+"' "
                                                + "AND `returning_stock_status_id` = 1");
                            }else {
                                MySQL.executeIUD("INSERT INTO `returning_stock`(`stock_id`, `qty`, "
                                        + "`returning_stock_status_id`) VALUES('"+returnItem.getStockID()+"', "
                                                + "'"+returnItem.getQty()+"', 1)");
                            }
                            
                            MySQL.executeIUD("UPDATE `stock` SET `qty` = `qty`-'"+returnItem.getQty()+"' "
                                    + "WHERE `id` = '"+returnItem.getStockID()+"'");
                        }else if ((returnMethod.getActionCommand().equals("Refund") && 
                                reasonComboBox1.getSelectedItem().equals("Defective Product(s)")) || 
                                (returnMethod.getActionCommand().equals("Refund") && 
                                reasonComboBox1.getSelectedItem().equals("Damaged Product(s)")) || 
                                (returnMethod.getActionCommand().equals("Refund") && 
                                reasonComboBox1.getSelectedItem().equals("Expired Item(s)"))) {
                            MySQL.executeIUD("INSERT INTO `customer_return_item`(`customer_return_receipt_id`, `qty`, "
                                + "`stock_id`) VALUES('"+receiptID+"', '"+returnItem.getQty()+"', "
                                        + "'"+returnItem.getStockID()+"')");
                            
                            ResultSet resultSet1 = MySQL.executeSearch("SELECT * FROM `returning_stock` "
                                    + "WHERE `stock_id` = '"+returnItem.getStockID()+"' "
                                            + "AND `returning_stock_status_id` = 1");
                            
                            if (resultSet1.next()) {
                                MySQL.executeIUD("UPDATE `returning_stock` SET `qty` = `qty`+'"+returnItem.getQty()+"' "
                                        + "WHERE `stock_id` = '"+returnItem.getStockID()+"' "
                                                + "AND `returning_stock_status_id` = 1");
                            }else {
                                MySQL.executeIUD("INSERT INTO `returning_stock`(`stock_id`, `qty`, "
                                        + "`returning_stock_status_id`) VALUES('"+returnItem.getStockID()+"', "
                                                + "'"+returnItem.getQty()+"', 1)");
                            }
                        }else if ((returnMethod.getActionCommand().equals("Store Credit") && 
                                reasonComboBox1.getSelectedItem().equals("Defective Product(s)")) || 
                                (returnMethod.getActionCommand().equals("Store Credit") && 
                                reasonComboBox1.getSelectedItem().equals("Damaged Product(s)")) || 
                                (returnMethod.getActionCommand().equals("Store Credit") && 
                                reasonComboBox1.getSelectedItem().equals("Expired Item(s)"))) {
                            MySQL.executeIUD("INSERT INTO `customer_return_item`(`customer_return_receipt_id`, `qty`, "
                                + "`stock_id`) VALUES('"+receiptID+"', '"+returnItem.getQty()+"', "
                                        + "'"+returnItem.getStockID()+"')");
                            
                            ResultSet resultSet1 = MySQL.executeSearch("SELECT * FROM `returning_stock` "
                                    + "WHERE `stock_id` = '"+returnItem.getStockID()+"' "
                                            + "AND `returning_stock_status_id` = 1");
                            
                            if (resultSet1.next()) {
                                MySQL.executeIUD("UPDATE `returning_stock` SET `qty` = `qty`+'"+returnItem.getQty()+"' "
                                        + "WHERE `stock_id` = '"+returnItem.getStockID()+"' "
                                                + "AND `returning_stock_status_id` = 1");
                            }else {
                                MySQL.executeIUD("INSERT INTO `returning_stock`(`stock_id`, `qty`, "
                                        + "`returning_stock_status_id`) VALUES('"+returnItem.getStockID()+"', "
                                                + "'"+returnItem.getQty()+"', 1)");
                            }
                            
                            MySQL.executeIUD("UPDATE `customer` SET `store_credit` = `store_credit`+'"+returnTotal+"' "
                                    + "WHERE `mobile` = '"+customerMobileLabel.getText()+"'");
                        }else if ((returnMethod.getActionCommand().equals("Refund") && 
                                reasonComboBox1.getSelectedItem().equals("Incorrect Item(s)")) || 
                                (returnMethod.getActionCommand().equals("Refund") && 
                                reasonComboBox1.getSelectedItem().equals("Changed Mind"))) {
                            MySQL.executeIUD("INSERT INTO `customer_return_item`(`customer_return_receipt_id`, `qty`, "
                                + "`stock_id`) VALUES('"+receiptID+"', '"+returnItem.getQty()+"', "
                                        + "'"+returnItem.getStockID()+"')");
                            MySQL.executeIUD("UPDATE `stock` SET `qty` = `qty`+'"+returnItem.getQty()+"' "
                                + "WHERE `id` = '"+returnItem.getStockID()+"'");
                        }else if ((returnMethod.getActionCommand().equals("Store Credit") && 
                                reasonComboBox1.getSelectedItem().equals("Incorrect Item(s)")) || 
                                (returnMethod.getActionCommand().equals("Store Credit") && 
                                reasonComboBox1.getSelectedItem().equals("Changed Mind"))) {
                            MySQL.executeIUD("INSERT INTO `customer_return_item`(`customer_return_receipt_id`, `qty`, "
                                + "`stock_id`) VALUES('"+receiptID+"', '"+returnItem.getQty()+"', "
                                        + "'"+returnItem.getStockID()+"')");
                            MySQL.executeIUD("UPDATE `stock` SET `qty` = `qty`+'"+returnItem.getQty()+"' "
                                + "WHERE `id` = '"+returnItem.getStockID()+"'");
                            MySQL.executeIUD("UPDATE `customer` SET `store_credit` = `store_credit`+'"+returnTotal+"' "
                                    + "WHERE `mobile` = '"+customerMobileLabel.getText()+"'");
                        }
                    }
                    
                    if (returnType.getActionCommand().equals("Full")) {
                        MySQL.executeIUD("UPDATE `invoice` "
                                + "SET `invoice_status_id` = '"+invoiceStatusMap.get("Fully Returned")+"' "
                                        + "WHERE `id` = '"+invoiceIDLabel.getText()+"'");
                    }else if (returnType.getActionCommand().equals("Partial")) {
                        MySQL.executeIUD("UPDATE `invoice` "
                                + "SET `invoice_status_id` = '"+invoiceStatusMap.get("Partially Returned")+"' "
                                        + "WHERE `id` = '"+invoiceIDLabel.getText()+"'");
                    }
                    
                    InputStream path = this.getClass().getResourceAsStream("/reports/gh_return_receipt.jasper");
                    HashMap<String, Object> parameters = new HashMap<>();
                    parameters.put("Parameter1", employeeEmailLabel1.getText());
                    parameters.put("Parameter2", receiptIDField.getText());
                    parameters.put("Parameter3", invoiceIDLabel.getText());
                    parameters.put("Parameter4", dateTime);
                    parameters.put("Parameter5", returnTotalLabel.getText());
                    parameters.put("Parameter6", reasonComboBox1.getSelectedItem());
                    parameters.put("Parameter7", returnType.getActionCommand());
                    parameters.put("Parameter8", returnMethod.getActionCommand());
                    
                    String appDir = new File("").getAbsolutePath(); // Get the application's directory
                    String reportsFolder = appDir + File.separator + "ExportedReports"; // Main folder path
                    // Create the main "ExportedReports" folder if it doesn't exist
                    File mainDirectory = new File(reportsFolder);
                    if (!mainDirectory.exists()) {
                        mainDirectory.mkdirs();
                    }
                    // Create subfolder for "Return Receipt Reports" if it doesn't exist
                    String receiptReportsFolder = reportsFolder + File.separator + "Return Receipt Reports";
                    File receiptDirectory = new File(receiptReportsFolder);
                    if (!receiptDirectory.exists()) {
                        receiptDirectory.mkdirs();
                    }
                    // Path to export the PDF file (inside "Return Receipt Reports" subfolder)
                    String outputPath = receiptReportsFolder + File.separator + "Return_receipt_report_" + receiptID + ".pdf";

                    JRTableModelDataSource dataSource = new JRTableModelDataSource(customerReturnItemTable.getModel());
                    JasperPrint report = JasperFillManager.fillReport(path, parameters, dataSource);
                    JasperViewer.viewReport(report, false);
                    JasperPrintManager.printReport(report, false);
                    JasperExportManager.exportReportToPdfFile(report, outputPath);
                    resetCustomerReturns();
                    logger.log(Level.INFO, "Successfully generated return receipt: " + receiptID);
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }//GEN-LAST:event_confirmButtonActionPerformed

    private void resetButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButton1ActionPerformed
        resetCustomerReturns();
    }//GEN-LAST:event_resetButton1ActionPerformed

    private void supplierComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_supplierComboBoxItemStateChanged
        try {
            if (supplierComboBox.getSelectedItem().equals("Select")) {
                supplierEmailLabel.setText("SUP. EMAIL");
            }else {
                ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `supplier` "
                    + "WHERE CONCAT(`first_name`, ' ', `last_name`) = '"+supplierComboBox.getSelectedItem()+"'");
            
                if (resultSet.next()) {
                    supplierEmailLabel.setText(resultSet.getString("email"));
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }//GEN-LAST:event_supplierComboBoxItemStateChanged

    private void selectReturnStockButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectReturnStockButtonActionPerformed
        if (supplierComboBox.getSelectedItem().equals("Select")) {
            JOptionPane.showMessageDialog(this, "Please select a supplier", "Warning", JOptionPane.WARNING_MESSAGE);
        }else {
            SelectReturnStock selectRmaStock = new SelectReturnStock((Frame) SwingUtilities.getWindowAncestor(this), true, this);
            selectRmaStock.setVisible(true);
        }
    }//GEN-LAST:event_selectReturnStockButtonActionPerformed

    private void addButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButton2ActionPerformed
        try {
            String stockID = returningStockIDLabel.getText();
            String productName = productNameLabel2.getText();
            String qty = quantityLabel.getText();
            String mfd = mfdLabel.getText();
            String exp = expLabel.getText();
            String buyingPrice = buyingPriceLabel.getText();
            
            if (stockID.equals("R. STOCK ID")) {
                JOptionPane.showMessageDialog(this, "Please select a return stock", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (supplierReturnItemMap.get(stockID) != null) {
                JOptionPane.showMessageDialog(this, "This return stock is already added", 
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }else {
                SupplierReturnItem supplierReturnItem = new SupplierReturnItem();
                supplierReturnItem.setStockID(stockID);
                supplierReturnItem.setProductName(productName);
                supplierReturnItem.setQty(qty);
                supplierReturnItem.setMfd(mfd);
                supplierReturnItem.setExp(exp);
                supplierReturnItem.setBuyingPrice(buyingPrice);
                supplierReturnItemMap.put(stockID, supplierReturnItem);
                loadSupplierReturnItems();
            }
            
            returningStockIDLabel.setText("R. STOCK ID");
            stockBarcodeField.setText("");
            productNameLabel2.setText("PRODUCT");
            quantityLabel.setText("QUANTITY");
            mfdLabel.setText("MFD");
            expLabel.setText("EXP");
            buyingPriceLabel.setText("BUYING PRICE");
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }//GEN-LAST:event_addButton2ActionPerformed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        try {
            int rowCount = supplierReturnItemTable.getRowCount();
            
            if (reasonComboBox2.getSelectedItem().equals("Select")) {
                JOptionPane.showMessageDialog(this, "Please select a return reason", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (rowCount == 0) {
                JOptionPane.showMessageDialog(this, "Please add item(s) to return invoice", "Warning", JOptionPane.WARNING_MESSAGE);
            }else {
                ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `return_invoice` "
                    + "WHERE `id` = '"+returnInvoiceIDField.getText()+"'");
            
                if (resultSet.next()) {
                    JOptionPane.showMessageDialog(this, "This return invoice already exists", "Warning", 
                            JOptionPane.WARNING_MESSAGE);
                }else {
                    String employeeEmail = employeeEmailLabel2.getText();
                    String returnInvoiceID = returnInvoiceIDField.getText();
                    String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    String supplierEmail = supplierEmailLabel.getText();
                    String reason = String.valueOf(reasonComboBox2.getSelectedItem());
                    
                    MySQL.executeIUD("INSERT INTO `return_invoice` "
                            + "VALUES('"+returnInvoiceID+"', '"+dateTime+"', '"+suppliersMap.get(supplierEmail)+"', "
                                    + "'"+supplierReturnReasonMap.get(reason)+"', 1, '"+employeeEmail+"', "
                                            + "'"+totalLabel.getText()+"')");
                    
                    for (SupplierReturnItem returnItem : supplierReturnItemMap.values()) {
                        MySQL.executeIUD("INSERT INTO `return_invoice_item`(`return_invoice_id`, `returning_stock_id`) "
                                + "VALUES('"+returnInvoiceID+"', '"+returnItem.getStockID()+"')");
                    }
                    
                    InputStream path = this.getClass().getResourceAsStream("/reports/gh_return_invoice.jasper");
                    HashMap<String, Object> parameters = new HashMap<>();
                    parameters.put("Parameter1", employeeEmailLabel2.getText());
                    parameters.put("Parameter2", returnInvoiceIDField.getText());
                    parameters.put("Parameter3", supplierComboBox.getSelectedItem());
                    parameters.put("Parameter4", dateTime);
                    parameters.put("Parameter5", reasonComboBox2.getSelectedItem());
                    parameters.put("Parameter6", totalLabel.getText());
                    
                    String appDir = new File("").getAbsolutePath(); // Get the application's directory
                    String reportsFolder = appDir + File.separator + "ExportedReports"; // Main folder path
                    // Create the main "ExportedReports" folder if it doesn't exist
                    File mainDirectory = new File(reportsFolder);
                    if (!mainDirectory.exists()) {
                        mainDirectory.mkdirs();
                    }
                    // Create subfolder for "Return Invoice Reports" if it doesn't exist
                    String retInvoiceReportsFolder = reportsFolder + File.separator + "Return Invoice Reports";
                    File retInvoiceDirectory = new File(retInvoiceReportsFolder);
                    if (!retInvoiceDirectory.exists()) {
                        retInvoiceDirectory.mkdirs();
                    }
                    // Path to export the PDF file (inside "Return Invoice Reports" subfolder)
                    String outputPath = retInvoiceReportsFolder + File.separator + "Return_invoice_report_" + returnInvoiceID + ".pdf";

                    JRTableModelDataSource dataSource = new JRTableModelDataSource(supplierReturnItemTable.getModel());
                    JasperPrint report = JasperFillManager.fillReport(path, parameters, dataSource);
                    JasperViewer.viewReport(report, false);
                    JasperPrintManager.printReport(report, false);
                    JasperExportManager.exportReportToPdfFile(report, outputPath);
                    resetSupplierReturns();
                    logger.log(Level.INFO, "Successfully generated return invoice: " + returnInvoiceID);
                    
                    GmailOAuthService service = new GmailOAuthService();
                    String recipient = supplierEmail;
                    //String recipient = "gamergangster866@gmail.com";
                    String subject = "Report Attached";
                    String bodyText = "Please find the attached PDF report.";
                    String pdfFileName = "Return_invoice_report_" + returnInvoiceID + ".pdf";
                    service.sendEmailWithAttachment(recipient, subject, bodyText, pdfFileName);
                    
                    logger.log(Level.INFO, "Successfully sent email with attachment " + pdfFileName + " to: " + recipient);
                    JOptionPane.showMessageDialog(this, "Return request submitted successfully", "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }//GEN-LAST:event_submitButtonActionPerformed

    private void resetButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButton2ActionPerformed
        resetSupplierReturns();
    }//GEN-LAST:event_resetButton2ActionPerformed

    private void typeFullRadioButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_typeFullRadioButtonMouseClicked
        if (invoiceItemTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Please select an invoice", "Warning", JOptionPane.WARNING_MESSAGE);
            buttonGroup1.clearSelection();
        }else {
            invoiceItemTable.setEnabled(false);
        }
    }//GEN-LAST:event_typeFullRadioButtonMouseClicked

    private void typePartialRadioButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_typePartialRadioButtonMouseClicked
        if (invoiceItemTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Please select an invoice", "Warning", JOptionPane.WARNING_MESSAGE);
            buttonGroup1.clearSelection();
        }else {
            invoiceItemTable.setEnabled(true);
            addButton1.setEnabled(false);
        }
    }//GEN-LAST:event_typePartialRadioButtonMouseClicked

    private void findButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findButtonActionPerformed
        loadSupplierReturnInvoices();
    }//GEN-LAST:event_findButtonActionPerformed

    private void stockBarcodeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stockBarcodeFieldActionPerformed
        String barcode = stockBarcodeField.getText();
        
        try {
            if (barcode.length() == 12) {
                ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `stock` "
                        + "INNER JOIN `product` ON `stock`.`product_id` = `product`.`id` "
                        + "INNER JOIN `grn_item` ON `stock`.`id` = `grn_item`.`stock_id` "
                        + "INNER JOIN `returning_stock` ON `stock`.`id` = `returning_stock`.`stock_id`"
                        + "WHERE `stock`.`barcode` = '"+barcode+"'");
                
                if (resultSet.next()) {
                    returningStockIDLabel.setText(resultSet.getString("returning_stock.id"));
                    quantityLabel.setText(resultSet.getString("returning_stock.qty"));
                    productNameLabel2.setText(resultSet.getString("product.name"));
                    mfdLabel.setText(resultSet.getString("stock.mfd"));
                    expLabel.setText(resultSet.getString("stock.exp"));
                    buyingPriceLabel.setText(resultSet.getString("grn_item.price"));
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton1;
    private javax.swing.JButton addButton2;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel buyingPriceLabel;
    private javax.swing.JLabel closeLabel1;
    private javax.swing.JLabel closeLabel2;
    private javax.swing.JButton confirmButton;
    private javax.swing.JLabel customerMobileLabel;
    private javax.swing.JTable customerReturnItemTable;
    private javax.swing.JPanel customerReturnsPanel;
    private javax.swing.JLabel employeeEmailLabel1;
    private javax.swing.JLabel employeeEmailLabel2;
    private javax.swing.JLabel expLabel;
    private javax.swing.JButton findButton;
    private com.toedter.calendar.JDateChooser fromDateChooser;
    private javax.swing.JLabel invoiceDateLabel;
    private javax.swing.JLabel invoiceEmpEmailLabel;
    private javax.swing.JLabel invoiceIDLabel;
    private javax.swing.JTable invoiceItemTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JRadioButton methodCreditRadioButton;
    private javax.swing.JRadioButton methodExchangeRadioButton;
    private javax.swing.JRadioButton methodRefundRadioButton;
    private javax.swing.JLabel mfdLabel;
    private javax.swing.JLabel paidAmountLabel;
    private javax.swing.JLabel productNameLabel1;
    private javax.swing.JLabel productNameLabel2;
    private javax.swing.JLabel productPriceLabel;
    private javax.swing.JFormattedTextField qtyFormattedField1;
    private javax.swing.JLabel quantityLabel;
    private javax.swing.JComboBox<String> reasonComboBox1;
    private javax.swing.JComboBox<String> reasonComboBox2;
    private javax.swing.JTextField receiptIDField;
    private javax.swing.JButton resetButton1;
    private javax.swing.JButton resetButton2;
    private javax.swing.JTextField returnInvoiceIDField;
    private javax.swing.JLabel returnTotalLabel;
    private javax.swing.JLabel returningStockIDLabel;
    private javax.swing.JButton selectInvoiceButton;
    private javax.swing.JButton selectReturnStockButton;
    private javax.swing.JComboBox<String> sortByComboBox;
    private javax.swing.JTextField stockBarcodeField;
    private javax.swing.JButton submitButton;
    private javax.swing.JTable supReturnInvoiceTable;
    private javax.swing.JComboBox<String> supplierComboBox;
    private javax.swing.JLabel supplierEmailLabel;
    private javax.swing.JTable supplierReturnItemTable;
    private javax.swing.JPanel supplierReturnsPanel;
    private com.toedter.calendar.JDateChooser toDateChooser;
    private javax.swing.JLabel totalLabel;
    private javax.swing.JRadioButton typeFullRadioButton;
    private javax.swing.JRadioButton typePartialRadioButton;
    // End of variables declaration//GEN-END:variables
}
