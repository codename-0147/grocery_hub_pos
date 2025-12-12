# Grocery Hub POS App

This is a POS (Point of Sales) application created for the use of supermarkets.
(Grocery Hub is a fictional super market made for the project use.)

Features:
 - Employee management
 - Supplier management
 - Customer management
 - Inventory management
 - Generate barcodes
 - Print and save "GRNs"
 - Print and save "invoices"
 - Print and save "customer return receipts"
 - Print and save "supplier return invoices"
 - Send Gmail to supplier when adding return invoice
 - Manage "sales reports" and other reports
 - Database backup & restore

## Built with

* [JDK 22.0.1](https://www.java.com/en/)
* [MySQL 8.0](https://www.mysql.com/)
* [Apache NetBeans IDE 22](https://netbeans.apache.org/front/main/index.html)
* [MySQL Workbench 8.0 CE](https://www.mysql.com/)
* [Jaspersoft Studio 7.0.0 CE for JasperReports 7.0.0](https://www.jaspersoft.com/)
* [Google Cloud Console](https://console.cloud.google.com/)

## Installation

### Creating a *Google Cloud project* for sending Gmails

1. Go to [Google Cloud Console](https://console.cloud.google.com/).
2. Create a new project.
3. Go to **APIs & Services**, click on **Enable APIs and services** then search for 
   **Gmail API** and enable it.
4. Then in **APIs & Services**, go to **Credentials** and create an **OAuth client ID**. 
   For the application type, select **Desktop app**. [Ref](https://developers.google.com/workspace/guides/create-credentials#desktop-app)
5. Then in **APIs & Services**, go to **OAuth consent screen**. Then in **Branding**,
   Under **App information**, provide **App name**, **User support email**. Under **Developer contact information**,
   provide an email.
6. Then in **Audience**, select the **User type** as **External**. Then add your email address and any other authorized test users. 
   Then save and finish. [Ref](https://developers.google.com/workspace/guides/configure-oauth-consent#configure_oauth_consent)

### Creating *GmailOAuthService.java* file for sending Gmails

1. Make a new Java class as **GmailOAuthService** in **model** package.
2. Add the below code and edit the variables **APPLICATION_NAME**, **CLIENT_ID**, **CLIENT_SECRET**. 
   (Add your **Client ID & Client Secret** you got when creating **OAuth client ID** credential in above steps.)

```java
package model;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.Properties;

public class GmailOAuthService {
    private static final String APPLICATION_NAME = "Your POS Application";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    // Use the Client ID and Secret from your Google Cloud Console
    private static final String CLIENT_ID = "Your Client ID";
    private static final String CLIENT_SECRET = "Your Client Secret";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    
    public Gmail getGmailService() throws Exception {
        // Load credentials and set up the Gmail API service
        Credential credential = getCredentials();  // Updated from GoogleCredential to Credential
        return new Gmail.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    private Credential getCredentials() throws Exception {
        File tokenFolder = new java.io.File(TOKENS_DIRECTORY_PATH);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                CLIENT_ID, CLIENT_SECRET,
                Collections.singleton(GmailScopes.GMAIL_SEND))
                .setDataStoreFactory(new FileDataStoreFactory(tokenFolder))
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();

        try {
            // Try normal authorization first
            //System.out.println("No refresh token found. User authorization (browser login) may be needed.");
            return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver.Builder().setPort(8888).build())
                    .authorize("user");
        } catch (com.google.api.client.auth.oauth2.TokenResponseException e) {
            if (e.getDetails() != null && "invalid_grant".equals(e.getDetails().getError())) {
                System.out.println("Detected invalid token. Deleting old tokens and retrying...");

                // Delete tokens folder
                deleteDirectory(tokenFolder);

                // Retry authorization (fresh)
                GoogleAuthorizationCodeFlow freshFlow = new GoogleAuthorizationCodeFlow.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        JSON_FACTORY,
                        CLIENT_ID, CLIENT_SECRET,
                        Collections.singleton(GmailScopes.GMAIL_SEND))
                        .setDataStoreFactory(new FileDataStoreFactory(tokenFolder))
                        .setAccessType("offline")
                        .setApprovalPrompt("force")
                        .build();

                //System.out.println("Using saved refresh token. No browser login needed.");
                return new AuthorizationCodeInstalledApp(freshFlow, new LocalServerReceiver.Builder().setPort(8888).build())
                        .authorize("user");
            } else {
                throw e; // If different error, rethrow it
            }
        }
    }

    // Helper method to delete the tokens folder
    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteDirectory(f);
                    } else {
                        f.delete();
                    }
                }
            }
            directory.delete();
        }
    }

    public void sendEmailWithAttachment(String to, String subject, String bodyText, String pdfFileName) throws Exception {
        // Initialize the Gmail API service
        Gmail service = getGmailService();

        // Dynamically locate the PDF file
        String appDirectory = new File(".").getCanonicalPath(); // Get application installation directory
        String attachmentFilePath = Paths.get(appDirectory, "ExportedReports", "Return Invoice Reports", pdfFileName).toString();

        // Check if the file exists
        if (!Files.exists(Paths.get(attachmentFilePath))) {
            throw new FileNotFoundException("Attachment file not found: " + attachmentFilePath);
        }

        // Create the email content with an attachment
        MimeMessage email = createEmailWithAttachment(to, "me", subject, bodyText, attachmentFilePath);
        Message message = createMessageWithEmail(email);

        // Send the email
        service.users().messages().send("me", message).execute();
        System.out.println("Email with attachment sent successfully!");
    }

    private MimeMessage createEmailWithAttachment(String to, String from, String subject, String bodyText, String attachmentFilePath) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);

        // Create the email body
        MimeBodyPart textBodyPart = new MimeBodyPart();
        textBodyPart.setText(bodyText);

        // Create the attachment part
        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(attachmentFilePath);
        attachmentBodyPart.setDataHandler(new DataHandler(source));
        attachmentBodyPart.setFileName(new File(attachmentFilePath).getName());

        // Combine the body and attachment
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textBodyPart);
        multipart.addBodyPart(attachmentBodyPart);

        email.setContent(multipart);
        return email;
    }

    private Message createMessageWithEmail(MimeMessage email) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.getUrlEncoder().encodeToString(rawMessageBytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
}
```

### Creating the database

1. Download [grocery_hub_pos_db](https://drive.google.com/file/d/1cLt1EdkEs3WLftim1zzfIvY2CFg1asyl/view?usp=sharing) MySQL Workbench file.
2. Open it from MySQL Workbench, then click **Database > Forward engineer...** and follow the wizard to create the database.
3. Add data to the tables mentioned below first.
    - customer_return_method:
      * Exchange
      * Refund
      * Store Credit

    - customer_return_reason:
      * Defective Product(s)
      * Incorrect Item(s)
      * Changed Mind
      * Damaged Product(s)
      * Expired Item(s)

    - customer_return_type:
      * Full
      * Partial

    - employee_type:
      * Admin
      * Cashier

    - gender:
      * Male
      * Female

    - invoice_status:
      * Not Returned
      * Fully Returned
      * Partially Returned

    - payment_method:
      * Cash
      * Credit Card

    - returning_stock_status:
      * Pending
      * Processed

    - return_invoice_status:
      * Pending
      * Approved

    - supplier_return_reason:
      * Defective Product(s)
      * Incorrect Item(s)
      * Damaged Product(s)
      * Expired Item(s)
      * Excess Stock

## Usage

1. Open up the **SignIn** file from IDE and run it. It is the main class.
2. When running the project for the first time, **SetupConnection** GUI will open up to setup the connection to database you have 
   created above. Set it up as follows:
    - Host Address: *localhost*
    - Port: *3306*
    - DB Name: *grocery_hub_pos_db*
    - Username: *root*
    - Password: *Your MySQL password*
    - Select Dump File: *Select mysqldump.exe file from where you have installed MySQL. (ex: If installed in the default 
      path, it can be located in C:\Program Files\MySQL\MySQL Server 8.0\bin)*
3. Then click on **Setup Connection**. When the connection to the database created successfully, **SignIn** GUI will open up.
4. Make sure to add an **Admin** employee manually in database before running the project to sign in to the application after 
   setting up the connection to database.
5. When adding a **Supplier return invoice** for the first time, a browser popup will appear with OAuth consent screen to authorize 
   an email address. Make sure to select an email adress you've added as a test user when setting up OAuth consent screen in above steps.
