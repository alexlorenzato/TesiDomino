
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
import java.util.Random;

public class Table {
    ArrayList<Tile> all_tiles;     
    ArrayList<Tile> played_tiles;
    ArrayList<Tile> reserve_tiles;
    ArrayList<ArrayList<Tile>> p_hands;
    int head, tail;
    int current_player;   // 0:P1, 1:P2
    GameState game_state;
    
    int max_tile, hand_size;

    public Table(int max_tile, int hand_size) {

        if (max_tile > 0 && hand_size >= 0) {

            game_state = GameState.OPEN;
            this.max_tile  = max_tile;
            this.hand_size = hand_size;
            head = -1;
            tail = -1;

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
        int[] move = new int[] {-1, -1};     // {index of the tile in player hands, head=0/tail=1 of table}
        int turn = 0;

        System.out.println("\n-------------------- START GAME ---------------------");

        printTableConfig();
        
        firstMove();
        passTurn();

        while(game_state == GameState.OPEN){
            printTableConfig();

            move = randomMove();
            playTile(move);
            passTurn();

            turn ++;
        }
        
        /*
        int counter = 0;
        while(counter < 5){ 
            printTableConfig();
            counter++;
            move = randomMove();
            if(move[0] == -1) { playTile(move); }
            passTurn();

            //printPlayedTiles();
        }*/

        // eccezione prima mossa
        // while partita non finita
        // turno del giocatore attivo
            // capire che mosse può fare
            // sceglierne una 
                // giocare
                // passare
            // se non può farne
                // passare
            // verificare se partita finita
            // cambiare current_player (se..)
    }


    public void firstMove(){ 
        int max_tile = -1, index_tile = -1;
        for (int i = 0; i < p_hands.get(current_player).size(); i++) {
            Tile tmp_tile = p_hands.get(current_player).get(i);
            if ((tmp_tile.val_1 == tmp_tile.val_2) && (tmp_tile.val_1 > max_tile)) {
                max_tile = tmp_tile.val_1;
                index_tile = i;
            }
        }

        Tile played_tile = p_hands.get(current_player).remove(index_tile);
        played_tiles.add(played_tile);
        head = played_tile.val_1;
        tail = played_tile.val_2;
    }


//todo rimuovere tile dalla mano giocatore
    public int[] randomMove(){

        ArrayList<int[]> moves = availableMoves(current_player);

        if (!moves.isEmpty()) {
            Random rand = new Random();
            
            int random_index = rand.nextInt(moves.size());
            
            return moves.get(random_index);
        } else {
            int[] move = new int[2];
            move[0] = -1;
            move[1] = -1; 
            return move; 
        }
    }
    

    public void playTile(int[] move){
        Tile played_tile = p_hands.get(current_player).remove(move[0]);

        if(move[1] == 0){                            // head
            played_tiles.add(0, played_tile);
            if(played_tile.val_1 == head){
                head = played_tile.val_2;
            }
            else{ head = played_tile.val_1; }
        }
        else{                                        // tail
            played_tiles.add(played_tile);
            if(played_tile.val_1 == tail) {
                tail = played_tile.val_2;
            }
            else{ tail = played_tile.val_1; }
        }
        System.out.println("playTile(): " + played_tile.val_1 + "|" + played_tile.val_2);
    }
    

    public void passTurn(){
        if(current_player == 1){
            if(availableMoves(0).size() > 0){ 
                current_player = 0;
            }
            else if(availableMoves(1).size() == 0){
                endGame();
            }
        }
        else {
            if(availableMoves(1).size() > 0){
                current_player = 1;
            } 
            else if(availableMoves(0).size() == 0){
                endGame();
            }
        }
    }
//? il controllo availableMoves deve venire fatto dopo che è stata fiocata la tile del turno corrente, questo durantei l passTurn
    
    public ArrayList<int[]> availableMoves(int player){
        
        ArrayList<int[]> moves = new ArrayList<>();

        for (int i = 0; i < p_hands.get(player).size(); i++) {
            Tile tmp_tile = p_hands.get(player).get(i);
            if (tmp_tile.val_1 == head) {
                int[] move = new int[2];
                move[0] = i;
                move[1] = 0; 
                moves.add(move); 
            }
            if((tmp_tile.val_2 != tmp_tile.val_1) && (tmp_tile.val_2 == head)){  // look if the other value is playable, but avoid duplicates with double-number tiles
                int[] move = new int[2];
                move[0] = i;
                move[1] = 0; 
                moves.add(move); 
            }
            if (tmp_tile.val_1 == tail) {
                int[] move = new int[2];
                move[0] = i;
                move[1] = 1; 
                moves.add(move); 
            }
            if((tmp_tile.val_2 != tmp_tile.val_1) && (tmp_tile.val_2 == tail)){  // look if the other value is playable, but avoid duplicates with double-number tiles
                int[] move = new int[2];
                move[0] = i;
                move[1] = 1; 
                moves.add(move); 
            }
        }
        return moves;
    }


    public void endGame(){
        game_state = GameState.ENDED;
        System.out.println("Game Over");
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
        //todo mettere owner
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

        if      (max_val_p1 > max_val_p2) { current_player = 0; }
        else if (max_val_p2 > max_val_p1) { current_player = 1; } 
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
        System.out.println();
        System.out.println("----------------------");
        System.out.println("Current Player: " + current_player);
        System.out.print("P1:  ");
        printTiles(p_hands.get(0));
        System.out.print("P2:  ");
        printTiles(p_hands.get(1));
        System.out.print("UP:  ");
        printTiles(reserve_tiles);
        System.out.print("TB:  ");
        printTiles(played_tiles);
        System.out.println("head: " + head + " tail: " + tail);
        System.out.println("----------------------");
        System.out.println();
    }


    public void printTiles(ArrayList<Tile> tiles) {
        for (int i = 0; i < tiles.size(); i++) {
            Tile tmp_tile = tiles.get(i);
            Tile.printTile(tmp_tile);
            System.out.print(" - ");
        }
        System.out.println();
    }


    public void printAvailableMoves(int player){
        ArrayList<int[]> moves = availableMoves(player);
        for(int i = 0; i < moves.size(); i++){
            Tile tmp = p_hands.get(player).get(moves.get(i)[0]);
            Tile.printTile(tmp);
        }
    }


    public void printPlayedTiles(){
        for (int i = 0; i < played_tiles.size(); i++) {
            Tile tmp_tile = played_tiles.get(i);
            System.out.print(tmp_tile.val_1 + "|" + tmp_tile.val_2 + " - ");
        }
    }
}

