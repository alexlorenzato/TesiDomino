import re
import argparse

"""
Da eseguire dalla cartella in cui sono presenti i file .log da dargli in input!

Prende una serie di file .log in input da riga di comando, li parsa tutti linea per linea strutturandone i dati.
Una volta concluso il parsing di tutti i file di input, calcola delle statistiche sui dati raccolti.
"""

###### Classe ######

class GameData:
    def __init__(self, player1_hand, player2_hand, minimax_scores, best_value, game_duration):
        self.player1_hand   = player1_hand    # Lista di tuple (dominoes)
        self.player2_hand   = player2_hand    # Lista di tuple (dominoes)
        self.minimax_scores = minimax_scores  # Lista di punteggi minimax
        self.best_value     = best_value      # Miglior punteggio possibile (value)
        self.game_duration  = game_duration   # Durata in millisecondi

    def __repr__(self):
        return f"<GameData Player1: {self.player1_hand}, Player2: {self.player2_hand}, Best Value: {self.best_value}, Duration: {self.game_duration} ms>"

    def print_minimax_scores(self):
        print("Minimax Scores:")
        for score, has_x in self.minimax_scores:
            print(f"{score}, {has_x}")


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
    sections = re.split(r'\s{2,}', line.strip())
    if len(sections[-1].split()) > 1:
        sections.extend(sections.pop().split())

    if len(sections) < 5:
        print(f"Errore: la riga non contiene abbastanza sezioni. Ne contiene: ", len(sections))
        return None  
    
    player1_hand   = parseHand(sections[0])
    player2_hand   = parseHand(sections[1])
    minimax_scores = parseMinimaxScores(sections[2])
    best_value     = int(sections[3])
    game_duration  = int(sections[4])

    return GameData(player1_hand, player2_hand, minimax_scores, best_value, game_duration)


"""Legge il file e parsa ogni riga in un oggetto GameData."""
def parseFile(file_path):
    game_data_list = []
    with open(file_path, 'r') as file:
        for line in file:
            game_data = parseLine(line)
            game_data_list.append(game_data)
    return game_data_list


###### Funzioni statistiche ######


""" Durata media partita in ms """
def avgTime(list):
    avg = 0
    for item in list:
        avg += item.game_duration
    return avg / len(list)


""" Numero medio di foglie di un albero (nota: non valore, ma numero di foglie) """
def avgLeaves(list):
    avg = 0
    for item in list:
       avg += len(item.minimax_scores) 
    return avg / len(list)


""" Quante partite con la X """
def avgEndings(list):
    count = 0
    total = 0
    for item in list:
        count = 0
        for leaf in item.minimax_scores:
            if(leaf[1]):
               count+=1
        total += (count / len(item.minimax_scores))
    return total / len(list)


###### Codice da eseguire ######


def main():
    parser = argparse.ArgumentParser(description="Processa un file di domino.")
    parser.add_argument('file_paths', type=str, nargs='+', help="Il percorso del file di dati domino.")
    args = parser.parse_args()

    all_game_data = []
    
    # Processa ogni file e aggiungi i dati alla lista globale
    for file_path in args.file_paths:
        print(f"Processando file: {file_path}")
        game_data_list = parseFile(file_path)
        all_game_data.extend(game_data_list)

    # Calcola le statistiche sui dati aggregati
    if all_game_data:
        avg_time    = avgTime(all_game_data)
        avg_leaves  = avgLeaves(all_game_data)
        avg_endings = avgEndings(all_game_data)

    print("avg_time:",    avg_time)
    print("avg_leaves:",  avg_leaves)
    print("avg_endings:", avg_endings)


# Avvia il programma
if __name__ == "__main__":
    main()