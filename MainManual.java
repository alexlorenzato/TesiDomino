import java.util.ArrayList;

public class MainManual {
    public static void main(String[] args) {
    
        ArrayList<Tile> p1_hand = new ArrayList<>();
        ArrayList<Tile> p2_hand = new ArrayList<>();
        
        p1_hand.add(new Tile(1, 1, 0));
        p1_hand.add(new Tile(1, 2, 0)); 
        p1_hand.add(new Tile(2, 3, 0)); 
        p1_hand.add(new Tile(3, 4, 0)); 
        p1_hand.add(new Tile(5, 6, 0)); 
        
        //p2_hand.add(new Tile(4, 5, 1));  
        //p2_hand.add(new Tile(6, 6, 1)); 
        //p2_hand.add(new Tile(2, 2, 1)); 
        //p2_hand.add(new Tile(1, 3, 1)); 
        //p2_hand.add(new Tile(0, 5, 1)); 

        p2_hand.add(new Tile(0, 0, 1));
        p2_hand.add(new Tile(0, 0, 1));
        p2_hand.add(new Tile(0, 0, 1));
        p2_hand.add(new Tile(0, 0, 1));
        p2_hand.add(new Tile(0, 0, 1));

        Table table = new Table(p1_hand, p2_hand);
        
    }
}

