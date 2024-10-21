import re
import argparse

"""
Da eseguire dalla cartella in cui sono presenti i file .log da dargli in input!

Prende lista di file .log da riga di comando e ne riordina le mani
"""

# Funzione per parsare una stringa di tessere in una lista di tuple (dominoes)
def parse_hand(hand_str):
    try:
        return [tuple(map(int, domino.split('|'))) for domino in hand_str.split()]
    except ValueError:
        print(f"Errore nel parsing delle tessere: {hand_str}")
        return []

# Funzione per ordinare le tessere del domino in base alla regola fornita
def sort_dominoes(hand):
    return sorted(hand, key=lambda x: (min(x), max(x)))

# Funzione per parsare una singola riga, ordinare le tessere e riscrivere la riga
def process_line(line):
    try:
        sections = re.split(r'\s{2,}', line.strip())
        if len(sections[-1].split()) > 1:
            sections.extend(sections.pop().split())

        if len(sections) < 5:
            print(f"Errore: la riga non contiene abbastanza sezioni. Ne contiene: {len(sections)}")
            return None

        # Parso le mani dei giocatori (le prime due sezioni)
        player1_hand = parse_hand(sections[0])
        player2_hand = parse_hand(sections[1])

        # Se il parsing delle mani non è riuscito, ritorno None
        if not player1_hand or not player2_hand:
            return None

        # Ordino le mani dei giocatori
        sorted_player1_hand = sort_dominoes(player1_hand)
        sorted_player2_hand = sort_dominoes(player2_hand)

        # Ricostruisco la riga con le tessere ordinate
        sorted_line = (
            ' '.join([f"{a}|{b}" for a, b in sorted_player1_hand]) + ' ' * 5 +  # Player 1 hand
            ' '.join([f"{a}|{b}" for a, b in sorted_player2_hand]) + ' ' * 5 +  # Player 2 hand
            sections[2] + ' ' * 5 +                                             # Minimax scores
            sections[3] + ' ' * 5 +                                             # Best value
            sections[4]                                                         # Game duration
        )

        return sorted_line
    except Exception as e:
        print(f"Errore nella riga: {line.strip()} -> {str(e)}")
        return None

# Funzione per leggere e processare il contenuto di un singolo file e sovrascriverlo
def process_file(file_path):
    print(f"Processing file: {file_path}")

    try:
        with open(file_path, 'r') as file:
            lines = file.readlines()

        modified_lines = []
        x = 0
        for line in lines:
            x += 1
            if x % 1000000 == 0:
                print(f"Processing line {x}")
            sorted_line = process_line(line)
            if sorted_line:
                modified_lines.append(sorted_line + '\n')

        # Sovrascrivi il file originale con le righe modificate
        with open(file_path, 'w') as file:
            file.writelines(modified_lines)

    except Exception as e:
        print(f"Errore durante la lettura o scrittura del file {file_path}: {str(e)}")

# Funzione principale per gestire più file
def main():
    parser = argparse.ArgumentParser(description="Processa più file di domino.")
    parser.add_argument('file_paths', nargs='+', help="I percorsi dei file di dati domino.")
    args = parser.parse_args()

    for file_path in args.file_paths:
        process_file(file_path)

# Avvia il programma
if __name__ == "__main__":
    main()
