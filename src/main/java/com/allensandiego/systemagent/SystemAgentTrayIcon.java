package com.allensandiego.systemagent;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.MenuItem;
import java.awt.AWTException;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.core.io.ClassPathResource;

public class SystemAgentTrayIcon extends TrayIcon {

    private static final String IMAGE_PATH = "images/icons8-system-16.png";
    private static final String TOOLTIP = "System Agent";
    private PopupMenu popup;
    final SystemTray tray;

    public SystemAgentTrayIcon() {
        super(createImage(IMAGE_PATH, TOOLTIP), TOOLTIP);
        popup = new PopupMenu();
        tray = SystemTray.getSystemTray();
        try {
            setup();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    private void setup() throws AWTException {
        // Create a pop-up menu components
        MenuItem exitItem = new MenuItem("Exit");
        popup.add(exitItem);
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final int exitCode = 0;
                ExitCodeGenerator exitCodeGenerator = new ExitCodeGenerator() {

                    @Override
                    public int getExitCode() {
                        return exitCode;
                    }

                };

                tray.remove(SystemAgentTrayIcon.this);
                SpringApplication.exit(SystemAgentApplication.context, exitCodeGenerator);
            }
        });
        // popup.addSeparator();
        setPopupMenu(popup);
        tray.add(this);

    }

    protected static Image createImage(String path, String description) {
        URL imageURL;
        try {
            imageURL = new ClassPathResource(path).getURL();
            return new ImageIcon(imageURL, description).getImage();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
