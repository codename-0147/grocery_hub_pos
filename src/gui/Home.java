/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;

import java.awt.Dimension;
import static java.lang.Thread.sleep;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.SwingUtilities;
import model.JpanelLoader;
import static gui.SignIn.logger;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import model.MySQL;
import java.sql.ResultSet;
import java.util.logging.Level;

/**
 *
 * @author barth
 */
public class Home extends javax.swing.JFrame {
    JpanelLoader jPanelLoad = new JpanelLoader();
    
    private static Employees employees;
    private static Suppliers suppliers;
    private static Customers customers;
    public static ProductsNStock productsNStock;
    private static GRN grn;
    private static Invoice invoice;
    private static Returns returns;
    private static Reports reports;

    /**
     * Creates new form Home1
     */
    public Home(String email,String fName,String lName) {
        initComponents();
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/gh-main-logo.png")));
        leftMenuButton.setVisible(false);
        this.setExtendedState(Home.MAXIMIZED_BOTH);
        updateFieldDate();
        emailLabel.setText(email);
        fullNameLabel.setText(fName + " " + lName);
        checkGuiAccess(email);
    }
    
    private void checkGuiAccess(String email) {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `employee` INNER JOIN `employee_type` "
                    + "ON `employee`.`employee_type_id` = `employee_type`.`id` "
                    + "WHERE `employee`.`email` = '"+email+"' AND `employee_type`.`name` = 'Cashier'");
            
            if (resultSet.next()) {
                employeesButton.setEnabled(false);
                suppliersButton.setEnabled(false);
                customersButton.setEnabled(false);
                productsNStockButton.setEnabled(false);
                grnButton.setEnabled(false);
                reportsButton.setEnabled(false);
                dbBackupButton.setEnabled(false);
                dbRestoreButton.setEnabled(false);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
        }
    }
    
    private void moveLeft() {
        rightMenuButton.setVisible(false);
        Thread t = new Thread(
                    () -> {
                        for (int i = 215; i >= 76; i-=10) {
                            jPanel1.setPreferredSize(new Dimension(i,jPanel1.getHeight()));
                            SwingUtilities.updateComponentTreeUI(jPanel1);
                            
                            try {
                                Thread.sleep(1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        leftMenuButton.setVisible(true);
                        jLabel2.setVisible(false);
                        employeesButton.setText("");
                        suppliersButton.setText("");
                        customersButton.setText("");
                        productsNStockButton.setText("");
                        grnButton.setText("");
                        invoiceButton.setText("");
                        returnsButton.setText("");
                        reportsButton.setText("");
                        dbBackupButton.setText("");
                        dbRestoreButton.setText("");
                        signOutButton.setText("");
                    }
            );
            t.start();
    }
    
    private void moveRight() {
        leftMenuButton.setVisible(false);
        Thread t = new Thread(
                    () -> {
                        for (int i = 70; i <= 215; i+=10) {
                            jPanel1.setPreferredSize(new Dimension(i,jPanel1.getHeight()));
                            SwingUtilities.updateComponentTreeUI(jPanel1);
                            
                            try {
                                Thread.sleep(1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        rightMenuButton.setVisible(true);
                        jLabel2.setVisible(true);
                        employeesButton.setText("Employees");
                        suppliersButton.setText("Suppliers");
                        customersButton.setText("Customers");
                        productsNStockButton.setText("Products & Stock");
                        grnButton.setText("GRN");
                        invoiceButton.setText("Invoice");
                        returnsButton.setText("Returns");
                        reportsButton.setText("Reports");
                        dbBackupButton.setText("Backup DB");
                        dbRestoreButton.setText("Restore DB");
                        signOutButton.setText("Sign Out");
                    }
            );
            t.start();
    }
    
    public void updateFieldDate() {
        Thread Clock = new Thread() {
            public void run() {
                    for(;;) {
                        try {
                            sleep(1000);
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm:ss a dd/MM/yyyy"); 
                            LocalDateTime now = LocalDateTime.now();  
                            dateTimeLabel.setText(dtf.format(now));
                        }
                        catch (Exception e) {
                            System.out.println("Error");
                        }
                    }
            }
        };
        Clock.start();
    }
    
    private void backupDatabase() {
        try {
            String backupDir = new File("").getAbsolutePath() + File.separator + "Backups";
            File backupFolder = new File(backupDir);
            
            if (!backupFolder.exists()) {
                backupFolder.mkdirs();
            }

            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String backupFileName = "backup-" + date + ".sql";
            String backupFilePath = backupDir + File.separator + backupFileName;
            
            String appPath = new File("").getAbsolutePath();
            File dbInfoFile = new File(appPath + File.separator + "dbinfo.ser");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dbInfoFile));
            MySQL db = (MySQL) ois.readObject();
            ois.close();

            List<String> command = Arrays.asList(
                db.dump,
                "-h", db.host,
                "-P", db.port,
                "-u", db.un,
                "-p" + db.pw,
                db.dbname
            );

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectOutput(new File(backupFilePath));
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process process = processBuilder.start();
            int processComplete = process.waitFor();

            if (processComplete == 0) {
                logger.log(Level.INFO, "Successully backed up database");
                JOptionPane.showMessageDialog(null, "Backup created successfully: " + backupFilePath);
            } else {
                logger.log(Level.INFO, "Failed backing up database");
                JOptionPane.showMessageDialog(null, "Backup failed.");
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
    
    private void restoreDatabase() {
        try {
            JFileChooser fileChooser = new JFileChooser(new File("").getAbsolutePath() + File.separator + "Backups");
            fileChooser.setDialogTitle("Select SQL File to Restore");
            int userSelection = fileChooser.showOpenDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File restoreFile = fileChooser.getSelectedFile();
                
                String appPath = new File("").getAbsolutePath();
                File dbInfoFile = new File(appPath + File.separator + "dbinfo.ser");
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dbInfoFile));
                MySQL db = (MySQL) ois.readObject();
                ois.close();

                List<String> command = Arrays.asList(
                    "mysql",
                    "-h", db.host,
                    "-P", db.port,
                    "-u", db.un,
                    "-p" + db.pw,
                    db.dbname
                );

                ProcessBuilder processBuilder = new ProcessBuilder(command);
                processBuilder.redirectInput(restoreFile);
                processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

                Process process = processBuilder.start();
                int processComplete = process.waitFor();

                if (processComplete == 0) {
                    logger.log(Level.INFO, "Successfully restored database");
                    JOptionPane.showMessageDialog(null, "Database restored successfully.");
                } else {
                    logger.log(Level.INFO, "Failed restoring database");
                    JOptionPane.showMessageDialog(null, "Restore failed.");
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.log(Level.WARNING, "Exception", e);
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
    
    public void removeEmployees() {
        panelsLoadPanel.remove(employees);
        employees = null;
        SwingUtilities.updateComponentTreeUI(panelsLoadPanel);
    }
    
    public void removeSuppliers() {
        panelsLoadPanel.remove(suppliers);
        suppliers = null;
        SwingUtilities.updateComponentTreeUI(panelsLoadPanel);
    }
    
    public void removeCustomers() {
        panelsLoadPanel.remove(customers);
        customers = null;
        SwingUtilities.updateComponentTreeUI(panelsLoadPanel);
    }
    
    public void removeProductsNStock() {
        panelsLoadPanel.remove(productsNStock);
        productsNStock = null;
        SwingUtilities.updateComponentTreeUI(panelsLoadPanel);
    }
    
    public void removeGRN() {
        panelsLoadPanel.remove(grn);
        grn = null;
        SwingUtilities.updateComponentTreeUI(panelsLoadPanel);
    }
    
    public void removeInvoice() {
        panelsLoadPanel.remove(invoice);
        invoice = null;
        SwingUtilities.updateComponentTreeUI(panelsLoadPanel);
    }
    
    public void removeReturns() {
        panelsLoadPanel.remove(returns);
        returns = null;
        SwingUtilities.updateComponentTreeUI(panelsLoadPanel);
    }
    
    public void removeReports() {
        panelsLoadPanel.remove(reports);
        reports = null;
        SwingUtilities.updateComponentTreeUI(panelsLoadPanel);
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
        dateTimeLabel = new javax.swing.JLabel();
        emailLabel = new javax.swing.JLabel();
        fullNameLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        employeesButton = new javax.swing.JButton();
        suppliersButton = new javax.swing.JButton();
        customersButton = new javax.swing.JButton();
        productsNStockButton = new javax.swing.JButton();
        grnButton = new javax.swing.JButton();
        invoiceButton = new javax.swing.JButton();
        reportsButton = new javax.swing.JButton();
        leftMenuButton = new javax.swing.JButton();
        rightMenuButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        returnsButton = new javax.swing.JButton();
        dbBackupButton = new javax.swing.JButton();
        dbRestoreButton = new javax.swing.JButton();
        signOutButton = new javax.swing.JButton();
        panelsLoadPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Grocery Hub POS System");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        dateTimeLabel.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        dateTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dateTimeLabel.setText("DateTime");
        dateTimeLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        emailLabel.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        emailLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        emailLabel.setText("Email");
        emailLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        fullNameLabel.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        fullNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fullNameLabel.setText("First Name Last Name");
        fullNameLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        jLabel3.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 0, 0));
        jLabel3.setText("WELCOME!");

        jLabel4.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel4.setText("Name :");

        jLabel5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel5.setText("Email :");

        jLabel6.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel6.setText("Date Time :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(51, 51, 51)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fullNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(emailLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dateTimeLabel)
                    .addComponent(emailLabel)
                    .addComponent(fullNameLabel)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        employeesButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        employeesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/employees-icon.png"))); // NOI18N
        employeesButton.setText("Employees");
        employeesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                employeesButtonActionPerformed(evt);
            }
        });

        suppliersButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        suppliersButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/supplier-icon.png"))); // NOI18N
        suppliersButton.setText("Suppliers");
        suppliersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                suppliersButtonActionPerformed(evt);
            }
        });

        customersButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        customersButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/customers-icon.png"))); // NOI18N
        customersButton.setText("Customers");
        customersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customersButtonActionPerformed(evt);
            }
        });

        productsNStockButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        productsNStockButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/products-icon.png"))); // NOI18N
        productsNStockButton.setText("Products & Stock");
        productsNStockButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productsNStockButtonActionPerformed(evt);
            }
        });

        grnButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        grnButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/goods-icon.png"))); // NOI18N
        grnButton.setText("GRN");
        grnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grnButtonActionPerformed(evt);
            }
        });

        invoiceButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        invoiceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/invoice-icon.png"))); // NOI18N
        invoiceButton.setText("Invoice");
        invoiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invoiceButtonActionPerformed(evt);
            }
        });

        reportsButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        reportsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/reports-icon.png"))); // NOI18N
        reportsButton.setText("Reports");
        reportsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportsButtonActionPerformed(evt);
            }
        });

        leftMenuButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/menu-icon.png"))); // NOI18N
        leftMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftMenuButtonActionPerformed(evt);
            }
        });

        rightMenuButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/menu-icon.png"))); // NOI18N
        rightMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightMenuButtonActionPerformed(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/gh-sub-logo.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 153, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("POS System");

        returnsButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        returnsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/returns-icon.png"))); // NOI18N
        returnsButton.setText("Returns");
        returnsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                returnsButtonActionPerformed(evt);
            }
        });

        dbBackupButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        dbBackupButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/db-backup-icon.png"))); // NOI18N
        dbBackupButton.setText("Backup DB");
        dbBackupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dbBackupButtonActionPerformed(evt);
            }
        });

        dbRestoreButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        dbRestoreButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/db-restore-icon.png"))); // NOI18N
        dbRestoreButton.setText("Restore DB");
        dbRestoreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dbRestoreButtonActionPerformed(evt);
            }
        });

        signOutButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        signOutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/sign-out-icon.png"))); // NOI18N
        signOutButton.setText("Sign out");
        signOutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signOutButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(employeesButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(suppliersButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(customersButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(productsNStockButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(grnButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(invoiceButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(reportsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(leftMenuButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rightMenuButton))
                    .addComponent(returnsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addGap(0, 2, Short.MAX_VALUE))
                    .addComponent(dbBackupButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dbRestoreButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(signOutButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(leftMenuButton)
                    .addComponent(rightMenuButton))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(employeesButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(suppliersButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customersButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(productsNStockButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(grnButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(invoiceButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(returnsButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reportsButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
                .addComponent(dbBackupButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dbRestoreButton)
                .addGap(18, 18, 18)
                .addComponent(signOutButton)
                .addContainerGap())
        );

        panelsLoadPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout panelsLoadPanelLayout = new javax.swing.GroupLayout(panelsLoadPanel);
        panelsLoadPanel.setLayout(panelsLoadPanelLayout);
        panelsLoadPanelLayout.setHorizontalGroup(
            panelsLoadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelsLoadPanelLayout.setVerticalGroup(
            panelsLoadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelsLoadPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(panelsLoadPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void employeesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_employeesButtonActionPerformed
        if (employees == null) {
            panelsLoadPanel.removeAll();
            employees = new Employees(this);
            jPanelLoad.jPanelLoader(panelsLoadPanel, employees);
        }else {
            panelsLoadPanel.removeAll();
            jPanelLoad.jPanelLoader(panelsLoadPanel, employees);
        }
    }//GEN-LAST:event_employeesButtonActionPerformed

    private void suppliersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_suppliersButtonActionPerformed
        if (suppliers == null) {
            panelsLoadPanel.removeAll();
            suppliers = new Suppliers(this);
            jPanelLoad.jPanelLoader(panelsLoadPanel, suppliers);
        }else {
            panelsLoadPanel.removeAll();
            jPanelLoad.jPanelLoader(panelsLoadPanel, suppliers);
        }
    }//GEN-LAST:event_suppliersButtonActionPerformed

    private void customersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customersButtonActionPerformed
        if (customers == null) {
            panelsLoadPanel.removeAll();
            customers = new Customers(this);
            jPanelLoad.jPanelLoader(panelsLoadPanel, customers);
        }else {
            panelsLoadPanel.removeAll();
            jPanelLoad.jPanelLoader(panelsLoadPanel, customers);
        }
    }//GEN-LAST:event_customersButtonActionPerformed

    private void productsNStockButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productsNStockButtonActionPerformed
        if (productsNStock == null) {
            panelsLoadPanel.removeAll();
            productsNStock = new ProductsNStock(this);
            jPanelLoad.jPanelLoader(panelsLoadPanel, productsNStock);
        }else {
            panelsLoadPanel.removeAll();
            jPanelLoad.jPanelLoader(panelsLoadPanel, productsNStock);
        }
    }//GEN-LAST:event_productsNStockButtonActionPerformed

    private void grnButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grnButtonActionPerformed
        if (grn == null) {
            panelsLoadPanel.removeAll();
            grn = new GRN(this);
            jPanelLoad.jPanelLoader(panelsLoadPanel, grn);
        }else {
            panelsLoadPanel.removeAll();
            jPanelLoad.jPanelLoader(panelsLoadPanel, grn);
        }
    }//GEN-LAST:event_grnButtonActionPerformed

    private void invoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invoiceButtonActionPerformed
        if (invoice == null) {
            panelsLoadPanel.removeAll();
            invoice = new Invoice(this);
            jPanelLoad.jPanelLoader(panelsLoadPanel, invoice);
        }else {
            panelsLoadPanel.removeAll();
            jPanelLoad.jPanelLoader(panelsLoadPanel, invoice);
        }
    }//GEN-LAST:event_invoiceButtonActionPerformed

    private void reportsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportsButtonActionPerformed
        if (reports == null) {
            panelsLoadPanel.removeAll();
            reports = new Reports(this);
            jPanelLoad.jPanelLoader(panelsLoadPanel, reports);
        }else {
            panelsLoadPanel.removeAll();
            jPanelLoad.jPanelLoader(panelsLoadPanel, reports);
        }
    }//GEN-LAST:event_reportsButtonActionPerformed

    private void leftMenuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leftMenuButtonActionPerformed
        moveRight();
    }//GEN-LAST:event_leftMenuButtonActionPerformed

    private void rightMenuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rightMenuButtonActionPerformed
        moveLeft();
    }//GEN-LAST:event_rightMenuButtonActionPerformed

    private void returnsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_returnsButtonActionPerformed
        if (returns == null) {
            panelsLoadPanel.removeAll();
            returns = new Returns(this);
            jPanelLoad.jPanelLoader(panelsLoadPanel, returns);
        }else {
            panelsLoadPanel.removeAll();
            jPanelLoad.jPanelLoader(panelsLoadPanel, returns);
        }
    }//GEN-LAST:event_returnsButtonActionPerformed

    private void dbBackupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dbBackupButtonActionPerformed
        backupDatabase();
    }//GEN-LAST:event_dbBackupButtonActionPerformed

    private void dbRestoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dbRestoreButtonActionPerformed
        restoreDatabase();
    }//GEN-LAST:event_dbRestoreButtonActionPerformed

    private void signOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signOutButtonActionPerformed
        int option = JOptionPane.showConfirmDialog(this, "Do you want to sign out?", "Message", JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            logger.log(Level.INFO, "User signed out");
            this.dispose();
            employees = null;
            suppliers = null;
            customers = null;
            productsNStock = null;
            grn = null;
            invoice = null;
            returns = null;
            reports = null;
            logger.log(Level.INFO, "Proceeding to launch the SignIn GUI");
            new SignIn().setVisible(true);
        }
    }//GEN-LAST:event_signOutButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        logger.log(Level.INFO, "Stopped the application");
        System.exit(0);
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton customersButton;
    private javax.swing.JLabel dateTimeLabel;
    private javax.swing.JButton dbBackupButton;
    private javax.swing.JButton dbRestoreButton;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JButton employeesButton;
    private javax.swing.JLabel fullNameLabel;
    private javax.swing.JButton grnButton;
    private javax.swing.JButton invoiceButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton leftMenuButton;
    private javax.swing.JPanel panelsLoadPanel;
    private javax.swing.JButton productsNStockButton;
    private javax.swing.JButton reportsButton;
    private javax.swing.JButton returnsButton;
    private javax.swing.JButton rightMenuButton;
    private javax.swing.JButton signOutButton;
    private javax.swing.JButton suppliersButton;
    // End of variables declaration//GEN-END:variables
}
