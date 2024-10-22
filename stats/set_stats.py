import re
import argparse

"""
Comando: python3 script.py --hand "1|2 2|3 4|5" file1.log file2.log file3.log

Prende una mano di tile in ingresso e cerca tutte le partite con quella mano, poi calcola stats su quelle partite.
"""

# Classe che rappresenta i dati di una partita
class GameData:
    def __init__(self, player1_hand, player2_hand, minimax_scores, best_value, game_duration):
        self.player1_hand   = player1_hand    # Lista di tuple (dominoes)
        self.player2_hand   = player2_hand    # Lista di tuple (dominoes)
        self.minimax_scores = minimax_scores  # Lista di punteggi minimax
        self.best_value     = best_value      # Miglior punteggio possibile (value)
        self.game_duration  = game_duration   # Durata in millisecondi

    # Verifica se una delle due mani è la mano target, e ritorna 1 se appartiene al player1, 2 se appartiene al player2
    def contains_hand(self, hand):
        if self.player1_hand == hand:
            return 1
        elif self.player2_hand == hand:
            return 2
        return 0

# Funzione per parsare una stringa di domino in una lista di tuple (es. '1|2 3|4' diventa [(1,2), (3,4)])
def parse_hand(hand_str):
    return [tuple(map(int, domino.split('|'))) for domino in hand_str.split()]

# Funzione per parsare i punteggi minimax, cercando la presenza della 'X' e rimuovendola
def parse_minimax_scores(scores_str):
    minimax_scores = []
    for score in scores_str.split():
        if 'X' in score:
            minimax_scores.append((int(score.replace('X', '')), True))  # Se c'è la 'X', True
        else:
            minimax_scores.append((int(score), False))
    return minimax_scores

# Funzione per parsare una singola riga del file di log e restituire un oggetto GameData
def parse_line(line):
    sections = re.split(r'\s{2,}', line.strip())
    if len(sections[-1].split()) > 1:
        sections.extend(sections.pop().split())

    if len(sections) < 5:
        return None  # Ritorna None se la riga non ha abbastanza dati
    
    # Parsiamo i dati della partita
    player1_hand   = parse_hand(sections[0])
    player2_hand   = parse_hand(sections[1])
    minimax_scores = parse_minimax_scores(sections[2])
    best_value     = int(sections[3])
    game_duration  = int(sections[4])

    return GameData(player1_hand, player2_hand, minimax_scores, best_value, game_duration)

# Funzione per leggere un file di log e filtrare le partite in base alla mano specificata
# Salva se la mano appartiene al giocatore 1 o 2
def process_file(file_path, target_hand):
    target_hand = sorted(parse_hand(target_hand))  # Ordina la mano specificata
    matching_games = []

    with open(file_path, 'r') as file:
        for line in file:
            game_data = parse_line(line)
            if game_data:
                player_position = game_data.contains_hand(target_hand)
                if player_position:  # Se la mano è trovata
                    matching_games.append((game_data, player_position))  # Aggiungi la partita e la posizione del giocatore
    
    return matching_games

# Funzione per calcolare la durata media delle partite
def calculate_avg_duration(games):
    total_duration = sum(game.game_duration for game, _ in games)
    return total_duration / len(games) if games else 0

# Funzione per calcolare il numero medio di foglie (minimax scores)
def calculate_avg_leaves(games):
    total_leaves = sum(len(game.minimax_scores) for game, _ in games)
    return total_leaves / len(games) if games else 0

# Funzione per calcolare il numero medio di foglie che hanno la 'X'
def calculate_x_leaves(games):
    total_x_leaves = 0
    total_leaves = 0
    for game, _ in games:
        total_leaves += len(game.minimax_scores)
        total_x_leaves += sum(1 for score, has_x in game.minimax_scores if has_x)  # Conta solo le foglie con 'X'
    return total_x_leaves / total_leaves if total_leaves > 0 else 0

# Funzione per calcolare percentuale di vittorie
def calculate_wins(games):
    wins = 0
    for game, player_position in games:
        if player_position == 1 and game.best_value > 0:
            wins += 1  # Il giocatore 1 vince se il punteggio è > 0
        elif player_position == 2 and game.best_value < 0:
            wins += 1  # Il giocatore 2 vince se il punteggio è < 0
    
    return wins / len(games) if games else 0

# Funzione principale per eseguire il programma
def main():
    parser = argparse.ArgumentParser(description="Processa file di domino e calcola statistiche per mani specifiche.")
    parser.add_argument('file_paths', nargs='+', help="I percorsi dei file di log di domino.")
    parser.add_argument('--hand', required=True, help="La mano specificata da cercare (es. '1|2 2|3 4|5').")
    args = parser.parse_args()

    all_matching_games = []

    # Processa ogni file e raccoglie le partite che contengono la mano specificata
    for file_path in args.file_paths:
        print(f"Processando file: {file_path}")
        matching_games = process_file(file_path, args.hand)
        all_matching_games.extend(matching_games)

    # Se ci sono partite trovate, calcola le statistiche
    if all_matching_games:
        avg_duration = calculate_avg_duration(all_matching_games)
        avg_leaves   = calculate_avg_leaves(all_matching_games)
        win_rate     = calculate_wins(all_matching_games)
        x_leaves_rate = calculate_x_leaves(all_matching_games)

        print(f"Statistiche per la mano {args.hand}:")
        print(f"Durata media delle partite: {avg_duration:.2f} ms")
        print(f"Numero medio di foglie: {avg_leaves:.2f}")
        print(f"Percentuale di vittorie: {win_rate * 100:.2f}%")
        print(f"Percentuale di foglie con 'X': {x_leaves_rate * 100:.2f}%")
    else:
        print(f"Nessuna partita trovata con la mano {args.hand}.")

# Esegue il programma
if __name__ == "__main__":
    main()
