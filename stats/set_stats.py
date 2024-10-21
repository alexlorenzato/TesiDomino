import re
import argparse

"""
Comando: python3 script.py --hand "1|2 2|3 4|5" file1.log file2.log file3.log


"""

# Classe che rappresenta i dati di una partita
class GameData:
    def __init__(self, player1_hand, player2_hand, minimax_scores, best_value, game_duration):
        self.player1_hand   = player1_hand    # Lista di tuple (dominoes)
        self.player2_hand   = player2_hand    # Lista di tuple (dominoes)
        self.minimax_scores = minimax_scores  # Lista di punteggi minimax
        self.best_value     = best_value      # Miglior punteggio possibile (value)
        self.game_duration  = game_duration   # Durata in millisecondi

    def contains_hand(self, hand):
        # Verifica se una delle due mani del gioco è uguale alla mano specificata
        return self.player1_hand == hand or self.player2_hand == hand

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
def process_file(file_path, target_hand):
    target_hand = sorted(parse_hand(target_hand))  # Ordina la mano specificata
    matching_games = []

    with open(file_path, 'r') as file:
        for line in file:
            game_data = parse_line(line)
            if game_data and game_data.contains_hand(target_hand):
                matching_games.append(game_data)
    
    return matching_games

# Funzione per calcolare la durata media delle partite
def calculate_avg_duration(games):
    total_duration = sum(game.game_duration for game in games)
    return total_duration / len(games) if games else 0

# Funzione per calcolare il numero medio di foglie (minimax scores)
def calculate_avg_leaves(games):
    total_leaves = sum(len(game.minimax_scores) for game in games)
    return total_leaves / len(games) if games else 0

# Funzione per calcolare la percentuale di partite con almeno una 'X' nei minimax scores
def calculate_x_percentage(games):
    total_x_games = sum(1 for game in games if any(leaf[1] for leaf in game.minimax_scores))
    return (total_x_games / len(games)) * 100 if games else 0



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
        avg_leaves = calculate_avg_leaves(all_matching_games)
        x_percentage = calculate_x_percentage(all_matching_games)

        print(f"Statistiche per la mano {args.hand}:")
        print(f"Durata media delle partite: {avg_duration:.2f} ms")
        print(f"Numero medio di foglie: {avg_leaves:.2f}")
        print(f"Percentuale di partite con 'X': {x_percentage:.2f}%")
    else:
        print(f"Nessuna partita trovata con la mano {args.hand}.")

# Esegue il programma
if __name__ == "__main__":
    main()
