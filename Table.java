
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

/*

5. Non so se possa essere rilevante dato il piccolo numero di tessere, ma potremmo pensare
a qualche altra idea per velocizzare la availableMoves evitando una scansione
lineare su tutte le tessere. Lo scopo è quello di di velocizzare l’alfabeta (ed eventualmente
anche la playTile() se viene modificata come spiegato in 4).
Questo potrebbe portare a cambiare (anche di molto) la rappresentazione
sottostante di p_hands, ed è possibile che sia inutile se giochiamo
con set di 7 o 14 tessere per giocatore. Teniamo quindi in considerazione questa modifica,
da provare eventualmente più avanti. Relativamente ad una modifica, una tabella hash
(HashMap) potrebbe velocizzare di molto verificare se un giocatore possiede
o meno una certa tessera, ma d’altra parte potrebbe essere più lento andare
a cercare le tessere giocabili. Bisogna pensarci.

6. Implementerei played_tiles con una Deque, e aggiungerei in testa o coda a seconda della mossa
giocata. In questo modo è semplice stampare di volta in volta tutto il “serpente”. La lista
di mosse effettuate può essere gestita con una pila dove si indica semplicemente: “testa", “coda”,
“turno passato”. 

? in che senso una pila in cui indicare semplicemente testa, cosa e turno passato?
*/


import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Deque;
import java.util.ArrayDeque;

public class Table {

    ArrayList<Tile> all_tiles;     
    ArrayList<Tile> reserve_tiles;
    Deque<Tile>     played_tiles;  
    ArrayList<ArrayList<Tile>> p_hands;
    int head, tail, max_tile, hand_size, current_player;  // current_player -> 0:P1, 1:P2
    GameState game_state;


    public Table(int max_tile, int hand_size) {
        if (max_tile > 0 && hand_size >= 0) {  

            game_state = GameState.OPEN;
            this.max_tile  = max_tile;
            this.hand_size = hand_size;
            head = -1;
            tail = -1;

            all_tiles     = new ArrayList<>();
            reserve_tiles = new ArrayList<>();
            played_tiles  = new ArrayDeque<>();
            p_hands       = new ArrayList<>();
            p_hands.add(new ArrayList<>());
            p_hands.add(new ArrayList<>());

            generateAllTiles();  
            dealPlayersHands();
            chooseStartingPlayer();
            playGame();
        }
        else { System.err.println("Error [Table()]: invalid constructor parameters."); }
    }


    public Table(ArrayList<Tile> p1_hand, ArrayList<Tile> p2_hand){
        if (p1_hand != null && p2_hand != null && p1_hand.size() == p2_hand.size() && p1_hand.size() > 0) {
            //* max_tile qui non viene considerato ma non dovrebbe essere influente

            game_state = GameState.OPEN;
            this.hand_size = p1_hand.size();
    
            head = -1;
            tail = -1;
    
            // all_tiles = new ArrayList<>();
            // reserve_tiles = new ArrayList<>();
            played_tiles  = new ArrayDeque<>();
            p_hands   = new ArrayList<>();

            p_hands.add(p1_hand); 
            p_hands.add(p2_hand); 
    
            chooseStartingPlayer();
            playGame();
        } else {
            System.err.println("Error [Table()]: invalid hands.");
        }
    }



    /*************************************/
    /*               ACTIONS             */
    /*************************************/    


    // INFO: manage game from start to end
    public void playGame(){
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


    // INFO: select and play the starting tile (starting player decided by chooseStartingPlayer)
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
        played_tiles.addFirst(played_tile);
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
    

    // INFO: move the given tile from the current player's hand to the table
    public void playTile(int[] move) {   // move[0] = index of the tile (in player hand), move[1] = 0->head of table, 1-> tail of table
        //todo verificare legalità mossa e in caso lanciare eccezione
        Tile played_tile = p_hands.get(current_player).remove(move[0]);

        if (move[1] == 0) { // head
            played_tiles.addFirst(played_tile);
            if (played_tile.val_1 == head) {
                head = played_tile.val_2;
            } else {
                head = played_tile.val_1;
            }
        } else { // tail
            played_tiles.addLast(played_tile);
            if (played_tile.val_1 == tail) {
                tail = played_tile.val_2;
            } else {
                tail = played_tile.val_1;
            }
        }
        //todo gestire qui il passaggio di turno (passando mossa vuota?)
        System.out.println("playTile(): " + played_tile.val_1 + "|" + played_tile.val_2);
    }


    // INFO: play the tile
    public void playTile(Tile t){
        //todo prendere input una tile (null se si passa), verificare che gioc possieda la tile, verificare che sia effettivamente giocabile
        //todo per capire lato da giocare: usare 1° dei due numeri 
        if(t == null){
            passTurn();
        }
        else{
            if(pOwnsTile(t) && isPlayableTile(t)){

            }
        }
    }
    
    
    // INFO: change current player if other player has moves, otherwise keep current, if neither game over
    public void passTurn() {
        //todo aggiungere la mano vuota come condizione di fine partita
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
    

    // INFO: return list of available moves for given player
    public ArrayList<int[]> availableMoves(int player){    // move[0] = index of the tile (in player hand), move[1] = 0->head of table, 1-> tail of table    
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


    // INFO: manage a game over
    public void endGame() {
        game_state = GameState.ENDED;
        System.out.println("Game Over");
        //todo conta punti
    }
    

    //INFO: heck if a move is legal
    public boolean isLegalMove(int player, int[] move) {    // move[0] = index of the tile (in player hand), move[1] = 0->head of table, 1-> tail of table
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

    // INFO:
    public void resetTable() {
        generateAllTiles();
        dealPlayersHands();
        chooseStartingPlayer();
    }
    

    // INFO: generate the tile set that is going to be used for the whole game
    private void generateAllTiles() {
        for (int i = 0; i <= max_tile; i++) {
            for (int j = i; j <= max_tile; j++) {
                all_tiles.add(new Tile(i, j));
            }
        }
        Collections.shuffle(all_tiles);
    }


    // INFO: 
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
    
    
    // INFO: 
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
    /*               UTILS               */
    /*************************************/
    

    // INFO: check if the player has in his hand the tile t
    public boolean pOwnsTile(Tile t, int player){
        for (int i = 0; i < p_hands.get(player).size(); i++){
            Tile tmp_tile = p_hands.get(player).get(i);

            if( (tmp_tile.val_1 == t.val_1 && tmp_tile.val_2 == t.val_2) || (tmp_tile.val_1 == t.val_2 && tmp_tile.val_2 == t.val_1)){
                return true;
            }
        }
        return false;
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
        printPlayedTiles(played_tiles);
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


    /*public void printPlayedTiles(){
        for (int i = 0; i < played_tiles.size(); i++) {
            Tile tmp_tile = played_tiles.get(i);
            System.out.print(tmp_tile.val_1 + "|" + tmp_tile.val_2 + " - ");
        }
    }*/


    public void printPlayedTiles(Deque<Tile> tiles) {
        for (Tile element : tiles) {
            //System.out.println(element);
            Tile.printTile(element);
            System.out.print(" - ");
        }
    }


}  // that's all folks //

