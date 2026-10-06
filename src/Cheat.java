/**
 * Cheat strategy: looks at the player's move and picks the symbol that beats it.
 */
public class Cheat implements Strategy
{
    /**
     * Returns the move that beats the player's move.
     *
     * @param playerMove the player's move: "R", "P" or "S"
     * @return the winning move, or "X" if the player's move is not recognized
     */
    @Override
    public String getMove(String playerMove)
    {
        String computerMove;
        switch (playerMove)
        {
            case "R":
                computerMove = "P";
                break;
            case "P":
                computerMove = "S";
                break;
            case "S":
                computerMove = "R";
                break;
            default:
                computerMove = "X";
                break;
        }
        return computerMove;
    }
}
