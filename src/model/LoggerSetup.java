/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.IOException;
import java.util.logging.*;

/**
 *
 * @author barth
 */
public class LoggerSetup {
    private static final Logger logger = Logger.getLogger("gh-pos-app");
    private static boolean isInitialized = false;

    public static Logger getLogger() {
        if (!isInitialized) {
            try {
                FileHandler fileHandler = new FileHandler("gh-pos-app.log", true);
                fileHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(fileHandler);
                isInitialized = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logger;
    }
}
