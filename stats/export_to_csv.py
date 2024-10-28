
"""
Comando:

python3 export_to_csv.py cmd1.log cmd2.log cmd3.log cmd4.log cmd5.log cmd6.log cmd7.log cmd8.log cmd9.log cmd10.log cmd11.log cmd12.log cmd13.log cmd14.log cmd15.log cmd16.log cmd17.log cmd18.log cmd19.log cmd20.log cmd21.log cmd22.log cmd23.log cmd24.log cmd25.log cmd26.log cmd27.log cmd28.log cmd29.log cmd30.log cmd31.log cmd32.log cmd33.log cmd34.log cmd35.log cmd36.log cmd37.log cmd38.log cmd39.log cmd40.log cmd41.log --output raw_data.csv

"""


import re
import csv
import argparse

# Funzione per parsare una stringa di domino in una lista di tuple (es. '1|2 3|4' diventa [(1,2), (3,4)])
def parse_hand(hand_str):
    return [tuple(map(int, domino.split('|'))) for domino in hand_str.split()]

# Funzione per parsare i punteggi minimax, cercando la presenza della 'X' e rimuovendola
def parse_minimax_scores(scores_str):
    minimax_scores = []
    for score in scores_str.split():
        if 'X' in score:
            minimax_scores.append((int(score.replace('X', '')), True))  # Se c'è la 'X', True
        else:
            minimax_scores.append((int(score), False))
    return minimax_scores

# Funzione per parsare una singola riga del file di log e restituire una lista di sezioni grezze
def parse_line(line):
    sections = re.split(r'\s{2,}', line.strip())
    if len(sections[-1].split()) > 1:
        sections.extend(sections.pop().split())

    if len(sections) < 5:
        return None  # Salta la riga se ha dati incompleti

    return sections  # Restituisce le sezioni così come sono

# Funzione per leggere un file di log e raccogliere i dati grezzi di ogni partita nelle 'sections'
def collect_raw_data(file_path):
    raw_data = []
    with open(file_path, 'r') as file:
        for line in file:
            sections = parse_line(line)
            if sections:
                raw_data.append(sections)  # Aggiunge la riga come lista di sezioni
    return raw_data

# Funzione per esportare i dati grezzi in CSV
def export_raw_data_to_csv(all_sections, output_file="raw_data.csv"):
    with open(output_file, mode="w", newline="") as file:
        writer = csv.writer(file)
        writer.writerow(["Player1Hand", "Player2Hand", "MinimaxScores", "BestValue", "GameDuration"])  # Header

        # Scrive tutte le sezioni in CSV
        for sections in all_sections:
            writer.writerow(sections)

def main():
    parser = argparse.ArgumentParser(description="Esporta dati grezzi di domino dai log in un file CSV.")
    parser.add_argument('file_paths', nargs='+', help="I percorsi dei file di log di domino.")
    parser.add_argument('--output', default="raw_data.csv", help="File CSV di output per dati grezzi.")
    args = parser.parse_args()

    all_sections = []  # Lista per raccogliere tutti i dati grezzi di ogni partita

    # Processa ogni file e raccoglie le sezioni dei dati grezzi
    for file_path in args.file_paths:
        print(f"Processando file: {file_path}")
        sections = collect_raw_data(file_path)
        all_sections.extend(sections)

    # Esporta i dati grezzi in CSV
    export_raw_data_to_csv(all_sections, args.output)
    print(f"Dati esportati in {args.output}")

# Esegue il programma
if __name__ == "__main__":
    main()
