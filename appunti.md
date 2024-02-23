

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
            eval = minimax(position, depth - 1, aplha, beta, false)
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