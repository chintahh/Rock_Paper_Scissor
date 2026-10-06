import java.util.Random;

/**
 * Random strategy: picks Rock, Paper or Scissors with equal probability.
 * Named RandomStrategy to avoid clashing with java.util.Random.
 */
public class RandomStrategy implements Strategy
{
    private final Random rnd = new Random();

    /**
     * Returns a random move, ignoring the player's move.
     *
     * @param playerMove the player's move (not used)
     * @return "R", "P" or "S"
     */
    @Override
    public String getMove(String playerMove)
    {
        String[] moves = {"R", "P", "S"};
        return moves[rnd.nextInt(3)];
    }
}
