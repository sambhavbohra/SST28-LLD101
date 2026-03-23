import java.util.LinkedList;
import java.util.Queue;

public class Game {
    Queue<Player> players;
    Board board;
    IRuleStratergy ruleStratergy;

    public Game(int n,String rule) {
        this.players = new LinkedList<>();
        this.board = new Board(n);
        this.ruleStratergy = Factory.getStratergy(rule);
    }
    
    public void addPlayer(Player p) {
        players.add(p);
    }

    public void run() {
        while(players.size() > 1) {
            Player p = players.poll();
            int position = ruleStratergy.makeMove(p);
            System.out.println(p.name + " moved from " + p.position + " to " + position);
            p.position = position;
            int finalPosition = board.checkSnakeAndLadder(p);
            p.position = finalPosition;
            if(p.position == 100) {
                System.out.println(p.name + " reaches 100!");
                continue;
            }
            players.add(p);
        }
    }
}