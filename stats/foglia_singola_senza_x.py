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
                matching_games.append(game)
    return matching_games

###### Codice principale ######

def main():
    parser = argparse.ArgumentParser(description="Trova partite con una sola foglia senza X.")
    parser.add_argument('file_paths', type=str, nargs='+', help="Il percorso dei file di dati domino.")
    parser.add_argument('--output', type=str, default="matching_games.txt", help="File di output per le partite trovate.")
    args = parser.parse_args()

    with open(args.output, 'w') as output_file:
        for file_path in args.file_paths:
            print(f"Processando file: {file_path}")
            game_data_list = parseFile(file_path)
            matching_games = findSingleLeafNoX(game_data_list)

            # Scrivi l'intestazione per indicare il file corrente
            output_file.write(f"=== Risultati per file: {file_path} ===\n")
            if matching_games:
                for game in matching_games:
                    output_file.write(f"Mano Giocatore 1: {game.player1_hand}\n")
                    output_file.write(f"Mano Giocatore 2: {game.player2_hand}\n")
                    output_file.write(f"Foglia: {game.minimax_scores}\n")
                    output_file.write("\n")
            else:
                output_file.write("Nessuna partita trovata con una sola foglia senza X.\n")
            output_file.write("\n")  # Separatore tra i file

# Avvia il programma
if __name__ == "__main__":
    main()
