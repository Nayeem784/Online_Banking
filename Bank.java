import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class BankingAppUI {
    static Connection con;
    static JFrame frame;
    static JTextField usernameField, pinField, depositField, transferField, receiverAcField;
    static JTextArea outputArea;
    static int currentAcNo = -1;

    public static void main(String[] args) {
        connectDatabase();
        showMainMenu();
    }

    static void connectDatabase() {
        try {
            String url = "jdbc:postgresql://localhost:5432/Bank";
            String user = "postgres";
            String pass = "123";
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void showMainMenu() {
        frame = new JFrame("PuKa Bank");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JButton createBtn = new JButton("Create Account");
        createBtn.setBounds(100, 50, 200, 30);
        createBtn.addActionListener(e -> showCreateAccount());
        frame.add(createBtn);

        JButton loginBtn = new JButton("Login Account");
        loginBtn.setBounds(100, 100, 200, 30);
        loginBtn.addActionListener(e -> showLoginAccount());
        frame.add(loginBtn);

        JButton exitBtn = new JButton("Exit");
        exitBtn.setBounds(100, 150, 200, 30);
        exitBtn.addActionListener(e -> System.exit(0));
        frame.add(exitBtn);

        frame.setVisible(true);
    }

    static void showCreateAccount() {
        JFrame createFrame = new JFrame("Create Account");
        createFrame.setSize(400, 300);
        createFrame.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 50, 100, 30);
        createFrame.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 50, 150, 30);
        createFrame.add(usernameField);

        JLabel pinLabel = new JLabel("4 Digit PIN:");
        pinLabel.setBounds(50, 100, 100, 30);
        createFrame.add(pinLabel);

        pinField = new JTextField();
        pinField.setBounds(150, 100, 150, 30);
        createFrame.add(pinField);

        JButton submitBtn = new JButton("Create");
        submitBtn.setBounds(100, 160, 200, 30);
        submitBtn.addActionListener(e -> createAccount());
        createFrame.add(submitBtn);

        createFrame.setVisible(true);
    }

    static void createAccount() {
        String name = usernameField.getText();
        int pin = Integer.parseInt(pinField.getText());

        try {
            String sql = "INSERT INTO customer(cname, balance, pass_code) VALUES (?, 1000, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, pin);
            int res = ps.executeUpdate();
            JOptionPane.showMessageDialog(frame, res == 1 ? "Account Created!" : "Failed to Create Account");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Username already exists!");
        }
    }

    static void showLoginAccount() {
        JFrame loginFrame = new JFrame("Login Account");
        loginFrame.setSize(400, 300);
        loginFrame.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 50, 100, 30);
        loginFrame.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 50, 150, 30);
        loginFrame.add(usernameField);

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setBounds(50, 100, 100, 30);
        loginFrame.add(pinLabel);

        pinField = new JTextField();
        pinField.setBounds(150, 100, 150, 30);
        loginFrame.add(pinField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(100, 160, 200, 30);
        loginBtn.addActionListener(e -> loginAccount());
        loginFrame.add(loginBtn);

        loginFrame.setVisible(true);
    }

    static void loginAccount() {
        String name = usernameField.getText();
        int pin = Integer.parseInt(pinField.getText());

        try {
            String sql = "SELECT * FROM customer WHERE cname = ? AND pass_code = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, pin);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                currentAcNo = rs.getInt("ac_no");
                showAccountDashboard(rs.getString("cname"));
            } else {
                JOptionPane.showMessageDialog(frame, "Login Failed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void showAccountDashboard(String name) {
        JFrame dashboard = new JFrame("Dashboard - " + name);
        dashboard.setSize(500, 500);
        dashboard.setLayout(null);

        JButton depositBtn = new JButton("Deposit Money");
        depositBtn.setBounds(50, 50, 200, 30);
        depositBtn.addActionListener(e -> depositMoneyUI());
        dashboard.add(depositBtn);

        JButton viewBalBtn = new JButton("View Balance");
        viewBalBtn.setBounds(50, 100, 200, 30);
        viewBalBtn.addActionListener(e -> viewBalance());
        dashboard.add(viewBalBtn);

        JButton transferBtn = new JButton("Transfer Money");
        transferBtn.setBounds(50, 150, 200, 30);
        transferBtn.addActionListener(e -> transferMoneyUI());
        dashboard.add(transferBtn);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(50, 200, 200, 30);
        logoutBtn.addActionListener(e -> dashboard.dispose());
        dashboard.add(logoutBtn);

        outputArea = new JTextArea();
        outputArea.setBounds(50, 250, 380, 150);
        dashboard.add(outputArea);

        dashboard.setVisible(true);
    }

    static void depositMoneyUI() {
        String amountStr = JOptionPane.showInputDialog("Enter Amount to Deposit:");
        if (amountStr != null && !amountStr.isEmpty()) {
            int amount = Integer.parseInt(amountStr);
            try {
                String sql = "UPDATE customer SET balance = balance + ? WHERE ac_no = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, amount);
                ps.setInt(2, currentAcNo);
                int res = ps.executeUpdate();
                outputArea.setText(res == 1 ? "Amount Deposited Successfully!" : "Deposit Failed!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void viewBalance() {
        try {
            String sql = "SELECT balance FROM customer WHERE ac_no = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, currentAcNo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int balance = rs.getInt("balance");
                outputArea.setText("Current Balance: Rs." + balance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void transferMoneyUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        receiverAcField = new JTextField();
        panel.add(new JLabel("Receiver A/c No:"));
        panel.add(receiverAcField);

        transferField = new JTextField();
        panel.add(new JLabel("Amount:"));
        panel.add(transferField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Transfer Money", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int receiverAc = Integer.parseInt(receiverAcField.getText());
            int amount = Integer.parseInt(transferField.getText());
            transferMoney(receiverAc, amount);
        }
    }

    static void transferMoney(int receiverAc, int amount) {
        try {
            con.setAutoCommit(false);

            String sql = "SELECT balance FROM customer WHERE ac_no = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, currentAcNo);
            ResultSet rs = ps.executeQuery();

            if (rs.next() && rs.getInt("balance") >= amount) {
                PreparedStatement debit = con.prepareStatement("UPDATE customer SET balance = balance - ? WHERE ac_no = ?");
                debit.setInt(1, amount);
                debit.setInt(2, currentAcNo);
                debit.executeUpdate();

                PreparedStatement credit = con.prepareStatement("UPDATE customer SET balance = balance + ? WHERE ac_no = ?");
                credit.setInt(1, amount);
                credit.setInt(2, receiverAc);
                credit.executeUpdate();

                con.commit();
                outputArea.setText("Transfer Successful!");
            } else {
                outputArea.setText("Insufficient Balance!");
            }
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}
