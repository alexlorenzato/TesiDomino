
/*
fare undo quante volte si vuole (utile per minimax), serve pila che memorizzi mosse per poter tornare indietro
attenzione, ricordare anche quando è stato saltato il turno perché facendo undo devo ricordarmi di chi era la tessera


noi lanciamo minimax su questo gioco, dove la radice sarà il 6,6, avremo un risultato e si ritorna il vincitore
l'alfabeta pruning USA la classe table, però è esterna
il main alloca la table con la configurazione di gioco e lancia alfabeta pruning con quella configurazione e da lì otteniamo il vincitore
vogliamo sapere anche quanti punti vengono fatti dai 2 giocatori

? meglio fare le funzioni con parametro player invece che prenderlo dall'oggetto table?
? devo essere in grado di fare una partita dal Main per poter essere in grado di usare alphaBeta? ad esempio t.playTile, t.showHand, etc..
? per fare la cronologia mosse, conviene fare un oggetto "Move"?
*/


import java.util.ArrayList;
import java.util.Collections;
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

        System.out.println("\n-------------------- START GAME ---------------------");

        printTableConfig();
        
        firstMove();
        passTurn();

        while (game_state == GameState.OPEN) {
            printTableConfig();

            move = randomMove();
            playTile(move);
            passTurn();
        }

        printTableConfig();
    }


    //* Select AND play the starting tile
    //Note: starting player is decided by chooseStartingPlayer during setup
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


    //* Return a random index of a playable tile in the current player's hand 
    public int[] randomMove(){
        ArrayList<int[]> moves = availableMoves(current_player);

        if (!moves.isEmpty()) {
            Random rand = new Random();
            int random_index = rand.nextInt(moves.size());
            return moves.get(random_index);
        } 
        else {
            int[] move = new int[2];
            move[0] = -1;
            move[1] = -1; 
            return move; 
        }
    }
    

    //* Move the given tile from the current player's hand to the table
    // move[0] = index of the tile (in player hand), move[1] = 0->head of table, 1-> tail of table
    public void playTile(int[] move) {
        Tile played_tile = p_hands.get(current_player).remove(move[0]);

        if (move[1] == 0) { // head
            played_tiles.add(0, played_tile);
            if (played_tile.val_1 == head) {
                head = played_tile.val_2;
            } else {
                head = played_tile.val_1;
            }
        } else { // tail
            played_tiles.add(played_tile);
            if (played_tile.val_1 == tail) {
                tail = played_tile.val_2;
            } else {
                tail = played_tile.val_1;
            }
        }
        System.out.println("playTile(): " + played_tile.val_1 + "|" + played_tile.val_2);
    }
    
    
    //* Change the current player if the other player has available moves, if he hasn't
    //* then keep the current player, if neither game is over
    public void passTurn() {
        if (current_player == 1) {
            if (availableMoves(0).size() > 0) {
                current_player = 0;
            } else if (availableMoves(1).size() == 0) {
                endGame();
            }
        } else {
            if (availableMoves(1).size() > 0) {
                current_player = 1;
            } else if (availableMoves(0).size() == 0) {
                endGame();
            }
        }
    }
    

    //* Return list of available moves for the given player
    // move[0] = index of the tile (in player hand), move[1] = 0->head of table, 1-> tail of table
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


    //* Manage what needed when the game is over
    public void endGame() {
        game_state = GameState.ENDED;
        System.out.println("Game Over");
        //todo conta punti
    }
    

    //* Check if a move is legal
    // move[0] = index of the tile (in player hand), move[1] = 0->head of table, 1-> tail of table
    public boolean isLegalMove(int player, int[] move) {
        if (move[1] == 0) {
            if(p_hands.get(player).get(move[0]).val_1 == head || p_hands.get(player).get(move[0]).val_2 == head){
                return true;
            }
            return false;
        }
        else {
            if (p_hands.get(player).get(move[0]).val_1 == tail || p_hands.get(player).get(move[0]).val_2 == tail) {
                return true;
            }
            return false;
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
    

    //* Generate the tile set that is going to be used for the whole game
    private void generateAllTiles() {
        for (int i = 0; i <= max_tile; i++) {
            for (int j = i; j <= max_tile; j++) {
                all_tiles.add(new Tile(i, j));
            }
        }
        Collections.shuffle(all_tiles);
    }


    //* Function says it all
    private void dealPlayersHands() {
        for(int i = 0; i < hand_size; i++){
            p_hands.get(0).add(all_tiles.remove(0));
            p_hands.get(1).add(all_tiles.remove(0));

        }
        reserve_tiles = all_tiles;

        for (int i = 0; i < hand_size; i++) {
            p_hands.get(0).get(i).owner = 0;
            p_hands.get(1).get(i).owner = 1;
        }
    }
    
    
    // * Function says it all
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
        else { resetTable(); }   // if no one has a double
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
        //System.out.print("UP:  ");
        //printTiles(reserve_tiles);
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

