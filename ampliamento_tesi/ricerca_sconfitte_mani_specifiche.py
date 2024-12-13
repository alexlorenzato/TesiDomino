"""
Questo file serve per cercare le partite in cui i set del tipo x|0...x|6 perdono, di solito
hanno il 99.9% di possiblilità di vincere, devo trovare contro chi perdono.

python3 ricerca_sconfitte_mani_specifiche.py --hand "0|3 1|3 2|3 3|3 3|4 3|5 3|6" tutti_3.log --output perdite_tutti3.txt
"""

import re
import argparse

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
        sorted_hand = sorted(hand)  # Ordiniamo la mano da confrontare
        player1_hand_sorted = sorted(self.player1_hand)  # Ordiniamo la mano del giocatore 1
        player2_hand_sorted = sorted(self.player2_hand)  # Ordiniamo la mano del giocatore 2

        print(f"Confronto interno:\n"
            f" - Mano target: {sorted_hand}\n"
            f" - Giocatore 1 (ordinata): {player1_hand_sorted}\n"
            f" - Giocatore 2 (ordinata): {player2_hand_sorted}")

        if player1_hand_sorted == sorted_hand:
            print("** Match con Giocatore 1 **")
            return 1
        elif player2_hand_sorted == sorted_hand:
            print("** Match con Giocatore 2 **")
            return 2

        print("** Nessun match trovato **")
        return 0


# Funzione per parsare una stringa di domino in una lista di tuple (es. '1|2 3|4' diventa [(1,2), (3,4)]),
# gestendo eventuali errori di parsing.
def parse_hand(hand_str):
    try:
        return [tuple(map(int, domino.split('|'))) for domino in hand_str.split()]
    except ValueError as e:
        print(f"Errore nel parsing della mano: {hand_str} -- {e}")
        return []

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
    try:
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
    
    except (ValueError, IndexError) as e:
        # Se si verifica un errore durante il parsing della riga, la ignoriamo
        print(f"Errore nel parsing della riga: {line.strip()} -- {e}")
        return None

# Funzione per leggere un file di log e filtrare le partite in base alla mano specificata
# Salva se la mano appartiene al giocatore 1 o 2
def process_file(file_path, target_hand):
    target_hand = sorted(parse_hand(target_hand))  # Ordina la mano specificata
    print(f"Mano target ordinata: {target_hand}")  # Debug chiaro e unico
    matching_games = []

    try:
        with open(file_path, 'r') as file:
            for line in file:
                game_data = parse_line(line)
                if game_data:
                    # Confrontiamo la mano target con le mani dei giocatori
                    player1_hand_sorted = sorted(game_data.player1_hand)
                    player2_hand_sorted = sorted(game_data.player2_hand)

                    #print(f"Confronto mani:\n"
                    #      f" - Mano target: {target_hand}\n"
                    #      f" - Giocatore 1: {player1_hand_sorted}\n"
                    #      f" - Giocatore 2: {player2_hand_sorted}")  # Debug durante confronto

                    player_position = game_data.contains_hand(target_hand)

                    if player_position:  # Se c'è un match
                        print(f"** Match trovato! Giocatore {player_position} **")  # Notifica chiara del match
                        matching_games.append((game_data, player_position))  # Aggiungi la partita e la posizione
    except FileNotFoundError:
        print(f"File non trovato: {file_path}")
    except Exception as e:
        print(f"Errore nell'aprire il file: {file_path} -- {e}")

    print(f"Partite trovate: {len(matching_games)}")  # Totale partite trovate
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

# Funzione per salvare le partite perse su file
def save_lost_games(games, output_file, target_hand):
    lost_games = []
    for game, player_position in games:
        if player_position == 1 and game.best_value < 0:  # Giocatore 1 perde se il punteggio è negativo
            lost_games.append(game)
        elif player_position == 2 and game.best_value > 0:  # Giocatore 2 perde se il punteggio è positivo
            lost_games.append(game)

    with open(output_file, 'w') as f:
        if lost_games:
            for game in lost_games:
                # Costruire la riga in formato tab-separated
                player1_hand = " ".join(f"{d[0]}|{d[1]}" for d in game.player1_hand)
                player2_hand = " ".join(f"{d[0]}|{d[1]}" for d in game.player2_hand)
                minimax_scores = " ".join(
                    f"{score}{'X' if has_x else ''}" for score, has_x in game.minimax_scores
                )
                line = f"{player1_hand}\t{player2_hand}\t{minimax_scores}\t{game.best_value}\t{game.game_duration}"
                f.write(line + "\n")
        else:
            f.write(f"Nessuna partita persa trovata con la mano {target_hand}.\n")
    
    print(f"Partite perse scritte su {output_file} (formato originale).")

def main():
    parser = argparse.ArgumentParser(description="Processa file di domino e calcola statistiche per mani specifiche.")
    parser.add_argument('file_paths', nargs='+', help="I percorsi dei file di log di domino.")
    parser.add_argument('--hand', required=True, help="La mano specificata da cercare (es. '1|2 2|3 4|5').")
    parser.add_argument('--output', required=True, help="Il file di output dove scrivere le statistiche.")
    parser.add_argument('--lost_games', required=True, help="Il file di output dove scrivere le partite perse.")
    args = parser.parse_args()

    all_matching_games = []

    # Processa ogni file e raccoglie le partite che contengono la mano specificata
    for file_path in args.file_paths:
        print(f"Processando file: {file_path}")
        matching_games = process_file(file_path, args.hand)
        all_matching_games.extend(matching_games)



    # Scrive le partite perse su un file separato
    save_lost_games(all_matching_games, args.lost_games, args.hand)

    print(f"Risultati scritti su {args.output}")
    print(f"Partite perse scritte su {args.lost_games}")

# Esegue il programma
if __name__ == "__main__":
    main()