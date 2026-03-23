public class Hard implements IRuleStratergy {
    public int makeMove(Player p) {
        int currentPosition = p.position;
        int move = 0;
        int consecutiveSixes = 0;

        while (true) {
            int dice = Dice.throwDice();
            System.out.println("dice: " + dice);
            if (dice == 6) {
                consecutiveSixes++;
                if (consecutiveSixes == 3) {
                    return currentPosition;
                }
            } else {
                consecutiveSixes = 0;
            }

            move += dice;
            if (dice != 6) break;
        }

        int finalPosition = currentPosition + move;
        return Math.min(finalPosition, 100);
    }
}