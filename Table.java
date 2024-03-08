
/*
creare metodi per gestire la partita:
fare 1 mossa (sapendo chi è giocatore corrente)
controllare che mossa sia legale
possibilità di non fare una mossa (se può giocare, DEVE farlo)
capire chi è 1 giocatore
fare undo quante volte si vuole (utile per minimax), serve quindi pila che memorizzi mosse per poter tornare indietro
gestire i turni

attenzione, ricordare anche quanddo è stato slatato il turno perché facendo undo devo ricordarmi di chi era la tessera
*/


/*
creare una classe, istanziare oggetto e passargli configurazione
quindi ci sarà 1 e secondo giocatore
noi lanciamo minimax su questo gioco, dove la radice sarà il 6,6
avremo un risultato e avremo il vincitore

l'alphabeta ritorna chi vince

l'alfabeta pruntin USA la classe table, però è esterna
il main alloca la table con la configurazuibne di gioco e lancia alfabeta pruning con quella configurazione e da lì otteniamo il vincitore
vogliamo sapere anche quanti punti vengono fatti dai 2 giocatori

    far partire chi ha doppio 6 e poi essere in grado di capire chi è in grado di giocare 
    una tessera,
    sostanzialmente gestire bene i turni che possono essere multipli

table deve permettere di giocare anche con meno di 14 tessere
gestire anche la possibilità che non ci sia doppio6 e anche che nessuno abbia coppia quindi resettare la partita


? è giusto mettere i metodi private?
? è giusto fare le funzioni void e modificare lì i campi, o sarebbe meglio fare ad esempio: starting_player = chooseStartingPlayer() ?
*/

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Table {
    List<Tile> all_tiles;     
    List<Tile> played_tiles;
    List<Tile> reserve_tiles;
    List<Tile> p1_hand, p2_hand;
    int head, tail;
    int current_player; // 1:P1, 2:P2
    
    int max_tile, hand_size;

    public Table(int max_tile, int hand_size) {

        if (max_tile > 0 && hand_size >= 0) {
            this.max_tile = max_tile;
            this.hand_size = hand_size;

            generateAllTiles(max_tile);
            //printTiles(all_tiles);
            dealPlayersHands(hand_size);
            chooseStartingPlayer();
            getCurrentPlayer();

            played_tiles = new ArrayList<>();
            reserve_tiles = all_tiles;
        }
        else { System.err.println("Error: invalid constructor parameters."); }
    }

    
    public void playTile(){
        //? da sostituire alle funzioni addHead e addTail?
        // controllare legalità della mossa
    }

    
    public void addHead(Tile tile, int head){
        played_tiles.add(0, tile);
        this.head = head;
    }

    
    public void addTail(Tile tile, int tail) {
        played_tiles.add(tile);
        this.tail = tail;
    }
    

    public int getHead() { return head; }

    
    public int getTail() { return tail; }
    

    public int getCurrentPlayer() { return current_player; }

    
    public void resetTable() {
        generateAllTiles(max_tile);
        dealPlayersHands(hand_size);
        chooseStartingPlayer();
    }
    

    private void generateAllTiles(int max_tile) {
        all_tiles = new ArrayList<>();

        for (int i = 0; i <= max_tile; i++) {
            for (int j = i; j <= max_tile; j++) {
                all_tiles.add(new Tile(i, j));
            }
        }
        Collections.shuffle(all_tiles);
    }


    private void dealPlayersHands(int hand_size) {
        p1_hand = new ArrayList<>();
        p2_hand = new ArrayList<>();

        for(int i = 0; i < hand_size; i++){
            p2_hand.add(all_tiles.remove(0));
            p1_hand.add(all_tiles.remove(0));
        }
    }


    private void chooseStartingPlayer() {
        int max_val_p1 = -1, max_val_p2 = -1;

        for (int i = 0; i < p1_hand.size(); i++) {
            Tile tmp_tile = p1_hand.get(i);
            if ((tmp_tile.val_1 == tmp_tile.val_2) && (tmp_tile.val_1 > max_val_p1)) {
                max_val_p1 = tmp_tile.val_1;
            }
        }
        for (int i = 0; i < p2_hand.size(); i++) {
            Tile tmp_tile = p2_hand.get(i);
            if ((tmp_tile.val_1 == tmp_tile.val_2) && (tmp_tile.val_1 > max_val_p2)) {
                max_val_p2 = tmp_tile.val_1;
            }
        }

        if (max_val_p1 > max_val_p2) { current_player = 1; }
        else if (max_val_p2 > max_val_p1) { current_player = 2; } 
        else { resetTable(); }
    }
    

    public void printTableConfig() {
        System.out.println("____________________");
        System.out.println("Current Player: " + current_player);
        System.out.println("Players hands:");
        System.out.println("--- P1 ---");
        printTiles(p1_hand);
        System.out.println("--- P2 ---");
        printTiles(p2_hand);
        System.out.println("--- Unplayed ---");
        printTiles(reserve_tiles);
        System.out.println("____________________");
    }


    public void printTiles(List<Tile> tiles) {
        for (int i = 0; i < tiles.size(); i++) {
            Tile tmp_tile = tiles.get(i);
            Tile.printTile(tmp_tile);
        }
    }
}