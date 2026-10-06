import javax.swing.SwingUtilities;

/**
 * Launches the Rock Paper Scissors game.
 */
public class RockPaperScissorsRunner
{
    /**
     * Program entry point.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            RockPaperScissorsFrame frame = new RockPaperScissorsFrame();
            frame.setVisible(true);
        });
    }
}
