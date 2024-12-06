"""

python3 all_positive_leaves.py  cmd40.log cmd41.log --output all_positive2.txt
"""

import re
import argparse

###### Classe ######

class GameData:
    def __init__(self, player1_hand, player2_hand, minimax_scores):
        self.player1_hand = player1_hand  # Lista di tuple (dominoes)
        self.player2_hand = player2_hand  # Lista di tuple (dominoes)
        self.minimax_scores = minimax_scores  # Lista di punteggi minimax

###### Funzioni di parsing ######

def parseHand(hand_str):
    return [tuple(map(int, domino.split('|'))) for domino in hand_str.split()]

def parseMinimaxScores(scores_str):
    minimax_scores = []
    for score in scores_str.split():
        if 'X' in score:
            minimax_scores.append((int(score.replace('X', '')), True))
        else:
            minimax_scores.append((int(score), False))
    return minimax_scores

def parseLine(line):
    try:
        sections = re.split(r'\s{2,}', line.strip())
        if len(sections[-1].split()) > 1:
            sections.extend(sections.pop().split())
        if len(sections) < 3:
            print(f"Errore: la riga non contiene abbastanza sezioni. Contiene: {len(sections)}")
            return None  

        player1_hand = parseHand(sections[0])
        player2_hand = parseHand(sections[1])
        minimax_scores = parseMinimaxScores(sections[2])
        return GameData(player1_hand, player2_hand, minimax_scores)

    except (ValueError, IndexError) as e:
        print(f"Errore nel parsing della riga: {line.strip()} -- {e}")
        return None

def parseFile(file_path):
    game_data_list = []
    with open(file_path, 'r') as file:
        for line in file:
            try:
                game_data = parseLine(line)
                if game_data:
                    game_data_list.append(game_data)
            except Exception as e:
                print(f"Errore nel parsing della riga {line.strip()} in {file_path}: {e}")
    return game_data_list

###### Funzione per analizzare le partite ######

def findGamesWithAllPositiveLeaves(game_data_list):
    matching_games = []
    for game in game_data_list:
        try:
            all_positive = all(score > 0 for score, _ in game.minimax_scores)
            if all_positive:
                matching_games.append(game)
        except Exception as e:
            print(f"Errore durante l'analisi della partita: {game} -- {e}")
    return matching_games

###### Codice principale ######

def main():
    parser = argparse.ArgumentParser(description="Trova partite con tutte le foglie > 0.")
    parser.add_argument('file_paths', type=str, nargs='+', help="Il percorso dei file di dati domino.")
    parser.add_argument('--output', type=str, default="positive_leaves_games.txt", help="File di output per le partite trovate.")
    args = parser.parse_args()

    with open(args.output, 'w') as output_file:
        total_matching_games = 0

        for file_path in args.file_paths:
            print(f"Processando file: {file_path}")
            try:
                game_data_list = parseFile(file_path)
                if game_data_list:
                    matching_games = findGamesWithAllPositiveLeaves(game_data_list)
                    total_matching_games += len(matching_games)

                    # Scrive i risultati nel file di output
                    output_file.write(f"File: {file_path}\n")
                    output_file.write(f"Partite con tutte le foglie > 0: {len(matching_games)}\n")
                    for game in matching_games:
                        output_file.write(f"Mano Giocatore 1: {game.player1_hand}\n")
                        output_file.write(f"Mano Giocatore 2: {game.player2_hand}\n")
                        output_file.write("\n")
                    output_file.write("\n")
            except Exception as e:
                print(f"Errore nell'elaborazione del file {file_path}: {e}")
        
        output_file.write(f"Totale partite con tutte le foglie > 0: {total_matching_games}\n")

# Avvia il programma
if __name__ == "__main__":
    main()
