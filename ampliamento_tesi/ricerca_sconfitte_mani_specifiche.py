"""
Questo file serve per cercare le partite in cui i set del tipo x|0...x|6 perdono, di solito
hanno il 99.9% di possiblilità di vincere, devo trovare contro chi perdono.

python3 set_stats_on_file.py --hand "0|3 1|3 2|3 3|3 3|4 3|5 3|6" tutti_3.log --output perdite_tutti3.txt
"""

import re
import argparse

class GameData:
    def __init__(self, player1_hand, player2_hand, best_value):
        self.player1_hand = player1_hand  # Lista di tuple (dominoes)
        self.player2_hand = player2_hand  # Lista di tuple (dominoes)
        self.best_value = best_value      # Miglior punteggio possibile (value)

    # Verifica se una delle due mani è la mano target e ritorna 1 se appartiene al player1, 2 se appartiene al player2
    def contains_hand(self, hand):
        sorted_hand = sorted(hand)  # Ordiniamo la mano da confrontare
        if sorted(self.player1_hand) == sorted_hand:
            return 1
        elif sorted(self.player2_hand) == sorted_hand:
            return 2
        return 0

# Funzione per parsare una stringa di domino in una lista di tuple (es. '1|2 3|4' diventa [(1,2), (3,4)])
def parse_hand(hand_str):
    try:
        return [tuple(map(int, domino.split('|'))) for domino in hand_str.split()]
    except ValueError as e:
        print(f"Errore nel parsing della mano: {hand_str} -- {e}")
        return []

# Funzione per parsare una singola riga del file di log e restituire un oggetto GameData
def parse_line(line):
    try:
        sections = re.split(r'\s{2,}', line.strip())
        if len(sections) < 3:
            return None  # Ritorna None se la riga non ha abbastanza dati

        # Parsiamo i dati della partita
        player1_hand = parse_hand(sections[0])
        player2_hand = parse_hand(sections[1])
        best_value = int(sections[2])

        return GameData(player1_hand, player2_hand, best_value)
    except (ValueError, IndexError) as e:
        print(f"Errore nel parsing della riga: {line.strip()} -- {e}")
        return None

# Funzione per leggere un file di log e trovare le partite in cui il giocatore perde
def find_losing_games(file_path, target_hand):
    target_hand = sorted(parse_hand(target_hand))  # Ordina la mano specificata
    losing_games = []

    try:
        with open(file_path, 'r') as file:
            for line in file:
                game_data = parse_line(line)
                if game_data:
                    player_position = game_data.contains_hand(target_hand)
                    if (player_position == 1 and game_data.best_value < 0) or \
                       (player_position == 2 and game_data.best_value > 0):
                        losing_games.append(line.strip())
    except FileNotFoundError:
        print(f"File non trovato: {file_path}")
    except Exception as e:
        print(f"Errore nell'aprire il file: {file_path} -- {e}")

    return losing_games

# Funzione principale per eseguire il programma e scrivere le partite perse su file
def main():
    parser = argparse.ArgumentParser(description="Trova partite perse in base a una mano specificata.")
    parser.add_argument('file_paths', nargs='+', help="Percorsi dei file di log di domino.")
    parser.add_argument('--hand', required=True, help="Mano specificata da cercare (es. '1|2 2|3 4|5').")
    parser.add_argument('--output', required=True, help="File di output dove scrivere le partite perse.")
    args = parser.parse_args()

    all_losing_games = []

    # Processa ogni file e raccoglie le partite perse
    for file_path in args.file_paths:
        print(f"Processando file: {file_path}")
        losing_games = find_losing_games(file_path, args.hand)
        all_losing_games.extend(losing_games)

    # Scrive le partite perse sul file specificato
    with open(args.output, 'w') as f:
        f.write(f"Mano specificata: {args.hand}\n\n")
        if all_losing_games:
            f.write("Partite perse trovate:\n")
            for game in all_losing_games:
                f.write(game + '\n')
        else:
            f.write("Nessuna partita persa trovata per la mano specificata.\n")

    print(f"Risultati scritti su {args.output}")

# Esegue il programma
if __name__ == "__main__":
    main()
