import java.util.ArrayList;

public class MainManual {
    public static void main(String[] args) {
    
        ArrayList<Tile> p1_hand = new ArrayList<>();
        ArrayList<Tile> p2_hand = new ArrayList<>();
		/*int head = 3, tail = 4, starting_player = 0;

        p1_hand.add(new Tile(3, 4, 0, 0));
        p1_hand.add(new Tile(3, 5, 0, 1)); 
        p1_hand.add(new Tile(4, 6, 0, 2)); 

        p2_hand.add(new Tile(2, 4, 1, 0));
        p2_hand.add(new Tile(2, 3, 1, 1));
        p2_hand.add(new Tile(4, 5, 1, 2));*/

		/*int head = 1, tail = 4, starting_player = 0;

        p1_hand.add(new Tile(1, 2, 0, 0));
        p1_hand.add(new Tile(3, 4, 0, 1));
        p1_hand.add(new Tile(6, 6, 0, 2));
		
		p2_hand.add(new Tile(3, 5, 1, 0));
		p2_hand.add(new Tile(6, 6, 1, 1));*/

		/*int head = 3, tail = 4, starting_player = 0;

        p1_hand.add(new Tile(3, 4, 0, 0));
        p1_hand.add(new Tile(1, 3, 0, 1));
        p1_hand.add(new Tile(2, 4, 0, 2));
		
		p2_hand.add(new Tile(2, 5, 1, 0));
		p2_hand.add(new Tile(4, 6, 1, 1));*/

		int head = 3, tail = 4, starting_player = 0;   //prova "finale"

        p1_hand.add(new Tile(3, 4, 0, 0));
        p1_hand.add(new Tile(1, 3, 0, 1));
        p1_hand.add(new Tile(2, 4, 0, 2));
		
		p2_hand.add(new Tile(1, 4, 1, 0));
		p2_hand.add(new Tile(3, 5, 1, 1));
		p2_hand.add(new Tile(4, 5, 1, 2));
        
		Table table = new Table(p1_hand, p2_hand, head, tail, starting_player);

		/*int head = 4, tail = 3, starting_player = 0;   //caso buggato

        p1_hand.add(new Tile(2, 2, 0, 0));
        p1_hand.add(new Tile(1, 0, 0, 1));
        p1_hand.add(new Tile(1, 4, 0, 2));
        p1_hand.add(new Tile(5, 0, 0, 3));
		
		p2_hand.add(new Tile(1, 1, 1, 0));
		p2_hand.add(new Tile(5, 2, 1, 1));
		p2_hand.add(new Tile(3, 1, 1, 2));
        
		Table table = new Table(p1_hand, p2_hand, head, tail, starting_player);*/

		/*int head = 1, tail = 4, starting_player = 0;   //caso buggato numero 2

        p1_hand.add(new Tile(3, 1, 0, 0));
        p1_hand.add(new Tile(2, 2, 0, 1));
        p1_hand.add(new Tile(1, 2, 0, 2));
		
		p2_hand.add(new Tile(3, 5, 1, 0));
		p2_hand.add(new Tile(3, 0, 1, 1));
		p2_hand.add(new Tile(2, 3, 1, 2));
		p2_hand.add(new Tile(2, 5, 1, 3));
        
		Table table = new Table(p1_hand, p2_hand, head, tail, starting_player);*/
    }
}




/*
 
    VERSIONE PROF

  lanciabile da riga di comando con:  java MainManual "6|6 5|2 0|4 4|5 1|1 1|2 2|2" "6|5 6|2 6|3 0|0 5|1 5|0 3|0”

import java.util.ArrayList;

public class MainManual {

	private static ArrayList<Tile> parseTiles(String tiles, int player) throws NumberFormatException {
		ArrayList<Tile> T = new ArrayList<>();
		String[] tokens   = tiles.trim().split("\\s+");
		for(String t: tokens) {
			String[] num = t.split("|");
			int x = Integer.parseInt(num[0]);
			int y = Integer.parseInt(num[2]);
			T.add(new Tile(x, y, player));
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
			p1_hand = parseTiles(args[0],0);
			p2_hand = parseTiles(args[1],1);
			System.out.println(p1_hand.size());
			System.out.println(p2_hand.size());
			Table table = new Table(p1_hand, p2_hand);
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}

	}
}


 */