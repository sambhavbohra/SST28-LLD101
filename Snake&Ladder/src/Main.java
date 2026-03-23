public class Main {
    public static void main(String[] args) {
        Game g = new Game(10, "easy");
        g.addPlayer(new Player("ram"));
        g.addPlayer(new Player("raj"));
        g.addPlayer(new Player("roj"));
        g.run();
    }
}