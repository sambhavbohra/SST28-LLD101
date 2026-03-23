public class Easy implements IRuleStratergy {
    public int makeMove(Player p) {
        int currentPosition = p.position;
        int move = 0;

        while (true) {
            int dice = Dice.throwDice();
            System.out.println("dice: " + dice);
            move += dice;
            if (dice != 6) break;
        }

        int finalPosition = currentPosition + move;
        return Math.min(finalPosition, 100);
    }
}