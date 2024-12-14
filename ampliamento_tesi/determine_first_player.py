import re

# Classe che rappresenta i dati di una partita
class GameData:
    def __init__(self, player1_hand, player2_hand, minimax_scores, best_value, game_duration):
        self.player1_hand   = player1_hand    # Lista di tuple (dominoes)
        self.player2_hand   = player2_hand    # Lista di tuple (dominoes)
        self.minimax_scores = minimax_scores  # Lista di punteggi minimax
        self.best_value     = best_value      # Miglior punteggio possibile (value)
        self.game_duration  = game_duration   # Durata in millisecondi

# Funzione per parsare una stringa di domino in una lista di tuple (es. '1|2 3|4' diventa [(1,2), (3,4)])
def parse_hand(hand_str):
    try:
        return [tuple(map(int, domino.split('|'))) for domino in hand_str.split()]
    except ValueError as e:
        print(f"Errore nel parsing della mano: {hand_str} -- {e}")
        return []

# Funzione per parsare una singola riga del file di log e restituire un oggetto GameData
def parse_line(line):
    try:
        sections = re.split(r'\s{2,}', line.strip())
        if len(sections[-1].split()) > 1:
            sections.extend(sections.pop().split())

        if len(sections) < 5:
            return None  # Ritorna None se la riga non ha abbastanza dati
        
        # Parsiamo i dati della partita
        player1_hand   = parse_hand(sections[0])
        player2_hand   = parse_hand(sections[1])
        minimax_scores = sections[2]  # Non utilizzato per la verifica
        best_value     = int(sections[3])
        game_duration  = int(sections[4])

        return GameData(player1_hand, player2_hand, minimax_scores, best_value, game_duration)
    
    except (ValueError, IndexError) as e:
        # Se si verifica un errore durante il parsing della riga, la ignoriamo
        print(f"Errore nel parsing della riga: {line.strip()} -- {e}")
        return None

# Funzione per determinare il primo giocatore in base al doppio più alto
def determine_first_player(player1_hand, player2_hand, best_value):
    # Filtra le tessere doppie (es. 0|0, 1|1, ecc.)
    player1_doubles = [d for d in player1_hand if d[0] == d[1]]
    player2_doubles = [d for d in player2_hand if d[0] == d[1]]

    # Trova il doppio più alto per ciascun giocatore
    highest_double_player1 = max(player1_doubles, default=(-1, -1))
    highest_double_player2 = max(player2_doubles, default=(-1, -1))

    # Determina chi ha il doppio più alto
    if highest_double_player1 > highest_double_player2:
        if(best_value < 0):
            print("Errore", highest_double_player1, ", ", highest_double_player2, ", ", best_value)
        return 1  # Giocatore 1
    elif highest_double_player2 > highest_double_player1:
        if(best_value > 0):
            print("Errore", highest_double_player1, ", ", highest_double_player2, ", ", best_value)
        return 2  # Giocatore 2
    else:
        return 0  # Nessuno ha un doppio (raro, ma possibile)

# Funzione per verificare il file
def verify_first_player(file_path):
    with open(file_path, 'r') as file:
        line_number = 0
        matches = True

        for line in file:
            line_number += 1
            # Parse della riga per ottenere i dati della partita
            game_data = parse_line(line)
            if not game_data:
                print(f"Riga {line_number} ignorata, formato non valido.")
                continue

            # Determiniamo il primo giocatore
            first_player = determine_first_player(game_data.player1_hand, game_data.player2_hand, game_data.best_value)

            # Controlliamo se il primo giocatore è associato alla prima colonna
            if first_player == 1:
                # OK: il primo giocatore è il Giocatore 1
                continue
            elif first_player == 2:
                # Errore: il primo giocatore è il Giocatore 2
                print(f"Errore alla riga {line_number}: il primo giocatore è il Giocatore 2 ma è nella seconda colonna.")
                matches = False
            elif first_player == 0:
                # Nessuno ha un doppio, quindi è impossibile determinare
                print(f"Attenzione alla riga {line_number}: nessun doppio trovato, impossibile determinare il primo giocatore.")
                matches = False

        return matches

# Main per eseguire il programma
def main():
    import argparse
    parser = argparse.ArgumentParser(description="Verifica che il primo giocatore corrisponda alla prima colonna del file.")
    parser.add_argument('file_path', help="Il percorso del file di log di domino.")
    args = parser.parse_args()

    is_valid = verify_first_player(args.file_path)

    if is_valid:
        print("Il file rispetta sempre l'associazione della prima colonna al primo giocatore.")
    else:
        print("Ci sono errori nell'associazione della prima colonna al primo giocatore.")

if __name__ == "__main__":
    main()
