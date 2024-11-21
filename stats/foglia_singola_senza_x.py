"""
Cerca partite che hanno una sola foglia e non hanno la X (quindi un giocatore ha esaurito le tessere) 
e scrive sul file specificato da terminale la mano in questione e il valore della foglia.

Comando:

python3 foglia_singola_senza_x.py cmd1.log cmd2.log cmd3.log cmd4.log cmd5.log cmd6.log cmd7.log cmd8.log cmd9.log cmd10.log cmd11.log cmd12.log cmd13.log cmd14.log cmd15.log cmd16.log cmd17.log cmd18.log cmd19.log cmd20.log cmd21.log cmd22.log cmd23.log cmd24.log cmd25.log cmd26.log cmd27.log cmd28.log cmd29.log cmd30.log cmd31.log cmd32.log cmd33.log cmd34.log cmd35.log cmd36.log cmd37.log cmd38.log cmd39.log cmd40.log cmd41.log --output foglia_singola.txt
python foglia_singola_senza_x.py cmd3.log  --output foglia_singola.txt
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

def findSingleLeafNoX(game_data_list):
    matching_games = []
    for game in game_data_list:
        if len(game.minimax_scores) == 1:  # Controlla se c'è solo una foglia
            score, has_x = game.minimax_scores[0]
            if not has_x:  # Controlla se il punteggio non ha la X
                print("Trovata")
                matching_games.append(game)
    return matching_games

###### Codice principale ######

def main():
    parser = argparse.ArgumentParser(description="Trova partite con una sola foglia senza X.")
    parser.add_argument('file_paths', type=str, nargs='+', help="Il percorso dei file di dati domino.")
    parser.add_argument('--output', type=str, default="matching_games.txt", help="File di output per le partite trovate.")
    args = parser.parse_args()

    all_game_data = []
    
    for file_path in args.file_paths:
        print(f"Processando file: {file_path}")
        game_data_list = parseFile(file_path)
        all_game_data.extend(game_data_list)

    if all_game_data:
        matching_games = findSingleLeafNoX(all_game_data)

        with open(args.output, 'w') as output_file:
            output_file.write(f"Partite con una sola foglia e senza X: {len(matching_games)}\n")
            for game in matching_games:
                output_file.write(f"Mano Giocatore 1: {game.player1_hand}\n")
                output_file.write(f"Mano Giocatore 2: {game.player2_hand}\n")
                output_file.write(f"Foglia: {game.minimax_scores}\n")
                output_file.write("\n")

# Avvia il programma
if __name__ == "__main__":
    main()
