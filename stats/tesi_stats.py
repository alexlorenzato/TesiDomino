import re
import argparse

"""
Da eseguire dalla cartella in cui sono presenti i file .log da dargli in input!

Prende una serie di file .log in input da riga di comando, li parsa tutti linea per linea strutturandone i dati.
Una volta concluso il parsing di tutti i file di input, calcola delle statistiche sui dati raccolti.

python3 tesi_stats.py cmd1.log cmd2.log cmd3.log cmd4.log cmd5.log cmd6.log cmd7.log cmd8.log cmd9.log cmd10.log cmd11.log cmd12.log cmd13.log cmd14.log cmd15.log cmd16.log cmd17.log cmd18.log cmd19.log cmd20.log cmd21.log cmd22.log cmd23.log cmd24.log cmd25.log cmd26.log cmd27.log cmd28.log cmd29.log cmd30.log cmd31.log cmd32.log cmd33.log cmd34.log cmd35.log cmd36.log cmd37.log cmd38.log cmd39.log cmd40.log cmd41.log

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
    
    tmp = 0
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
            tmp += 1
            if(tmp % 10000 == 0):
                print(tmp)
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