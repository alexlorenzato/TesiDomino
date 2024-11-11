import re
import argparse
import os

def parse_statistics(file_path):
    """Parsa le statistiche dal file specificato."""
    with open(file_path, 'r') as file:
        data = file.read()
    
    # RegEx per estrarre valori numerici
    match_duration = re.search(r'Durata media della partita: ([\d.]+)', data)
    match_leaves = re.search(r'Numero medio di foglie: ([\d.]+)', data)
    match_x_percent = re.search(r'Percentuale media di foglie con \'X\': ([\d.]+)%', data)
    match_max_leaves = re.search(r'Numero di foglie: (\d+)', data)
    match_player1_hand = re.search(r'Mano giocatore 1: (\[.*\])', data)
    match_player2_hand = re.search(r'Mano giocatore 2: (\[.*\])', data)

    if match_duration and match_leaves and match_x_percent:
        avg_duration = float(match_duration.group(1))
        avg_leaves = float(match_leaves.group(1))
        avg_x_percent = float(match_x_percent.group(1))
        max_leaves = int(match_max_leaves.group(1)) if match_max_leaves else 0
        player1_hand = eval(match_player1_hand.group(1)) if match_player1_hand else []
        player2_hand = eval(match_player2_hand.group(1)) if match_player2_hand else []

        return avg_duration, avg_leaves, avg_x_percent, max_leaves, player1_hand, player2_hand
    else:
        print(f"Errore di parsing in {file_path}")
        return None

def calculate_global_statistics(file_paths):
    """Calcola la sommatoria delle statistiche e trova la partita con il maggior numero di foglie."""
    total_duration = 0
    total_leaves = 0
    total_x_percent = 0
    total_files = 0
    max_leaves_global = 0
    player1_hand_max = None
    player2_hand_max = None

    for file_path in file_paths:
        stats = parse_statistics(file_path)
        if stats:
            avg_duration, avg_leaves, avg_x_percent, max_leaves, player1_hand, player2_hand = stats
            total_duration += avg_duration
            total_leaves += avg_leaves
            total_x_percent += avg_x_percent
            total_files += 1

            # Aggiornare il massimo delle foglie
            if max_leaves > max_leaves_global:
                max_leaves_global = max_leaves
                player1_hand_max = player1_hand
                player2_hand_max = player2_hand

    print("total_files:", total_files)
    print("total_duration:", total_duration)
    print("total_duration:", total_leaves)
    print("total_duration:", total_x_percent)

    # Calcolo delle medie globali
    global_avg_duration = total_duration / total_files if total_files > 0 else 0
    global_avg_leaves = total_leaves / total_files if total_files > 0 else 0
    global_avg_x_percent = total_x_percent / total_files if total_files > 0 else 0

    # Stampa dei risultati globali
    print("Statistiche Globali:")
    print(f"Durata media della partita: {global_avg_duration:.2f} ms")
    print(f"Numero medio di foglie: {global_avg_leaves:.2f}")
    print(f"Percentuale media di foglie con 'X': {global_avg_x_percent:.2f}%")

    print("\nPartita con il maggior numero di foglie globale:")
    if max_leaves_global > 0:
        print(f"Numero di foglie: {max_leaves_global}")
        print(f"Mano giocatore 1: {player1_hand_max}")
        print(f"Mano giocatore 2: {player2_hand_max}")
    else:
        print("Nessuna partita trovata con foglie registrate.")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Calcola le statistiche globali dai file di statistiche.")
    parser.add_argument('file_paths', type=str, nargs='+', help="Percorsi dei file di statistiche.")
    args = parser.parse_args()

    calculate_global_statistics(args.file_paths)
