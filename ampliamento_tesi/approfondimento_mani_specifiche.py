
"""
Codice per generare file del tipo:

java -cp ./TesiDomino Manual 'mano_fissa' 'mano_variabile'

es:

java -cp ./TesiDomino-main Manual '0|0 0|2 0|3 0|4 0|5 0|6' '1|5 1|6 2|2 2|4 3|5 4|5 6|6'
java -cp ./TesiDomino-main Manual '0|0 0|2 0|3 0|4 0|5 0|6' '2|3 2|6 2|2 2|4 3|5 4|5 6|6'
java -cp ./TesiDomino-main Manual '0|0 0|2 0|3 0|4 0|5 0|6' '4|6 1|5 2|2 2|4 3|5 4|5 6|6'
"""

from itertools import combinations

# Set fisso per il giocatore 1
player1_hand = ["0|0", "0|1", "0|2", "0|3", "0|4", "0|5", "0|6"]

# Genera tutte le tessere del domino
all_tiles = [f"{i}|{j}" for i in range(7) for j in range(i, 7)]

# Rimuove le tessere del giocatore 1 dal set totale per il giocatore 2
remaining_tiles = [tile for tile in all_tiles if tile not in player1_hand]

# Apre il file in modalità scrittura
with open("domino_matches.txt", "w") as file:
    # Genera tutte le combinazioni possibili di 7 tessere per il giocatore 2
    for player2_hand in combinations(remaining_tiles, 7):
        # Converte la mano del giocatore 2 in formato stringa
        player2_hand_str = " ".join(player2_hand)
        # Crea la riga nel formato specificato e la scrive nel file
        file.write(f"java -cp ./TesiDomino Manual '{' '.join(player1_hand)}' '{player2_hand_str}'\n")

print("File 'domino_matches.txt' generato con successo.")
