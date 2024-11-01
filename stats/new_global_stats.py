import re
import argparse
from collections import Counter

"""
python new_global_stats.py cmd1.log cmd2.log --output statistiche_globali.txt --leaf_distribution distribuzione_foglie.txt

"""

###### Classe ######

class GameData:
    def __init__(self, player1_hand, player2_hand, minimax_scores, best_value, game_duration):
        self.player1_hand = player1_hand  # Lista di tuple (dominoes)
        self.player2_hand = player2_hand  # Lista di tuple (dominoes)
        self.minimax_scores = minimax_scores  # Lista di punteggi minimax
        self.best_value = best_value  # Miglior punteggio possibile (value)
        self.game_duration = game_duration  # Durata in millisecondi

    def __repr__(self):
        return f"<GameData Player1: {self.player1_hand}, Player2: {self.player2_hand}, Best Value: {self.best_value}, Duration: {self.game_duration} ms>"

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
        if len(sections) < 5:
            print(f"Errore: la riga non contiene abbastanza sezioni. Contiene: {len(sections)}")
            return None  
        
        player1_hand = parseHand(sections[0])
        player2_hand = parseHand(sections[1])
        minimax_scores = parseMinimaxScores(sections[2])
        best_value = int(sections[3])
        game_duration = int(sections[4])

        return GameData(player1_hand, player2_hand, minimax_scores, best_value, game_duration)

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


###### Funzioni statistiche ######

def avgTime(game_data_list):
    total_duration = sum(game.game_duration for game in game_data_list)
    return total_duration / len(game_data_list) if game_data_list else 0

def avgLeaves(game_data_list):
    total_leaves = sum(len(game.minimax_scores) for game in game_data_list)
    return total_leaves / len(game_data_list) if game_data_list else 0

def avgEndings(game_data_list):
    total_x_leaves = 0
    total_leaves = 0
    for game in game_data_list:
        x_leaves = sum(1 for score, has_x in game.minimax_scores if has_x)
        total_x_leaves += x_leaves
        total_leaves += len(game.minimax_scores)
    return total_x_leaves / total_leaves if total_leaves > 0 else 0

def leafDistribution(game_data_list):
    leaf_counts = [len(game.minimax_scores) for game in game_data_list if game is not None]
    distribution = Counter(leaf_counts)
    return distribution

def findGameWithMostLeaves(game_data_list):
    return max(game_data_list, key=lambda game: len(game.minimax_scores), default=None)


###### Codice principale ######

def main():
    parser = argparse.ArgumentParser(description="Processa file di domino e calcola statistiche.")
    parser.add_argument('file_paths', type=str, nargs='+', help="Il percorso dei file di dati domino.")
    parser.add_argument('--output', type=str, default="statistics_output.txt", help="File di output per le statistiche.")
    parser.add_argument('--leaf_distribution', type=str, default="leaf_distribution.txt", help="File per la distribuzione delle foglie.")
    args = parser.parse_args()

    all_game_data = []
    
    for file_path in args.file_paths:
        print(f"Processando file: {file_path}")
        game_data_list = parseFile(file_path)
        all_game_data.extend(game_data_list)

    if all_game_data:
        avg_time = avgTime(all_game_data)
        avg_leaves = avgLeaves(all_game_data)
        avg_endings = avgEndings(all_game_data)
        game_with_most_leaves = findGameWithMostLeaves(all_game_data)

        with open(args.output, 'w') as stats_file:
            stats_file.write("Statistiche Partite:\n")
            stats_file.write(f"Durata media delle partite: {avg_time:.2f} ms\n")
            stats_file.write(f"Numero medio di foglie: {avg_leaves:.2f}\n")
            stats_file.write(f"Percentuale media di foglie con 'X': {avg_endings * 100:.2f}%\n")
            
            if game_with_most_leaves:
                stats_file.write("\nPartita con il maggior numero di foglie:\n")
                stats_file.write(f"Numero di foglie: {len(game_with_most_leaves.minimax_scores)}\n")
                stats_file.write(f"Mano giocatore 1: {game_with_most_leaves.player1_hand}\n")
                stats_file.write(f"Mano giocatore 2: {game_with_most_leaves.player2_hand}\n")
        

        distribution = leafDistribution(all_game_data)
        with open(args.leaf_distribution, 'w') as dist_file:
            max_leaves = max(distribution.keys(), default=0)
            for leaves in range(1, max_leaves + 1):
                count = distribution.get(leaves, 0)
                dist_file.write(f"{leaves}: {count}\n")

# Avvia il programma
if __name__ == "__main__":
    main()