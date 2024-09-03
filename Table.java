import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Stack;


public class Table {

    int max_tile, hand_size, head, tail, current_player, MAX_DEPTH = 10000;     // current_player -> 0:P1, 1:P2
    ArrayList<Tile> all_tiles, reserve_tiles;
    Deque<Tile> played_tiles;  
    ArrayList<ArrayList<Tile>> p_hands;
    GameState game_state;
    Stack<Character> history;
    boolean DEBUG = false;



    /*************************************/
    /*            CONSTRUCTORS           */
    /*************************************/ 

    public Table(int max_tile, int hand_size) { 
        game_state = GameState.OPEN;
        this.max_tile  = max_tile;
        this.hand_size = hand_size;
        head = -1;
        tail = -1;

        all_tiles     = new ArrayList<>();
        reserve_tiles = new ArrayList<>();   // used only during setup
        played_tiles  = new ArrayDeque<>();
        p_hands       = new ArrayList<>();
        p_hands.add(new ArrayList<>());
        p_hands.add(new ArrayList<>());
        history = new Stack<>();

        generateAllTiles();  
        dealPlayersHands();
        chooseStartingPlayer();
        playGame();
    }


    public Table(ArrayList<Tile> p1_hand, ArrayList<Tile> p2_hand, int head, int tail, int starting_player){
        if (p1_hand != null && p2_hand != null && p1_hand.size() > 0) { 

            game_state = GameState.OPEN;
            this.hand_size = p1_hand.size();
            this.head = head;
            this.tail = tail;
    
            played_tiles  = new ArrayDeque<>();
            p_hands       = new ArrayList<>();
            history       = new Stack<>();

            p_hands.add(p1_hand); 
            p_hands.add(p2_hand); 
    
            if(starting_player == -1){ chooseStartingPlayer(); }
            else{ current_player = starting_player; }
            playGame();
        } 
        else { System.err.println("Error [Table()]: invalid hands."); }
    }


    // for Manual.java
    public Table(ArrayList<Tile> p1_hand, ArrayList<Tile> p2_hand){
        if (p1_hand != null && p2_hand != null && p1_hand.size() > 0) { 

            game_state = GameState.OPEN;
            this.hand_size = p1_hand.size();
            head = -1;
            tail = -1;
            
            played_tiles  = new ArrayDeque<>();
            p_hands       = new ArrayList<>();
            history       = new Stack<>();
            
            p_hands.add(p1_hand); 
            p_hands.add(p2_hand); 
            
            chooseStartingPlayer(); 
            playGame();
        } 
        else { System.err.println("Error [Table()]: invalid hands."); }
    }




    /*************************************/
    /*               ACTIONS             */
    /*************************************/    



    // INFO: manage game from start to end
    public void playGame(){
        Tile tile_to_play;

        printForTerminal();

        if(DEBUG) { System.out.println("\n-------------------- START GAME --------------------");}
        if(DEBUG) { printTableConfig();}
        firstMove();

        //while (game_state == GameState.OPEN) {
            if(DEBUG) { printTableConfig(); }
            tile_to_play = bestMove();
            if(DEBUG) { System.out.print("\ntile_to_play: "); }
            if(DEBUG) { if(tile_to_play != null) { tile_to_play.printTile(); } }
            playTile(tile_to_play);
            if(checkEndGame()){ endGame(null); }
        //}
        
        if(DEBUG) { System.out.println("\n--------------------- END GAME ---------------------\n");}
        if(DEBUG) { printTableConfig(); }
    }


 
    // INFO: return the tile that represents the best move available for the current player
    public Tile bestMove(){
        int  best_value = Integer.MIN_VALUE, move_value = Integer.MIN_VALUE;   
        Tile best_move = null, last_move = null;
        boolean swap_flag = false;  

        // list available moves
        if(DEBUG){ System.out.print("bestMove(P"+ (current_player+1) +") - available moves: ");}
        ArrayList<Integer> moves = availableMoves(current_player);
        if(DEBUG) { printTilesByIndex(moves, current_player); }

        // for each available move
        for (int move : moves) {
            Tile tile_to_play = p_hands.get(current_player).get(move);

            // a tile needs to be swapped in 2 cases:
            // 1) the value that has to be played is on val_2, in order to be used by playTile() it has to be in val_1
            // 2) the tile can be played in 2 different ways, so once it is played normally and a second time it is played swapped
            // the tile to play is the same one as last_move, that means it's time to swap it to try the second move with that tile
            if(tileNeedsSwap(last_move, tile_to_play)){ 
                tile_to_play.swapTile(); 
            }

            // play the tile and obtain a value for the move by investigating the possibilities of the opponent
            if(DEBUG){ System.out.println(); }
            playTile(tile_to_play);
            move_value = minimax(0, false);  

            // undo the move and proceed to see if it would be the best move
            undo();  
            if(DEBUG){ System.out.print("move_value: " + move_value + " con ");}
            if(DEBUG){ tile_to_play.printTile(); }
            if(DEBUG){ System.out.println(); }

            // new best move is found, update the best move
            if (move_value > best_value) {  

                best_value = move_value;
                best_move = tile_to_play;

                if(DEBUG){ System.out.print("best_move: ");}
                if(DEBUG){ tile_to_play.printTile();}
                if(DEBUG){System.out.print(".....");} 
                if(DEBUG){ best_move.printTile(); }
                if(DEBUG){ System.out.println(" with best_value: " + best_value);}
            }
            else {  
                if(DEBUG){ System.out.print("sub_move: ");}
                if(DEBUG){ tile_to_play.printTile(); }
                if(DEBUG){ System.out.println(" with value: " + move_value);}
            }
            last_move = tile_to_play;
        }

        System.out.print("\t" + best_value);

        return best_move;
    }



    // INFO: 
    private int minimax(int depth, boolean is_maximizing) {

        // minimax stops either if MAX_DEPTH has been reached or if a endgame condition
        // is triggered: a player has no more tiles or both players can0t make a move
        if (checkEndGame() || depth == MAX_DEPTH) {
            if(DEBUG){ printSpacesLn(depth, "Ascending - value: " + evaluateGameScoring() );}
            return evaluateGameScoring();  
        }

        // list of available moves
        ArrayList<Integer> moves = availableMoves(current_player);

        if(DEBUG) { printSpaces(depth, null); }
        if(DEBUG){System.out.print("depth:" + depth + " - P" + (current_player+1) +  " - h|t " + head + "|" + tail + " ha mosse: ");}
        if(DEBUG) { printTilesByIndex(moves, current_player); }

        // managing cases where there's a "pass": play a null Tile and call minimax with is_maximizing inverted 
        if(moves.isEmpty()) {
            if(DEBUG){ printSpaces(depth, null); }

            playTile(null);
            int eval = minimax(depth + 1, !is_maximizing);

            if(DEBUG){printSpaces(depth, null);}
            undo();
            return eval;
        }
        else {
            // is_maximizing indicates which player is playing: assuming P1 is the player that is 
            // trying to win (maximizing score), when is_maximizing = true this is the branch that is executed
            // instead if it's P2's turn, the else branch will be executed
            if (is_maximizing) {
                int     max_eval    = Integer.MIN_VALUE; 
                Tile    last_move   = null;
                boolean unswap_flag = false; 

                for (int move : moves) {
                    Tile tile_to_play = p_hands.get(current_player).get(move);

                    // a tile needs to be swapped in 2 cases:
                    // 1) the value that has to be played is on val_2, in order to be used by playTile() it has to be in val_1
                    // 2) the tile can be played in 2 different ways, so once it is played normally and a second time it is played swapped
                    // the tile to play is the same one as last_move, that means it's time to swap it to try the second move with that tile
                    // also set the flag for unswapping later 
                    if(tileNeedsSwap(last_move, tile_to_play)){ 
                        if(DEBUG){
                            System.out.print("SWAP1" );
                        }
                        tile_to_play.swapTile();  
                        unswap_flag = true;
                    }

                    // try the move and get an evaluation by investigating deeper layers
                    if(DEBUG){ printSpaces(depth, null);}

                    playTile(tile_to_play);
                    int eval = minimax(depth + 1, false);

                    if(DEBUG){ printSpaces(depth, null);}

                    undo();  

                    // return the tile to its original values to avoid inconsistencies
                    if(unswap_flag){
                        unswap_flag = false;
                        tile_to_play.swapTile();
                    }
        
                    // update the best value if found
                    max_eval = Math.max(max_eval, eval);
                    last_move = tile_to_play; //""
                }
                return max_eval;
            } 
            else {
                int min_eval = Integer.MAX_VALUE;
                Tile last_move = null;
                boolean unswap_flag = false; 

                for (int move : moves) {
                    Tile tile_to_play = p_hands.get(current_player).get(move);
        
                    if(tileNeedsSwap(last_move, tile_to_play)){ 
                        if(DEBUG){
                            System.out.print("SWAP2");
                        }
                        tile_to_play.swapTile();  
                        unswap_flag = true;
                    }

                    if(DEBUG){ printSpaces(depth, null); }

                    playTile(tile_to_play);  
                    int eval = minimax(depth + 1, true);

                    if(DEBUG){ printSpaces(depth, null); }
                    undo();  
                    if(unswap_flag){
                        unswap_flag = false;
                        tile_to_play.swapTile();
                    }
        
                    min_eval = Math.min(min_eval, eval);
                    last_move = tile_to_play; //""
                }
                return min_eval;
            }
        }
    }



    // INFO: play the tile, or pass turn if t is null
    // NOTE: when passing the parameter t, t.val1 MUST already be the number you want to 'attach'
    public void playTile(Tile t){   
        if(t == null){
            if(DEBUG){ System.out.println("playTile(): null"); }
            history.push('p');
            current_player = (current_player == 0) ? 1 : 0;    // pass the turn
        }
        else{
            // (t.val_1 == head || t.val_1 == tail) is to make sure the tile is passed correctly since .val_1 has to be
            // the value that is going to be played, so it's not possibile that it doesn't match head or tail
            if(pOwnsTile(t, current_player) && (t.val_1 == head || t.val_1 == tail) ){
                if(DEBUG){ System.out.print("playTile(): "); }
                if(DEBUG) {t.printTile();}
                if(DEBUG){ System.out.println(); }
                rmvTileHand(t);

                if(t.val_1 == head){
                    played_tiles.addFirst(t);
                    head = t.val_2;
                    history.push('h');
                    current_player = (current_player == 0) ? 1 : 0;    // pass the turn
                }
                else{
                    played_tiles.addLast(t);
                    tail = t.val_2;
                    history.push('t');
                    current_player = (current_player == 0) ? 1 : 0;    // pass the turn
                }
            }
            else{
                System.out.print("Error [playTile()]: tile not owned or unplayable. Tile: ");
                t.printTile();
                System.out.println(", owner: " + t.owner + " current_player: " + current_player + " head|tail: "+head+"|"+tail);
                System.out.print("P1 hand: ");
                printPlayerHand(0);
                System.out.print("P2 hand: ");
                printPlayerHand(1);

                System.exit(1); // in case of error interrupt the execution
            }
        }
    }
    


    // INFO: 
    // NOTE: undo MUST be used after turn has been passed 
    public void undo(){
        char last_move = history.peek();

        if(DEBUG){ System.out.print("Undoing: " + last_move + ",");}
        if(last_move == 'p'){                                // last move was a pass
            current_player = (current_player == 0) ? 1 : 0;
            history.pop();

            if(DEBUG){ System.out.print(" from P" + (current_player+1));}
            if(DEBUG){ System.out.println();}
        }
        else if(last_move == 'h'){                          // last move was in head 
            current_player = (current_player == 0) ? 1 : 0;
            Tile removed_tile = played_tiles.pollFirst();   // .pollFirst() removes and return first element of queue

            p_hands.get(current_player).add(removed_tile);

            head = (removed_tile.val_1 == head) ? removed_tile.val_2 : removed_tile.val_1; 
            history.pop();
            if(DEBUG){ System.out.print(" from P" + (current_player+1) +", tile: ");}
            if(DEBUG){ removed_tile.printTile(); }
            if(DEBUG){ System.out.println();}
        }
        else if(last_move == 't'){                          // last move was in tail 
            current_player = (current_player == 0) ? 1 : 0;  
            Tile removed_tile = played_tiles.pollLast();

            p_hands.get(current_player).add(removed_tile);

            tail = (removed_tile.val_1 == tail) ? removed_tile.val_2 : removed_tile.val_1;
            history.pop();
            if(DEBUG){ System.out.print(" from P" + (current_player+1) +", tile: ");}
            if(DEBUG){removed_tile.printTile();}
            if(DEBUG){ System.out.println();}
        }
        else {                                            // letter 'f' (first move)
            current_player = (current_player == 0) ? 1 : 0;
            Tile removed_tile = played_tiles.pollLast();

            p_hands.get(current_player).add(removed_tile);

            tail = -1;
            head = -1;
            history.pop();

            if(DEBUG){ System.out.print(" from P" + (current_player+1) +", tile: ");}
            if(DEBUG){removed_tile.printTile();}
            if(DEBUG){ System.out.println();}
        }
        checkAndSortPlayerHand(current_player);
    } 
































    // INFO: select and play the starting tile
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

        // move tile from hand to table
        Tile tile_to_play = p_hands.get(current_player).get(index_tile);
        rmvTileHand(tile_to_play);
        played_tiles.addFirst(tile_to_play);

        history.push('f');            // f -> first move
        head = tile_to_play.val_1;
        tail = tile_to_play.val_2;

        // pass turn and check for end game
        current_player = (current_player == 0) ? 1 : 0;    // invert current_player

        if(DEBUG){ System.out.print("firstMove(): ");}
        if(DEBUG){ tile_to_play.printTile();}
        if(DEBUG){ System.out.println();}
    }



    // INFO: return list of available moves for given player
    // NOTE: a tile with double match will be listed twice, (e.g., if head=2, tail=4 the tile
    //       2|4 will count as two possible moves)
    //       when head=tail, a tile could be placed in both positions, but that'd be useless,
    //       one is enough becaus of the simmetry
    public ArrayList<Integer> availableMoves(int player){    // move index of the tile (in player hand) 
        ArrayList<Integer> moves = new ArrayList<>();  

        for (int i = 0; i < p_hands.get(player).size(); i++) {
            Tile tmp_tile = p_hands.get(player).get(i);

            // the first if manages two situations where duplicates are not wanted: if head=tail it does not matter where the tile is played since it's simmetrical
            // so one move is enough, and if the tile has val_1=val_2 it does not make sense in which way is played because it's the same, so one move is enough
            if(head == tail  ||  tmp_tile.val_1 == tmp_tile.val_2){
                if( (tmp_tile.val_1 == head || tmp_tile.val_2 == head)  ||  (tmp_tile.val_1 == tail || tmp_tile.val_2 == tail) ){
                    moves.add(i);
                }
            }
            // in every other situation (where duplicates are not involved) there could be max two possible moves for a tile
            else {
                if(tmp_tile.val_1 == head || tmp_tile.val_1 == tail){ moves.add(i); }
                if(tmp_tile.val_2 == head || tmp_tile.val_2 == tail){ moves.add(i); }
            }
        }
        return moves;
    }



    // INFO: a swap is needed since playTile() wants the value to play in the position .val_1, cases for a swap are:
    // - there are two possible moves with the tile, e.g. tile 3|4 and head=3, tail=4, so swap needed if tile has to go to tail
    // - the tile has the value to be played in the field .val_2, so it needs to swap so that in .val_1 there's the value to be played
    // NOTE: last_tile is used because the moves list from availableMoves() returns two adjacent occurrencies of the same tile if that tile
    //       can be played in two positions, so with last_tile it can be detected when such case occurs
    public boolean tileNeedsSwap(Tile last_tile, Tile tile_to_play){
        if(last_tile != null){
            // case where the tile considered is the same one as before, meaning that this tile gives two possibile moves
            if(last_tile.val_1 == tile_to_play.val_1 && last_tile.val_2 == tile_to_play.val_2){
                return true;
            }
        }
        if(tile_to_play.val_1 != head && tile_to_play.val_1 != tail) {  // case where .val_1 can't be played so the possible moves is on .val_2
            return true;
        }
        return false;
    }



    // INFO: 
    public boolean checkEndGame(){
        if(p_hands.get(0).size() == 0 || p_hands.get(1).size() == 0){ return true; }              // a player has no more tiles
        else if(availableMoves(0).size() == 0 && availableMoves(1).size() == 0){ return true; }   // no player has available moves
        return false;
    }



    // INFO: manage a game over
    public void endGame(String msg) {
        game_state = GameState.ENDED;
        if(DEBUG){ System.out.print("\n\nGame Over: ");}
        
        // pv count
        int pv1 = sumPlayerPoints(0);
        int pv2 = sumPlayerPoints(1);

        if(DEBUG){ System.out.print(" PV1: " + pv1 + " PV2: " + pv2 + " ");}
    }



    // INFO: calculate the actual points a player has in hand
    public int sumPlayerPoints(int player) {
        int score = 0;

        for (Tile tile : p_hands.get(player)) {
            score += tile.val_1 + tile.val_2;
        }
        return score;
    }



    // INFO: 
    public int evaluateGameScoring() {
        int score_p1 = 0,  score_p2 = 0;

        for (Tile tile : p_hands.get(0)) {
            score_p1 += tile.val_1 + tile.val_2;
        }
        for (Tile tile : p_hands.get(1)) {
            score_p2 += tile.val_1 + tile.val_2;
        }

        
        if(p_hands.get(0).size() != 0 && p_hands.get(1).size() != 0){   // at least a player has still tiles in hand
            System.out.print(score_p2 - score_p1 + "X ");
        }   
        else {
            System.out.print(score_p2 - score_p1 + " ");
        }

        return (score_p2 - score_p1);
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


    // INFO: reorder tiles in player's hand using the "index" tiles field, needed because
    //       minimax won't put them back in order with the undos so there can be errors since 
    //       indexes are used to know which tile to play  
    public void checkAndSortPlayerHand(int player) {
        if (player < 0 || player >= p_hands.size()) {  // kinda useless check
            System.out.println("Invalid player index.");
            return;
        }

        ArrayList<Tile> hand = p_hands.get(player);   // hand is referring to p_hands, meaning that modifications will apply to p_hands

        boolean sorted = true;
        for (int i = 0; i < hand.size() - 1; i++) {
            if (hand.get(i).index > hand.get(i + 1).index) {
                sorted = false;
                break;
            }
        }

        if (!sorted) {
            hand.sort(Comparator.comparingInt(tile -> tile.index));
        } 
    }





    /*************************************/
    /*               SETUP               */
    /*************************************/

    // INFO:
    public void resetTable() {
        if(DEBUG) { System.out.println("resetting table"); }
        generateAllTiles();
        dealPlayersHands();
        chooseStartingPlayer();
    }
    

    // INFO: generate the tile set
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
        for (int i = 0; i < hand_size; i++) {                 
            Tile tileForPlayer1 = all_tiles.remove(0);
            Tile tileForPlayer2 = all_tiles.remove(0);
    
            // Set owner and index for player 1's tile
            tileForPlayer1.owner = 0;
            tileForPlayer1.index = p_hands.get(0).size();
            p_hands.get(0).add(tileForPlayer1);
    
            // Set owner and index for player 2's tile
            tileForPlayer2.owner = 1;
            tileForPlayer2.index = p_hands.get(1).size();
            p_hands.get(1).add(tileForPlayer2);
        }
        reserve_tiles = all_tiles;
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
    /*               PRINT               */
    /*************************************/

    public void printTableConfig() {
        System.out.println();
        System.out.println("----------------------------------------------------");
        System.out.println("Current Player: P" + (current_player+1) );
        System.out.print("P1:  ");
        printTiles(p_hands.get(0));
        System.out.print("P2:  ");
        printTiles(p_hands.get(1));
        System.out.print("TB:  ");
        printPlayedTiles(played_tiles);
        System.out.println("head: " + head + " tail: " + tail);
        System.out.println("----------------------------------------------------");
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



    public void printTilesByIndex(ArrayList<Integer> moves, int player){
        for (int move : moves) {  
            Tile tmp = p_hands.get(player).get(move);
            tmp.printTile();
            System.out.print(", ");
        }
        if(moves.size() == 0){ System.out.print("x"); }
        System.out.println();
    }



    public void printPlayedTiles(Deque<Tile> tiles) {
        for (Tile element : tiles) {
            Tile.printTile(element);
            System.out.print(" - ");
        }
    }



    public void printPlayerHand(int player) {
        if (player < 0 || player >= p_hands.size()) {
            System.out.println("Invalid player index.");
            return;
        }
    
        System.out.print("P" + (player + 1) + " hand:");
        for (Tile tile : p_hands.get(player)) {
            tile.printTile();
        }
        System.out.println();
    }



    public void printSpaces(int depth, String msg) {
        int num_spaces = (depth + 1) * 5; // Ad esempio, 3 spazi per livello di profondità
        String spaces = " ".repeat(num_spaces);
        if(msg != null){
            System.out.print(spaces + msg);
        }
        else {
            System.out.print(spaces);
        }
    }
    public void printSpacesLn(int depth, String msg) {
        int num_spaces = (depth + 1) * 5; // Ad esempio, 3 spazi per livello di profondità
        String spaces = " ".repeat(num_spaces);
        if(msg != null){
            System.out.println(spaces + msg);
        }
        else {
            System.out.println(spaces);
        }
    }



    public void printForTerminal(){
        
        for (Tile tile : p_hands.get(0)) {
            tile.printTileSimple();
            System.out.print(" ");
        }
        System.out.print("\t");
        
        for (Tile tile : p_hands.get(1)) {
            tile.printTileSimple();
            System.out.print(" ");
        }
        System.out.print("\t");
    }


    /*************************************/
    /*               BACKUP               */
    /*************************************/

    // INFO: manage a game with random moves from start to end
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
            //System.out.println("undo random");
        }

        System.out.println();
        System.out.println();

        if(DEBUG) { printTableConfig(); }
    }



    // INFO: choose random tile to play, if no moves available return null
    public Tile randomMove(){
        ArrayList<Integer> moves = availableMoves(current_player);

        if (!moves.isEmpty()) {
            Random rand = new Random();
            int random_index = rand.nextInt(moves.size());
            Tile tile_to_play = p_hands.get(current_player).get(moves.get(random_index));

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

}  // parentesi chiusura classe  


