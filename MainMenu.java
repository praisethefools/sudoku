import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Timer;

public class MainMenu {

    JFrame frame = new JFrame("Final Project - Sudoku Game");
    JButton playButton = new RoundedButton("Play");
    JButton quitButton = new RoundedButton("Quit");

    public MainMenu() {
        // undecorated supaya setOpacity aman ketika fitur fade dipakai
        frame.setUndecorated(true);
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth(), h = getHeight();
                Color color1 = new Color(70, 130, 180);
                Color color2 = new Color(100, 149, 237);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new java.awt.GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(40, 60, 40, 60));

        JLabel title = new JLabel("SUDOKU");
        title.setFont(new Font("Segoe UI", Font.BOLD, 48));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Classic Puzzle Game");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subtitle.setForeground(new Color(230, 230, 230));

        // layout center vertical
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = java.awt.GridBagConstraints.CENTER;
        mainPanel.add(title, gbc);
        gbc.gridy = 1;
        mainPanel.add(subtitle, gbc);
        gbc.gridy = 2; gbc.ipady = 30;
        mainPanel.add(playButton, gbc);
        gbc.gridy = 3; gbc.ipady = 0; gbc.insets = new java.awt.Insets(10,0,0,0);
        mainPanel.add(quitButton, gbc);

        configureButton(playButton);
        configureButton(quitButton);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        // Aksi tombol
        playButton.addActionListener(e -> fadeOut(frame, () -> new DifficultyMenu()));
        quitButton.addActionListener(e -> System.exit(0));
    }

    void configureButton(final JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(30, 144, 255));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(240, 56));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(65,105,225)); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(new Color(30,144,255)); }
        });
    }

    // Fade out animation: menggunakan javax.swing.Timer
    void fadeOut(final JFrame window, final Runnable afterFade) {
        Timer timer = new Timer(15, null);
        timer.addActionListener(new ActionListener() {
            float opacity = 1.0f;
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= 0.04f;
                if (opacity <= 0f) {
                    timer.stop();
                    try { window.dispose(); } catch (Exception ex) { /* ignore */ }
                    afterFade.run();
                } else {
                    try {
                        window.setOpacity(Math.max(0f, opacity));
                    } catch (UnsupportedOperationException ex) {
                        // jika tidak didukung, langsung tutup dan lanjut
                        timer.stop();
                        window.dispose();
                        afterFade.run();
                    }
                }
            }
        });
        timer.start();
    }

    // RoundedButton inner class
    class RoundedButton extends JButton {
        RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            super.paintComponent(g);
            g2.dispose();
        }
        @Override
        public void updateUI() {
            super.updateUI();
            setOpaque(false);
            setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        }
    }
}
