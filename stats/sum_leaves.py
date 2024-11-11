import argparse
from collections import defaultdict

def sum_leaf_counts(file_paths, output_file):
    # Dictionario per accumulare i conteggi delle foglie
    total_counts = defaultdict(int)
    
    # Legge ogni file e aggiorna il totale
    for file_path in file_paths:
        with open(file_path, 'r') as file:
            for line in file:
                try:
                    leaf_count, tree_count = map(int, line.split(":"))
                    total_counts[leaf_count] += tree_count
                except ValueError:
                    print(f"Errore nel parsing della linea '{line.strip()}' nel file {file_path}")
    
    # Scrive i risultati nel file di output
    with open(output_file, 'w') as output:
        for leaf_count in sorted(total_counts.keys()):
            output.write(f"{leaf_count}: {total_counts[leaf_count]}\n")

# Esempio di esecuzione
if __name__ == "__main__":
    # Imposta argparse per leggere i file dal terminale
    parser = argparse.ArgumentParser(description="Sommatoria dei conteggi di foglie su più file.")
    parser.add_argument('file_paths', nargs='+', help="Percorsi dei file da sommare")
    parser.add_argument('--output', type=str, default="sommatoria_finale.txt", help="Nome del file di output")
    args = parser.parse_args()
    
    # Esegue la funzione per sommare i file
    sum_leaf_counts(args.file_paths, args.output)
    print(f"Sommatoria salvata in '{args.output}'")
