
//TODO rimuovere played_tiles in quanto corrisponderebbe a Table.tiles?
//TODO decidere casualmente chi inizia? Ora inizia sempre Player1     DOPPIO 6

//? dove mettere le played_tiles? su Game o su Table?

/*
    far partire chi ha doppio 6 e poi essere in grado di capire chi è in grado di giocare una tessera,
    sostanzialmente gestire bene i turni che possono essere multipli
*/

package domino;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class Game {
    List<Tile> all_tiles;         // lista statica delle 28 tiles
    List<Tile> played_tiles;      
    List<Tile> unplayed_tiles;
    Player player1, player2;

    public Game() {
        all_tiles = new ArrayList<>();

        for (int i = 0; i <= 6; i++) {                  // generazione delle 28 tiles
            for (int j = i; j <= 6; j++) {
                all_tiles.add(new Tile(i, j));
            }
        }

        Collections.shuffle(all_tiles);

        List<Tile> player1_hand = new ArrayList<>();   
        List<Tile> player2_hand = new ArrayList<>();

        for (int i = 0; i < 14; i++) {                  // distribuzione mano iniziale
            player1_hand.add(all_tiles.remove(0));
            player2_hand.add(all_tiles.remove(0));
        }

        player1 = new Player(true, player1_hand);
        player2 = new Player(false, player2_hand);

        played_tiles = new ArrayList<>();
        unplayed_tiles = all_tiles;
    }
}