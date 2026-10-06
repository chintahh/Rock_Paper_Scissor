/**
 * Strategy interface for choosing the computer's move in Rock Paper Scissors.
 */
public interface Strategy
{
    /**
     * Determines the computer's move.
     *
     * @param playerMove the player's move: "R", "P" or "S"
     * @return the computer's move: "R", "P" or "S"
     */
    String getMove(String playerMove);
}
