/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package gui;

import static gui.SignIn.logger;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import model.MySQL;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Random;
import javax.imageio.ImageIO;
import java.util.logging.Level;
import javax.swing.JOptionPane;

/**
 *
 * @author barth
 */
public class AddBarcode extends javax.swing.JDialog {
    private HashMap<String, String> cityMap = new HashMap<>();
    private String stockID;
    private String stockBarcode;
    private static final Random random = new Random();
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * Creates new form AddressView
     */
    public AddBarcode(java.awt.Frame parent, boolean modal, String stockID, String stockBarcode) {
        super(parent, modal);
        initComponents();
        stockIDLabel.setText(stockID);
        barcodeField.setText(stockBarcode);
        this.stockID = stockID;
        this.stockBarcode = stockBarcode;
        String barcodeFieldText = barcodeField.getText();
        
        if (barcodeFieldText.equals("Pending")) {
            generateButton.setEnabled(true);
        }else {
            generateButton.setEnabled(false);
        }
    }
    
    // Generate a unique 12-character barcode compatible with Code 128
    private void generateBarcode() {
        // Get the last 8 digits of the current timestamp
        String timestampPart = String.valueOf(System.currentTimeMillis()).substring(5);
        
        // Generate a random 4-character alphanumeric suffix
        StringBuilder randomSuffix = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            randomSuffix.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        
        // Combine timestamp part and random alphanumeric suffix to get a 12-character barcode
        String barcode = timestampPart + randomSuffix;
        barcodeField.setText(barcode);
        logger.log(Level.INFO, "Generated barcode:" + barcode);
    }
    
    // Export the barcode as a PNG file in the "ExportedBarcodes" folder using ZXing
    private void exportBarcode(String barcodeText) throws Exception {
        // Get the application's directory
        String appDir = new File("").getAbsolutePath();
        
        // Set the path for the "ExportedBarcodes" folder
        String barcodesFolder = appDir + File.separator + "ExportedBarcodes";
        File mainDirectory = new File(barcodesFolder);
        
        // Create the "ExportedBarcodes" folder if it doesn't exist
        if (!mainDirectory.exists()) {
            mainDirectory.mkdirs();
        }
        
        // Define the file name with stockID
        String fileName = "barcode-" + barcodeText + "_stock-" + stockID + ".png";
        Path filePath = FileSystems.getDefault().getPath(barcodesFolder, fileName);

        // Set barcode format and dimensions
        BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;
        int width = 300;
        int height = 100;

        // Generate the barcode as a BitMatrix
        BitMatrix bitMatrix = new MultiFormatWriter().encode(barcodeText, barcodeFormat, width, height);
        
        // Convert BitMatrix to BufferedImage
        BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        
        // Create a new image with extra height to include text
        int textHeight = 20;
        BufferedImage combinedImage = new BufferedImage(width, height + textHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();
        
        // Draw the barcode on the new image
        g.drawImage(barcodeImage, 0, 0, null);
        
        // Draw the barcode text below the barcode
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Center the text
        FontMetrics fontMetrics = g.getFontMetrics();
        int textWidth = fontMetrics.stringWidth(barcodeText);
        int x = (width - textWidth) / 2;
        int y = height + textHeight - 5;  // Adjust text position for padding
        
        g.drawString(barcodeText, x, y);
        g.dispose();
        
        // Save the final image with text as PNG
        ImageIO.write(combinedImage, "PNG", filePath.toFile());

        logger.log(Level.INFO, "Successfully exported barcode " + barcodeText + " to: " + filePath);
        JOptionPane.showMessageDialog(null, "Barcode exported as PNG to: " + filePath);
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
        jLabel3 = new javax.swing.JLabel();
        barcodeField = new javax.swing.JTextField();
        stockIDLabel = new javax.swing.JLabel();
        addButton = new javax.swing.JButton();
        generateButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add Barcode");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel1.setText("Stock ID :");

        jLabel2.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 153, 255));
        jLabel2.setText("Add Barcode");

        jLabel3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel3.setText("Barcode :");

        barcodeField.setEditable(false);
        barcodeField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        stockIDLabel.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        stockIDLabel.setForeground(new java.awt.Color(255, 51, 51));
        stockIDLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        stockIDLabel.setText("STOCK ID HERE");
        stockIDLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        addButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/add_icon.png"))); // NOI18N
        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        generateButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        generateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/barcode-icon.png"))); // NOI18N
        generateButton.setText("Generate");
        generateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateButtonActionPerformed(evt);
            }
        });

        exportButton.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        exportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/export-icon.png"))); // NOI18N
        exportButton.setText("Export");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stockIDLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(barcodeField)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(generateButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exportButton))
                    .addComponent(jLabel2))
                .addGap(17, 17, 17))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(stockIDLabel))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(barcodeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(generateButton)
                    .addComponent(addButton)
                    .addComponent(exportButton))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void generateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateButtonActionPerformed
        generateBarcode();
        generateButton.setEnabled(false);
    }//GEN-LAST:event_generateButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        String barcode = barcodeField.getText();
        
        if (barcode.equals("Pending")) {
            JOptionPane.showMessageDialog(this, "Please generate a barcode to add", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (barcode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please generate a barcode to add", "Warning", JOptionPane.WARNING_MESSAGE);
        }else {
            try {
                ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `stock` "
                        + "WHERE `id` = '"+stockID+"' AND `barcode` = '"+barcode+"'");
                
                if (resultSet.next()) {
                    JOptionPane.showMessageDialog(this, "A stock with this barcode already exists "
                            + "or a barcode is already added", "Warning", JOptionPane.WARNING_MESSAGE);
                }else {
                    MySQL.executeIUD("UPDATE `stock` SET `barcode` = '"+barcode+"' WHERE `id` = '"+stockID+"'");
                    logger.log(Level.INFO, "Successfully added barcode " + barcode + " to stock: " + stockID);
                    JOptionPane.showMessageDialog(this, "Barcode added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    Home.productsNStock.loadStock();
                }
            } catch (Exception e) {
                //e.printStackTrace();
                logger.log(Level.WARNING, "Exception", e);
            }
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        String barcode = barcodeField.getText();
        
        if (barcode.equals("Pending")) {
            JOptionPane.showMessageDialog(this, "Please generate a barcode to export", "Warning", JOptionPane.WARNING_MESSAGE);
        }else if (barcode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please generate a barcode to export", "Warning", JOptionPane.WARNING_MESSAGE);
        }else {
            try {
                exportBarcode(barcode);
            } catch (Exception e) {
                //e.printStackTrace();
                logger.log(Level.WARNING, "Exception", e);
            }
        }
    }//GEN-LAST:event_exportButtonActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JTextField barcodeField;
    private javax.swing.JButton exportButton;
    private javax.swing.JButton generateButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel stockIDLabel;
    // End of variables declaration//GEN-END:variables
}
