//java Manual "6|6 5|2 0|4 4|5 1|1 1|2 2|2" "6|5 6|2 6|3 0|0 5|1 5|0 3|0”

// java Manual "1|3 4|2 3|4" "1|4 3|5 4|5"
import java.util.ArrayList;

public class Manual {

	private static ArrayList<Tile> parseTiles(String tiles, int player) throws NumberFormatException {
		ArrayList<Tile> T = new ArrayList<>();
		String[] tokens   = tiles.trim().split("\\s+");
        int index = 0;
		for(String t: tokens) {
			String[] num = t.split("|");
			int x = Integer.parseInt(num[0]);
			int y = Integer.parseInt(num[2]);
			T.add(new Tile(x, y, player, index));
            index++;
		}
		return T;
	}

	public static void main(String[] args) {
		if(args.length != 2) {
			System.err.println("Usage: MainManual <tiles player1> <tiles player2>\n");
			System.exit(0);
		}
		ArrayList<Tile> p1_hand, p2_hand;

		try {
			p1_hand = parseTiles(args[0], 0);
			p2_hand = parseTiles(args[1], 1);
			System.out.println(p1_hand.size());
			System.out.println(p2_hand.size());
			Table table = new Table(p1_hand, p2_hand);
		} 
        catch(NumberFormatException e) {
			e.printStackTrace();
		}

	}
}