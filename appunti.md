
# To Do

- Strutture dati:
    - Tile    (int, int)
    - All Tiles (lista 28 Tiles)
    - Player  (bool is_turn, ListOfTiles hand, )
    - Table   (Tiles queue, testa e coda sempre accessibili)
    - Played Tiles     (ListOfTiles, corrisponde a Table (?) )
    - Unplayed Tiles   (ListOfTiles)
    - Player1 and Player2 Hand (ListOfTiles)

- Eventi:
    - Start  
        - Inizializzare Table, Unplayed Tiles, Played Tiles
        - Riempire Player Hands (14 tile a ciascuno, randomicamente)
        - Definire chi inizia
        - Turno del Player1
    - End of game  (nessuno può giocare Tile, Player ha finito Tile)
    - Skip turn    (Giocatore di turno non può giocare Tile)
    - Play a tile  
    - End of turn  (passare il turno)

- Note:
    - come gestire una singola Tile che può generare 2 mosse?
    - come gestire skip turn?


&nbsp;
# Problemi

- come gestire le mosse possibili e non? inutile costruire un albero inserendo le mosse non possibili
- rispiegare al prof il caso del fare punti negativi se io avanzo meno tessere ma di maggior valore
- cosa succede se giocatori hanno stesso numero di tile? vince chi ha quella di minor valore?
- il giocare perpendicolarmente le coppie non ha alcun impatto, senza interfaccia grafica non può essere nemmeno notato


&nbsp;
# Regole versione deterministica

Si pescano 14 tile ciascuno, inizia chi ha doppio 6 e la prima mossa dev'essere il doppio 6.

Il campo da gioco è una coda con 2 estremità, per giocare una tile bisogna far combaciare un numero della tile con un'estremità. ~~le coppie vanno giocate perpendicolarmente~~

La partita finisce quando un giocatore posiziona la sua ultima tile o quando nessuno può più posizionarne.

Vince chi in mano ha la sommatoria minore e fa punti pari alla differenza.


&nbsp;
# Regole Gioco

https://www.mastersofgames.com/rules/dominoes-rules.htm


&nbsp;
**VERSIONE ‘BLOCK’**

Ogni giocatore pesca 6 tiles, inizia chi ha doppio numero partendo dal 6, se nessuno ha un doppio si ripesca.

Il primo tile giocato dev’essere quello che ha fatto cominciare la partita.

Il turno è posizionare una tile con numero combaciante a una delle due estremità della catena sul tavolo, e va posizionato longitudinalmente, le coppie invece possono andare in 3 direzioni ma sempre perpendicolari alle non-coppie.

Il gioco finisce quando un giocatore ha esaurito le sue tile, o se nessuno può più giocarne; in ogni caso vince chi ne ha di meno e si fanno N punti dove N sono i dots nelle tiles nella mano del giocatore perdente; se nessuno ha esaurito le tile si fa la differenza dei dots tra vincitore e perdente. [cosa succede se vinco ma ho 14 dots e il mio avversario ha più tessere ma solo 11 dots? o prendo 0 pt, o valore assoluto, non si può perdere punti per avere vinto la partita].

Vince chi arriva a X punti, solitamente X=100 o 121.


&nbsp;
**VERSIONE ‘DRAW’**

Una variante del Block, però si inizia con 7 tile se si è in 2 giocatori, se non si può giocare si pesca, se non si può più pescare si passa.

Questa variante consente maggior controllo e deduzione in quanto tutti i tile verranno giocati, mentre nel Block alcuni restano fuori dalla partita.


&nbsp;
# Pseudocodice MinMax
```
function minimax(position, depth, aplha, beta, maximizing_player)
    if depth == 0 OR game over in position 
        return static evaluation of position

    if maximizing_player
        max_eval = -infty
        for each child of position
            eval = minimax(position, depth - 1, alpha, beta, false)
            max_eval = max(max_eval, eval)
            alpha = max(alpha, eval)
            if beta <= alpha
                break
        return max_eval

    else
        min_eval = +infty
        for each child of position  
            eval = minimax(position, depth - 1, aplha, beta, true)
            min_eval = min(min_eval, eval)
            beta = min(beta, eval)
            if beta <= alpha
                break
        return min_eval
```