import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DifficultyMenu {

    JFrame frame = new JFrame("Select Difficulty");

    public DifficultyMenu() {
        frame.setUndecorated(true);
        frame.setSize(450, 320);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                Color c1 = new Color(60, 100, 160);
                Color c2 = new Color(110, 140, 220);
                g2.setPaint(new GradientPaint(0, 0, c1, 0, getHeight(), c2));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        panel.setLayout(new java.awt.GridBagLayout());
        panel.setBorder(new EmptyBorder(40, 60, 40, 60));

        JLabel title = new JLabel("Select Difficulty");
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(Color.WHITE);

        JButton easy = createButton("Easy");
        JButton medium = createButton("Medium");
        JButton hard = createButton("Hard");

        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(title, gbc);
        gbc.gridy = 1; gbc.insets = new java.awt.Insets(20,0,0,0);
        panel.add(easy, gbc);
        gbc.gridy = 2; gbc.insets = new java.awt.Insets(10,0,0,0);
        panel.add(medium, gbc);
        gbc.gridy = 3; gbc.insets = new java.awt.Insets(10,0,0,0);
        panel.add(hard, gbc);

        frame.getContentPane().add(panel);
        frame.setVisible(true);

        easy.addActionListener(e -> startSudoku("easy"));
        medium.addActionListener(e -> startSudoku("medium"));
        hard.addActionListener(e -> startSudoku("hard"));
    }

    JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(30, 144, 255));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(260, 56));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(65,105,225)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(new Color(30,144,255)); }
        });
        return btn;
    }

    void startSudoku(String difficulty) {
        frame.dispose();
        new Sudoku(difficulty);
    }
}
