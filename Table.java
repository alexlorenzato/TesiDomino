
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
    ArrayList<Tile> all_tiles;     
    ArrayList<Tile> played_tiles;
    ArrayList<Tile> reserve_tiles;
    ArrayList<ArrayList<Tile>> p_hands;
    int head, tail;
    int current_player; // 1:P1, 2:P2
    
    int max_tile, hand_size;

    public Table(int max_tile, int hand_size) {

        if (max_tile > 0 && hand_size >= 0) {
            this.max_tile  = max_tile;
            this.hand_size = hand_size;

            all_tiles     = new ArrayList<>();
            reserve_tiles = new ArrayList<>();
            played_tiles  = new ArrayList<>();
            p_hands       = new ArrayList<>();
            p_hands.add(new ArrayList<>());
            p_hands.add(new ArrayList<>());

            generateAllTiles();  
            dealPlayersHands();
            chooseStartingPlayer();

            startGame();
        }
        else { System.err.println("Error [Table]: invalid constructor parameters."); }
    }


    /*************************************/
    /*               ACTIONS             */
    /*************************************/    

    public void startGame(){
        printTableConfig();
        
        int move = -1;
        move = firstMove();
        playTile(move);

        printTableConfig();
        // eccezione prima mossa
        // while partita non finita
        // turno del giocatore attivo
            // capire che mosse può fare
            // sceglierne una a caso
                // giocare
                // passare
            // se non può farne
                // passare
            // verificare se partita finita
            // cambiare current_player (se..)
    }


    public int firstMove(){ 
        int max_val = -1, chosen_tile = -1;
        for (int i = 0; i < p_hands.get(current_player -1).size(); i++) {
            Tile tmp_tile = p_hands.get(current_player -1).get(i);
            if ((tmp_tile.val_1 == tmp_tile.val_2) && (tmp_tile.val_1 > max_val)) {
                max_val = tmp_tile.val_1;
                chosen_tile = i;
            }
        }
        return chosen_tile;
    }
    

    //todo manca un modo di selezionare il lato a cui voglio giocare
    public void playTile(int move){
        if(move != -1){
            Tile played_tile = p_hands.get(current_player -1).remove(move);
            played_tiles.add(played_tile);
        }
        else{
            System.err.println("Error [playTile]: Invalid move value.");
        }
    }
    

    /*************************************/
    /*               SETUP               */
    /*************************************/

    public void resetTable() {
        generateAllTiles();
        dealPlayersHands();
        chooseStartingPlayer();
    }
    

    private void generateAllTiles() {
        for (int i = 0; i <= max_tile; i++) {
            for (int j = i; j <= max_tile; j++) {
                all_tiles.add(new Tile(i, j));
            }
        }
        Collections.shuffle(all_tiles);
    }


    private void dealPlayersHands() {
        for(int i = 0; i < hand_size; i++){
            p_hands.get(0).add(all_tiles.remove(0));
            p_hands.get(1).add(all_tiles.remove(0));
        }
        reserve_tiles = all_tiles;
    }


    private void chooseStartingPlayer() {
        int max_val_p1 = -1, max_val_p2 = -1;

        for (int i = 0; i < p_hands.get(0).size(); i++) {
            Tile tmp_tile = p_hands.get(0).get(i);
            if ((tmp_tile.val_1 == tmp_tile.val_2) && (tmp_tile.val_1 > max_val_p1)) {
                max_val_p1 = tmp_tile.val_1;
            }
        }
        for (int i = 0; i < p_hands.get(1).size(); i++) {
            Tile tmp_tile = p_hands.get(1).get(i);
            if ((tmp_tile.val_1 == tmp_tile.val_2) && (tmp_tile.val_1 > max_val_p2)) {
                max_val_p2 = tmp_tile.val_1;
            }
        }

        if (max_val_p1 > max_val_p2) { current_player = 1; }
        else if (max_val_p2 > max_val_p1) { current_player = 2; } 
        else { resetTable(); }
    }


    /*************************************/
    /*               GETTER              */
    /*************************************/

    public int getHead() { return head; }
    public int getTail() { return tail; }
    public int getCurrentPlayer() { return current_player; }


    /*************************************/
    /*               PRINT               */
    /*************************************/

    public void printTableConfig() {

        System.out.println("____________________");
        System.out.println("Current Player: " + current_player);
        System.out.println("Players hands:");
        System.out.println("--- P1 ---");
        printTiles(p_hands.get(0));
        System.out.println("--- P2 ---");
        printTiles(p_hands.get(1));
        System.out.println("--- Unplayed ---");
        printTiles(reserve_tiles);
        System.out.println("--- Table ---");
        printTiles(played_tiles);
        System.out.println("____________________");
    }


    public void printTiles(ArrayList<Tile> tiles) {
        for (int i = 0; i < tiles.size(); i++) {
            Tile tmp_tile = tiles.get(i);
            Tile.printTile(tmp_tile);
        }
    }
}

