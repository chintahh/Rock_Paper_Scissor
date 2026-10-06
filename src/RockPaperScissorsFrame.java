import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * GUI Rock Paper Scissors game. The computer picks its move using one of
 * several Strategy implementations, chosen by probability each round.
 */
public class RockPaperScissorsFrame extends JFrame
{
    private static final String[] MOVES = {"R", "P", "S"};

    // Game state
    private int playerWins = 0;
    private int computerWins = 0;
    private int ties = 0;
    private final int[] playerCounts = new int[3]; // times player used R, P, S
    private String lastPlayerMove = null;           // null until the first round is played

    // Strategies: Cheat and Random are external, the other three are inner classes
    private final Strategy cheat = new Cheat();
    private final Strategy random = new RandomStrategy();
    private final Strategy leastUsed = new LeastUsed();
    private final Strategy mostUsed = new MostUsed();
    private final Strategy lastUsed = new LastUsed();
    private final Random rnd = new Random();

    // GUI components
    private final JTextField playerWinsField = new JTextField("0", 6);
    private final JTextField computerWinsField = new JTextField("0", 6);
    private final JTextField tiesField = new JTextField("0", 6);
    private final JTextArea resultsArea = new JTextArea(10, 40);

    /**
     * Builds the game window.
     */
    public RockPaperScissorsFrame()
    {
        super("Rock Paper Scissors Game");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        add(buildButtonPanel(), BorderLayout.NORTH);
        add(buildStatsPanel(), BorderLayout.CENTER);
        add(buildResultsPanel(), BorderLayout.SOUTH);

        addWindowListener(new java.awt.event.WindowAdapter()
        {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e)
            {
                confirmQuit();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    // ---------------------- GUI construction ----------------------

    private JPanel buildButtonPanel()
    {
        JPanel panel = new JPanel(new GridLayout(1, 4, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Choose your move"));

        MoveListener listener = new MoveListener(); // one listener for R, P and S

        String[] names = {"Rock", "Paper", "Scissors"};
        for (int i = 0; i < 3; i++)
        {
            JButton b = new JButton(names[i], loadIcon(names[i].toLowerCase(), i));
            b.setActionCommand(MOVES[i]);
            b.setVerticalTextPosition(SwingConstants.BOTTOM);
            b.setHorizontalTextPosition(SwingConstants.CENTER);
            b.addActionListener(listener);
            panel.add(b);
        }

        JButton quit = new JButton("Quit");
        quit.addActionListener(e -> confirmQuit());
        panel.add(quit);
        return panel;
    }

    private JPanel buildStatsPanel()
    {
        JPanel panel = new JPanel(new GridLayout(1, 6, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Stats"));

        JTextField[] fields = {playerWinsField, computerWinsField, tiesField};
        String[] labels = {"Player Wins:", "Computer Wins:", "Ties:"};
        for (int i = 0; i < 3; i++)
        {
            fields[i].setEditable(false);
            fields[i].setHorizontalAlignment(JTextField.CENTER);
            panel.add(new JLabel(labels[i], SwingConstants.RIGHT));
            panel.add(fields[i]);
        }
        return panel;
    }

    private JPanel buildResultsPanel()
    {
        resultsArea.setEditable(false);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Results"));
        panel.add(new JScrollPane(resultsArea), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Loads rock.png / paper.png / scissors.png from the classpath (src folder).
     * If the image is missing, draws a simple placeholder icon instead.
     *
     * @param name  base file name without extension
     * @param index 0 = rock, 1 = paper, 2 = scissors
     * @return an icon for the button
     */
    private ImageIcon loadIcon(String name, int index)
    {
        URL url = getClass().getResource("/" + name + ".png");
        if (url != null)
        {
            Image img = new ImageIcon(url).getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (index == 0) // rock
        {
            g.setColor(Color.GRAY);
            g.fillOval(8, 12, 48, 42);
        }
        else if (index == 1) // paper
        {
            g.setColor(Color.WHITE);
            g.fillRect(14, 6, 36, 52);
            g.setColor(Color.DARK_GRAY);
            g.drawRect(14, 6, 36, 52);
            for (int y = 18; y < 52; y += 10) g.drawLine(20, y, 44, y);
        }
        else // scissors
        {
            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(5));
            g.drawLine(12, 8, 50, 56);
            g.drawLine(52, 8, 14, 56);
        }
        g.dispose();
        return new ImageIcon(img);
    }

    // ---------------------- Game logic ----------------------

    /** Single listener shared by the Rock, Paper and Scissors buttons. */
    private class MoveListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            playRound(ae.getActionCommand()); // "R", "P" or "S"
        }
    }

    /**
     * Plays one round: picks a strategy by probability, gets the computer's
     * move, updates stats and appends the result to the text area.
     *
     * @param playerMove "R", "P" or "S"
     */
    private void playRound(String playerMove)
    {
        int roll = rnd.nextInt(100) + 1; // 1 - 100 inclusive
        Strategy strategy;
        String strategyName;
        if (roll <= 10)      { strategy = cheat;     strategyName = "Cheat"; }
        else if (roll <= 30) { strategy = leastUsed; strategyName = "Least Used"; }
        else if (roll <= 50) { strategy = mostUsed;  strategyName = "Most Used"; }
        else if (roll <= 70) { strategy = lastUsed;  strategyName = "Last Used"; }
        else                 { strategy = random;    strategyName = "Random"; }

        String computerMove = strategy.getMove(playerMove);

        // Record the player's move only after the strategy has used the earlier history
        playerCounts[indexOf(playerMove)]++;
        lastPlayerMove = playerMove;

        String line;
        if (playerMove.equals(computerMove))
        {
            ties++;
            line = name(playerMove) + " vs " + name(computerMove)
                    + " (Tie! Computer: " + strategyName + ")";
        }
        else if (beats(playerMove).equals(computerMove)) // computer played the move that beats the player's
        {
            computerWins++;
            line = name(computerMove) + " " + verb(computerMove) + " " + name(playerMove).toLowerCase()
                    + " (Computer wins! Computer: " + strategyName + ")";
        }
        else
        {
            playerWins++;
            line = name(playerMove) + " " + verb(playerMove) + " " + name(computerMove).toLowerCase()
                    + " (Player wins! Computer: " + strategyName + ")";
        }

        resultsArea.append(line + "\n");
        resultsArea.setCaretPosition(resultsArea.getDocument().getLength());
        playerWinsField.setText(String.valueOf(playerWins));
        computerWinsField.setText(String.valueOf(computerWins));
        tiesField.setText(String.valueOf(ties));
    }

    private void confirmQuit()
    {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?",
                "Quit", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION)
        {
            System.exit(0);
        }
    }

    private static int indexOf(String move)
    {
        return move.equals("R") ? 0 : move.equals("P") ? 1 : 2;
    }

    /** @return the move that beats the given move */
    private static String beats(String move)
    {
        return move.equals("R") ? "P" : move.equals("P") ? "S" : "R";
    }

    private static String name(String move)
    {
        return move.equals("R") ? "Rock" : move.equals("P") ? "Paper" : "Scissors";
    }

    private static String verb(String winningMove)
    {
        return winningMove.equals("R") ? "breaks" : winningMove.equals("P") ? "covers" : "cuts";
    }

    // ---------------------- Inner class strategies ----------------------

    /**
     * Least Used: assumes the player will soon use the symbol they've used least,
     * so the computer plays the symbol that beats it.
     */
    private class LeastUsed implements Strategy
    {
        @Override
        public String getMove(String playerMove)
        {
            int min = Math.min(playerCounts[0], Math.min(playerCounts[1], playerCounts[2]));
            List<Integer> candidates = new ArrayList<>();
            for (int i = 0; i < 3; i++)
            {
                if (playerCounts[i] == min) candidates.add(i); // break ties randomly
            }
            int pick = candidates.get(rnd.nextInt(candidates.size()));
            return beats(MOVES[pick]);
        }
    }

    /**
     * Most Used: assumes the player will repeat their favorite symbol,
     * so the computer plays the symbol that beats it.
     */
    private class MostUsed implements Strategy
    {
        @Override
        public String getMove(String playerMove)
        {
            int max = Math.max(playerCounts[0], Math.max(playerCounts[1], playerCounts[2]));
            List<Integer> candidates = new ArrayList<>();
            for (int i = 0; i < 3; i++)
            {
                if (playerCounts[i] == max) candidates.add(i);
            }
            int pick = candidates.get(rnd.nextInt(candidates.size()));
            return beats(MOVES[pick]);
        }
    }

    /**
     * Last Used: plays the symbol the player used on the previous round.
     * On the first round there is no history, so it picks randomly.
     */
    private class LastUsed implements Strategy
    {
        @Override
        public String getMove(String playerMove)
        {
            if (lastPlayerMove == null)
            {
                return MOVES[rnd.nextInt(3)];
            }
            return lastPlayerMove;
        }
    }
}
