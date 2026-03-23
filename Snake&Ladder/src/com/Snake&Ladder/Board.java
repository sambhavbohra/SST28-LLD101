import java.util.HashMap;
import java.util.Map;

public class Board {
    int n;
    Map<Integer,Snake> snakes;
    Map<Integer,Ladder> ladders;
    public Board(int n) {
        this.n = n;
        this.snakes = new HashMap<>();
        this.ladders = new HashMap<>();
        Factory.populateSnakes(snakes,n);
        Factory.populateLadders(ladders,n);
    }

    public int checkSnakeAndLadder(Player p) {
        if(snakes.containsKey(p.position)) {
            int finalPosition = snakes.get(p.position).end;
            System.out.println("stepped on snake and gone to " + finalPosition);
            return finalPosition;
        }
        if(ladders.containsKey(p.position)) {
            int finalPosition = ladders.get(p.position).end;
            System.out.println("stepped on ladder and gone to " + finalPosition);
            return finalPosition;
        }
        return p.position;
    } 

}