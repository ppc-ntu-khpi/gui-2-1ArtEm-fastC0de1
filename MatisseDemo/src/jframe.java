import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class jframe extends javax.swing.JFrame {

    static class Account {
        String type;
        double balance;
        double extra;

        Account(String type, double balance, double extra) {
            this.type = type;
            this.balance = balance;
            this.extra = extra;
        }
    }

    static class Client {
        String name;
        List<Account> accounts = new ArrayList<>();

        Client(String name) {
            this.name = name;
        }
    }
    private List<Client> clients = new ArrayList<>();

    private JComboBox<String> clientComboBox;
    private JTextPane clientDetailsPane;
    private JButton showButton;
    private JButton reportButton;
    private JButton aboutButton;
    private JLabel statusLabel;

    public jframe() {
        initComponents();
        setResizable(false);

        loadClientsFromFile();

        for (Client c : clients) {
            clientComboBox.addItem(c.name);
        }

        showButton.addActionListener(e -> showClient());
        reportButton.addActionListener(e -> showReport());
        aboutButton.addActionListener(e -> showAbout());
    }

    private void loadClientsFromFile() {
        try (Scanner sc = new Scanner(new File("test.dat"))) {

            int count = sc.nextInt();

            for (int i = 0; i < count; i++) {

                String name = sc.next() + " " + sc.next();
                int accCount = sc.nextInt();

                Client client = new Client(name);

                for (int j = 0; j < accCount; j++) {

                    String type = sc.next();

                    double balance = 0;
                    double extra = 0;

                    if (sc.hasNextDouble()) {
                        balance = sc.nextDouble();
                    } else {
                        sc.next();
                    }

                    if (sc.hasNextDouble()) {
                        extra = sc.nextDouble();
                    } else {
                        sc.next();
                    }

                    client.accounts.add(new Account(type, balance, extra));
                }

                clients.add(client);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error reading file: " + e.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showClient() {

        int index = clientComboBox.getSelectedIndex();
        if (index < 0 || index >= clients.size()) return;

        Client c = clients.get(index);

        StringBuilder sb = new StringBuilder();

        sb.append("<html><body style='font-family:sans-serif;font-size:11px;margin:5px;'>");
        sb.append("<b>").append(c.name).append("</b><br>");
        sb.append("-----------------------------------<br>");
        sb.append("Accounts:<br>");

        for (Account a : c.accounts) {

            switch (a.type) {

                case "S":
                    sb.append("Savings: ")
                            .append(a.balance)
                            .append(", rate: ")
                            .append(a.extra)
                            .append("<br>");
                    break;

                case "C":
                    sb.append("Checking: ")
                            .append(a.balance)
                            .append(", limit: ")
                            .append(a.extra)
                            .append("<br>");
                    break;

                case "O":
                    sb.append("Overdraft: ")
                            .append(a.balance)
                            .append("<br>");
                    break;
            }
        }

        sb.append("</body></html>");

        clientDetailsPane.setText(sb.toString());
        statusLabel.setText("Client loaded: " + c.name);
    }

    private void showReport() {

        StringBuilder sb = new StringBuilder();

        double totalBank = 0;
        int totalAccounts = 0;

        sb.append("<html><body style='font-family:sans-serif;font-size:11px;margin:5px;'>");
        sb.append("<b>BANK REPORT</b><br>");
        sb.append("-----------------------------------<br>");

        for (Client c : clients) {

            double clientTotal = 0;

            for (Account a : c.accounts) {
                clientTotal += a.balance;
            }

            totalBank += clientTotal;
            totalAccounts += c.accounts.size();

            sb.append(c.name)
                    .append(" | accounts: ")
                    .append(c.accounts.size())
                    .append(" | total: ")
                    .append(clientTotal)
                    .append("<br>");
        }

        sb.append("-----------------------------------<br>");
        sb.append("TOTAL CLIENTS: ").append(clients.size()).append("<br>");
        sb.append("TOTAL ACCOUNTS: ").append(totalAccounts).append("<br>");
        sb.append("TOTAL BANK BALANCE: ").append(totalBank).append("<br>");

        sb.append("</body></html>");

        statusLabel.setText(sb.toString());
    }


    private void showAbout() {
        JOptionPane.showMessageDialog(
                this,
                "MyBank System\nDeveloper: LENKO ARTEM",
                "About",
                JOptionPane.INFORMATION_MESSAGE
        );
    }


    private void initComponents() {

        setTitle("MyBank Clients");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));

        clientComboBox = new JComboBox<>();
        leftPanel.add(clientComboBox, BorderLayout.NORTH);

        clientDetailsPane = new JTextPane();
        clientDetailsPane.setContentType("text/html");
        clientDetailsPane.setEditable(false);
        clientDetailsPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        clientDetailsPane.setText("Select client and press Show");

        leftPanel.add(clientDetailsPane, BorderLayout.CENTER);

        contentPanel.add(leftPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        Dimension buttonSize = new Dimension(100, 28);

        showButton = new JButton("Show");
        showButton.setMaximumSize(buttonSize);

        reportButton = new JButton("Report");
        reportButton.setMaximumSize(buttonSize);

        aboutButton = new JButton("About");
        aboutButton.setMaximumSize(buttonSize);

        rightPanel.add(showButton);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(reportButton);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(aboutButton);

        contentPanel.add(rightPanel, BorderLayout.EAST);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        statusLabel = new JLabel(" Choose a client and press Show");
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, Color.BLACK),
                new EmptyBorder(5, 5, 5, 5)
        ));

        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        pack();
        setSize(420, 280);
        setLocationRelativeTo(null);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new jframe().setVisible(true));
    }
}