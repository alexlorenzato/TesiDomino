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

def parseFile(file_path):
    game_data_list = []
    with open(file_path, 'r') as file:
        for line in file:
            try:
                game_data = parseLine(line)
                if game_data:  # Aggiungi solo se `game_data` non è None
                    game_data_list.append(game_data)
            except Exception as e:
                print(f"Errore durante il parsing della riga: {line.strip()}. Errore: {e}")
    return game_data_list


def parseLine(line):
    try:
        sections = re.split(r'\s{2,}', line.strip())
        if len(sections[-1].split()) > 1:
            sections.extend(sections.pop().split())

        if len(sections) < 5:
            print(f"Errore: la riga non contiene abbastanza sezioni. Ne contiene: {len(sections)}")
            return None  

        player1_hand   = parseHand(sections[0])
        player2_hand   = parseHand(sections[1])
        minimax_scores = parseMinimaxScores(sections[2])
        best_value     = int(sections[3])
        game_duration  = int(sections[4])

        return GameData(player1_hand, player2_hand, minimax_scores, best_value, game_duration)

    except Exception as e:
        print(f"Errore nel parsing della riga: {line.strip()}. Errore: {e}")
        return None



###### Funzioni di parsing ######

# Funzione per trovare e stampare le mani dei giocatori della partita con il maggior numero di foglie
def print_max_leaves_game(games):
    if not games:
        print("Nessuna partita disponibile.")
        return

    max_leaves_game = max(games, key=lambda game: len(game.minimax_scores))
    max_leaves_count = len(max_leaves_game.minimax_scores)

    print("\nPartita con il maggior numero di foglie:")
    print(f"Numero di foglie: {max_leaves_count}")
    print(f"Mano del giocatore 1: {max_leaves_game.player1_hand}")
    print(f"Mano del giocatore 2: {max_leaves_game.player2_hand}")




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

    
    print_max_leaves_game(all_game_data)


# Avvia il programma
if __name__ == "__main__":
    main()