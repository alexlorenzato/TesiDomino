
# Regole 

### Double-6

28 tiles totali.

Si pescano 14 tile ciascuno, inizia chi ha doppio 6 e la prima mossa dev'essere il doppio 6.

Il campo da gioco è una coda con 2 estremità, per giocare una tile bisogna far combaciare un numero della tile con un'estremità.

La partita finisce quando un giocatore posiziona la sua ultima tile o quando nessuno può più posizionarne.

Vince chi in mano ha la sommatoria minore e fa punti pari alla differenza.

### Double-9

55 tessere totali.

### Double-12

91 tessere totali.

&nbsp;
# Note

Fonte iniziale per le regole: https://www.mastersofgames.com/rules/dominoes-rules.htm


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