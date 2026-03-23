import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Factory {

    static Set<Integer> used = new HashSet<>();

    public static IRuleStratergy getStratergy(String rule) {
        if(rule.equalsIgnoreCase("easy")) {
            return new Easy();
        }
        return new Hard();
    }

     public static void populateSnakes(Map<Integer, Snake> snakes, int n) {

        Random rand = new Random();
        int count = 0;
        int size = n * n;

        while (count < n) {
            int start = rand.nextInt(size - 1) + 2;
            int end = rand.nextInt(start - 1) + 1;

            int startRow = (start - 1) / n;
            int endRow = (end - 1) / n;

            if (startRow <= endRow) continue;
            if (used.contains(start) || used.contains(end)) continue;

            Snake snake = new Snake();
            snake.start = start;
            snake.end = end;

            snakes.put(start, snake);
            used.add(start);
            used.add(end);

            count++;
        }
    }

    public static void populateLadders(Map<Integer, Ladder> ladders, int n) {

        Random rand = new Random();
        int count = 0;
        int size = n * n;

        while (count < n) {
            int start = rand.nextInt(size - 1) + 1;
            int end = rand.nextInt(size - start) + start + 1;

            int startRow = (start - 1) / n;
            int endRow = (end - 1) / n;

            if (startRow >= endRow) continue;
            if (used.contains(start) || used.contains(end)) continue;

            Ladder ladder = new Ladder();
            ladder.start = start;
            ladder.end = end;

            ladders.put(start, ladder);
            used.add(start);
            used.add(end);

            count++;
        }

        used.clear();
    }
}