
//TODO aggiungere variabile "sommatoria punti in mano" ?
//? la funzione initPlayer è meglio qui oppure in Game?
//? ci va il package?

// Mettere in pausa questa classe e fare tutto su table

package domino;

public class Player {
    boolean turn;
    List<Tile> hand;

    public Player(boolean turn, List<Tile> hand) {
        this.turn = turn;
        this.hand = hand;
    }

    public void initPlayer(){

    }

//? devo passargli la Board o posso usarla come se fosse una variabile "globale"?
    public Tile playTile(){

    }
}