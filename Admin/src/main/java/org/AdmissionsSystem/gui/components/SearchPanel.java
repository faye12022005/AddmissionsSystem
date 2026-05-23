package org.AdmissionsSystem.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class SearchPanel extends JPanel {
    private final JTextField textField;
    private final JButton actionButton;
    private String placeholder = "";
    private boolean showingPlaceholder = false;

    public SearchPanel(int width, String placeholder, String buttonText) {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 6, 6));
        setPreferredSize(new Dimension(width, 44));

        textField = new JTextField();
        textField.setPreferredSize(new Dimension(Math.max(120, width - 100), 32));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                BorderFactory.createEmptyBorder(6,8,6,8)));

        actionButton = new JButton(buttonText == null ? "Tìm" : buttonText);
        actionButton.setPreferredSize(new Dimension(80, 32));

        add(textField);
        add(actionButton);

        setPlaceholder(placeholder);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (showingPlaceholder) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                    showingPlaceholder = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().trim().isEmpty()) {
                    setPlaceholder(SearchPanel.this.placeholder);
                }
            }
        });
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder == null ? "" : placeholder;
        if (textField.getText().trim().isEmpty() || showingPlaceholder) {
            showingPlaceholder = true;
            textField.setText(this.placeholder);
            textField.setForeground(Color.GRAY);
        }
    }

    public String getSearchText() {
        if (showingPlaceholder) return "";
        return textField.getText();
    }

    public void setSearchText(String text) {
        showingPlaceholder = false;
        textField.setText(text == null ? "" : text);
        textField.setForeground(Color.BLACK);
    }

    public void addActionListener(ActionListener al) {
        actionButton.addActionListener(al);
    }
}
