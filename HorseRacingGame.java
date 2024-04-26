import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class HorseRacingGame extends JFrame {
    private JPanel trackPanel;
    private JButton startRaceButton, betsButton;
    private JLabel[] horseLabels;
    private Image[] horseImages;
    private int numHorses = 3;
    private int balance = 50;
    private int[] bets;
    private boolean raceStarted = false;

    public HorseRacingGame() {
        setTitle("Horse Racing Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLayout(new BorderLayout());

        horseImages = new Image[numHorses];
        for (int i = 0; i < numHorses; i++) {
            try {
                File horseFile = new File("horse" + (i + 1) + ".png");
                Image horseImage = ImageIO.read(horseFile).getScaledInstance(100, 75, Image.SCALE_DEFAULT);
                horseImages[i] = horseImage;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        trackPanel = new JPanel();
        trackPanel.setBackground(new Color(34, 139, 34)); 
        trackPanel.setLayout(null); 
        add(trackPanel, BorderLayout.CENTER);

        horseLabels = new JLabel[numHorses];
        bets = new int[numHorses];

        for (int i = 0; i < numHorses; i++) {
            horseLabels[i] = new JLabel(new ImageIcon(horseImages[i]));
            horseLabels[i].setSize(100, 75);
            horseLabels[i].setLocation(50, 50 + i * 100);
            trackPanel.add(horseLabels[i]);
        }

        startRaceButton = new JButton("Start Race");
        startRaceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startRace();
            }
        });
        add(startRaceButton, BorderLayout.SOUTH);

        betsButton = new JButton("Bets");
        betsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayBets();
            }
        });
        add(betsButton, BorderLayout.NORTH);

        setVisible(true);
    }

    private void startRace() {
        raceStarted = true;
        Timer timer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < numHorses; i++) {
                    int newX = horseLabels[i].getX() + (int) (Math.random() * 10);
                    horseLabels[i].setLocation(newX, horseLabels[i].getY());
                    if (newX >= trackPanel.getWidth() - horseLabels[i].getWidth()) {
                        ((Timer) e.getSource()).stop();
                        showRaceOutcome(i);
                    }
                }
            }
        });
        timer.start();
    }

    private void displayBets() {
        if (raceStarted) {
            StringBuilder betSummary = new StringBuilder("<html>Your current bets:<br>");
            for (int i = 0; i < numHorses; i++) {
                if (bets[i] > 0) {
                    betSummary.append("Horse ").append(i + 1).append(": $").append(bets[i]).append("<br>");
                }
            }
            betSummary.append("</html>");
            JOptionPane.showMessageDialog(this, betSummary.toString());
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        JComboBox<String> horseSelection = new JComboBox<>();
        for (int i = 0; i < numHorses; i++) {
            horseSelection.addItem("Horse " + (i + 1));
        }
        JTextField betAmountField = new JTextField(10);
        JLabel balanceLabel = new JLabel("Current balance: $" + balance);
        panel.add(balanceLabel);
        panel.add(new JLabel("Select horse:"));
        panel.add(horseSelection);
        panel.add(new JLabel("Enter bet amount:"));
        panel.add(betAmountField);
        int result = JOptionPane.showConfirmDialog(null, panel, "Place Bet", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int bet = Integer.parseInt(betAmountField.getText());
            if (bet <= balance && bet > 0) {
                int selectedHorseIndex = horseSelection.getSelectedIndex();
                bets[selectedHorseIndex] += bet;
                balance -= bet;
                JOptionPane.showMessageDialog(this, "Bet placed on Horse " + (selectedHorseIndex + 1) + " for $" + bet);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid bet amount.");
            }
        }
    }

    private void showRaceOutcome(int winningHorse) {
        raceStarted = false;
        for (int i = 0; i < numHorses; i++) {
            if (i == winningHorse && bets[i] > 0) {
                int winnings = bets[i] * 2;
                balance += winnings;
                JOptionPane.showMessageDialog(this,
                        "Horse " + (i + 1) + " wins!\n" +
                        "You won $" + winnings + "!");
                bets[i] = 0; 
            } else if (bets[i] > 0) {
                JOptionPane.showMessageDialog(this, "Horse " + (i + 1) + " did not win. You lost $" + bets[i] + ".");
                bets[i] = 0;
            }
        }
        JOptionPane.showMessageDialog(this, "Updated balance: $" + balance);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new HorseRacingGame();
            }
        });
    }
}
