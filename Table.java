
/*

noi lanciamo minimax su questo gioco, dove la radice sarà il 6,6, avremo un risultato e si ritorna il vincitore
l'alfabeta pruning USA la classe table, però è esterna
il main alloca la table con la configurazione di gioco e lancia alfabeta pruning con quella configurazione e da lì otteniamo il vincitore
vogliamo sapere anche quanti punti vengono fatti dai 2 giocatori

-------------------------------------------------------------------------------------------

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

-------------------------------------------------------------------------------------------

! APPUNTI PER PROFESSORE:

    - randomMove(): è un pò brutto da leggere il codice, ma è stato necessario gestire alcune casistiche di swap, perché:
      playTile() ha bisogno di sapere in qualche modo quale è la faccia da giocare, perché sennò ci sono ambiguità 
      (es: head= 4, tail=5, P1 gioca 4|5 e la passa a playTile(), come si fa a sapere che lato giocare?), per risolverle
      ho fatto in modo che in tile.val_1 ci DEVE essere il numero che io voglio attaccare a una delle due estremità

    - availableMoves(): non è più usata per sapere l'indice del tile da giocare, ora è utile solo per sapere se il numero di mosse
      che può fare un giocatore è 0 o no (ad es. utile su checkEndGame()); si potrebbe ottimizzare fermandosi appena si vede che 
      c'é almeno una mossa disponibile;

    - undo(): usa una pila history, che contiene dei caratteri che indicano l'evento di quel turno (f -> first turn, p -> pass, 
      h -> tile in head, t -> tile in tail); inoltre deve venire usato DOPO che il turno è stato passato, questo perché la prima cosa 
      che fa è invertire current_player, in modo da ridargli in mano la tile giocata nel turno precedente


? DOMANDE PER PROFESSORE:

    - undo(): va bene che l'undo possa venir fatto solo dopo aver passato il turno? perché idealmente sarebbe interessante anche poter posizionare
      la tessere per "provare" e poi eventualmente cambiarla (quindi fare una sorta di "undo parziale"), ma questo è anche ciò che farà minmax quindi
      forse va bene com'é ora


TODO

    - punti vittoria vengono persi ogni volta che riavvio tavolo

*/


import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Stack;

public class Table {

    ArrayList<Tile> all_tiles;     
    ArrayList<Tile> reserve_tiles;
    int max_tile, hand_size;

    Deque<Tile> played_tiles;  
    ArrayList<ArrayList<Tile>> p_hands;
    int head, tail, current_player;  // current_player -> 0:P1, 1:P2
    GameState game_state;
    Stack<Character> history;


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
            history = new Stack<>();

            generateAllTiles();  
            dealPlayersHands();
            chooseStartingPlayer();
            playRandomGame();
        }
        else { System.err.println("Error [Table()]: invalid constructor parameters."); }
    }


    public Table(ArrayList<Tile> p1_hand, ArrayList<Tile> p2_hand){
        if (p1_hand != null && p2_hand != null && p1_hand.size() == p2_hand.size() && p1_hand.size() > 0) {

            game_state = GameState.OPEN;
            this.hand_size = p1_hand.size();
            head = -1;
            tail = -1;
    
            played_tiles  = new ArrayDeque<>();
            p_hands   = new ArrayList<>();
            history = new Stack<>();

            p_hands.add(p1_hand); 
            p_hands.add(p2_hand); 
    
            chooseStartingPlayer();
            playRandomGame();
        } else {
            System.err.println("Error [Table()]: invalid hands.");
        }
    }



    /*************************************/
    /*               ACTIONS             */
    /*************************************/    


    // INFO: manage game from start to end
    public void playRandomGame(){
        Tile tile_to_play;

        System.out.println("\n-------------------- START GAME --------------------");

        printTableConfig();
        firstMove();

        while (game_state == GameState.OPEN) {
            printTableConfig();
            tile_to_play = randomMove();
            playTile(tile_to_play);
        }
        
        System.out.println("\n--------------------- END GAME ---------------------\n");

        System.out.println();

        System.out.println("HISTORY: ");
        for (char move : history) {
            System.out.print(move + " -> ");
        }

        System.out.println();
        System.out.println();
        
        System.out.println("UNDO:");
        while (!history.isEmpty()) {
            undo();
        }

        System.out.println();
        System.out.println();

        printTableConfig();
    }


    // INFO: select and play the starting tile (starting player decided by chooseStartingPlayer)
    public void firstMove(){ 
        int max_tile = -1, index_tile = -1;

        // find the highest double tile
        for (int i = 0; i < p_hands.get(current_player).size(); i++) {
            Tile tmp_tile = p_hands.get(current_player).get(i);
            if ((tmp_tile.val_1 == tmp_tile.val_2) && (tmp_tile.val_1 > max_tile)) {
                max_tile = tmp_tile.val_1;
                index_tile = i;
            }
        }

        // move double tile from hand to table
        Tile tile_to_play = p_hands.get(current_player).remove(index_tile);
        played_tiles.addFirst(tile_to_play);

        // update: history/head/tail
        history.push('f');            // f -> first move
        head = tile_to_play.val_1;
        tail = tile_to_play.val_2;

        // pass turn and check for end game
        current_player = (current_player == 0) ? 1 : 0;    // invert current_player
        checkEndGame();

        System.out.print("firstMove(): ");
        tile_to_play.printTile();
        System.out.println();
    }


    // INFO: choose random tile to play, if no moves available return null
    public Tile randomMove(){
        ArrayList<int[]> moves = availableMoves(current_player);

        if (!moves.isEmpty()) {
            Random rand = new Random();
            int random_index = rand.nextInt(moves.size());
            Tile tile_to_play = p_hands.get(current_player).get(moves.get(random_index)[0]);

            if((tile_to_play.val_2 == head) && (tail == head)){
                System.out.print("swap: " + tile_to_play.val_1);
                tile_to_play.swapTile();
                System.out.println(" -> " + tile_to_play.val_1);
            }
            else if((head != tail) && ( (tile_to_play.val_1 != head) &&  (tile_to_play.val_1 != tail) ) ) {
                System.out.print("swap: " + tile_to_play.val_1);
                tile_to_play.swapTile();
                System.out.println(" -> " + tile_to_play.val_1);
            }
            return tile_to_play;
        } 
        else {
            return null;
        }
    }


    // INFO: play the tile, ora pass turn if t is null
    // NOTE: t.val1 MUST be the number you want to 'attach'
    public void playTile(Tile t){   
        if(t == null){
            System.out.println("playTile(): null");
            history.push('p');
            checkEndGame();
            current_player = (current_player == 0) ? 1 : 0;    // pass the turn
        }
        else{
            if(pOwnsTile(t, current_player) && isPlayableTile(t)){
                System.out.print("playTile(): ");
                t.printTile();
                System.out.println();

                rmvTileHand(t);

                if(t.val_1 == head){
                    played_tiles.addFirst(t);
                    head = t.val_2;
                    history.push('h');
                    checkEndGame();
                    current_player = (current_player == 0) ? 1 : 0;    // pass the turn
                }
                else{
                    played_tiles.addLast(t);
                    tail = t.val_2;
                    history.push('t');
                    checkEndGame();
                    current_player = (current_player == 0) ? 1 : 0;    // pass the turn
                }
            }
            else{
                System.out.println("Error [playTile()]: tile not owned or unplayable.");
            }
        }

        for (char move : history) {
            System.out.print(move + " -> ");
        }
        System.out.println();
    }
    
    
    // INFO: 
    public void checkEndGame(){
        if(p_hands.get(current_player).size() == 0){  
            endGame("Empty hand.");   
        }
        else if(availableMoves(0).size() == 0 && availableMoves(1).size() == 0){  // no player has available moves
            endGame("No available moves.");
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
        // System.out.print("P"+ (player + 1) +" has " + moves.size() + " moves ");
        return moves;
    }


    // INFO: manage a game over
    public void endGame(String msg) {
        game_state = GameState.ENDED;
        System.out.println("Game Over: " + msg);
        
        // pv count
        int pv1 = 0, pv2 = 0;
        for(int i = 0; i < p_hands.get(0).size(); i++){
            Tile tmp_tile = p_hands.get(0).get(i);
            pv1 += (tmp_tile.val_1 + tmp_tile.val_2);
        }
        for(int i = 0; i < p_hands.get(1).size(); i++){
            Tile tmp_tile = p_hands.get(1).get(i);
            pv2 += (tmp_tile.val_1 + tmp_tile.val_2);
        }

        System.out.println("PV1: " + pv1 + " PV2: " + pv2);
    }


    // INFO: 
    // NOTE: undo MUST be used after turn has been passed 
    public void undo(){
        char last_move = history.peek();

        System.out.print("Undoing: " + last_move + ",");
        if(last_move == 'p'){
            current_player = (current_player == 0) ? 1 : 0;
            history.pop();

            System.out.print(" from P" + (current_player+1));
            System.out.println();
        }
        else if(last_move == 'h'){
            current_player = (current_player == 0) ? 1 : 0;
            Tile removed_tile = played_tiles.pollFirst();
            p_hands.get(current_player).add(removed_tile);
            head = (removed_tile.val_1 == head) ? removed_tile.val_2 : removed_tile.val_1;
            history.pop();

            System.out.print(" from P" + (current_player+1) +", tile: ");
            removed_tile.printTile();
            System.out.println();
        }
        else if(last_move == 't'){
            current_player = (current_player == 0) ? 1 : 0;  
            Tile removed_tile = played_tiles.pollLast();
            p_hands.get(current_player).add(removed_tile);
            tail = (removed_tile.val_1 == tail) ? removed_tile.val_2 : removed_tile.val_1;
            history.pop();

            System.out.print(" from P" + (current_player+1) +", tile: ");
            removed_tile.printTile();
            System.out.println();
        }
        else {  // letter 'f' (first move)
            current_player = (current_player == 0) ? 1 : 0;
            Tile removed_tile = played_tiles.pollLast();
            p_hands.get(current_player).add(removed_tile);
            tail = -1;
            head = -1;
            history.pop();

            System.out.print(" from P" + (current_player+1) +", tile: ");
            removed_tile.printTile();
            System.out.println();
        }
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


    // INFO: 
    public boolean isPlayableTile(Tile t){
        if(t.val_1 == head || t.val_2 == head || t.val_1 == tail || t.val_2 == tail){
            return true;
        }
        return false;
    }


    // INFO: remova a tile from current player hand
    public Tile rmvTileHand(Tile t){
        for (int i = 0; i < p_hands.get(current_player).size(); i++){
            Tile tmp_tile = p_hands.get(current_player).get(i);

            if((t.val_1 == tmp_tile.val_1 && t.val_2 == tmp_tile.val_2) || (t.val_1 == tmp_tile.val_2 && t.val_2 == tmp_tile.val_1)){
                p_hands.get(current_player).remove(i);
                return tmp_tile;
            }
        }
        return null;
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
    

    // INFO: generate the whole tile set
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
    
    
    // INFO: player with highest double is starting
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


    public void printPlayedTiles(Deque<Tile> tiles) {
        for (Tile element : tiles) {
            //System.out.println(element);
            Tile.printTile(element);
            System.out.print(" - ");
        }
    }


}  // that's all folks //

