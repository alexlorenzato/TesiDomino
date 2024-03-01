
//TODO metodi add NON devono controllare compatibilità, responsabilità delegata alle funzioni che si occupano di fare le mosse


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

table deve permettere di giocare anche con meno di 14 tessere
gestire anche la possibilità che non ci sia doppio6 e anche che nessuno abbia coppia quindi resettare la partita
*/
package domino;

public class Table{
    List<Tile> tiles;
    int head, tail;
    int current_player;

    public Table(){
        this.tiles = new ArrayList<>();
        this.head = -1;                     // valori fake per tavolo inizialmente vuoto
        this.tail = -1;
    }

//? da sostituire alle funzioni addHead e addTail?
    public playTile(){

    }

    public void addHead(Tile tile, int head){
        tiles.add(0, tile);
        this.head = head;
    }

    public void addTail(Tile tile, int tail){
        tiles.add(tile);
        this.tail = tail;
    }

    public Tile getHead(){
        return head;
    }

    public Tile getTail(){
        return tail;
    }

    public int getCurrentPlayer(){

    }

    public void reset(){

    }
}