import matplotlib.pyplot as plt

# Leggi il file e memorizza i dati
file_path = 'sommatoria_finale.txt'  # sostituisci con il percorso reale del file
x_values = []
y_values = []

with open(file_path, 'r') as file:
    for line in file:
        # Rimuovi eventuali spazi bianchi ai bordi e separa il numero a sinistra da quello a destra
        parts = line.strip().split(":")
        if len(parts) == 2:
            x_values.append(int(parts[0].strip()))  # numero a sinistra (asse x)
            y_values.append(int(parts[1].strip()))  # numero a destra (asse y)

# Crea il grafico
plt.figure(figsize=(10, 6))
plt.plot(x_values, y_values, marker='o', linestyle='-', color='b')
plt.xlim(1950, 35000)

# Aggiungi etichette e titolo
plt.xlabel("Numero foglie (asse x)")
plt.ylabel("Frequenza (asse y)")
plt.title("Distribuzione numero di foglie")

# Mostra il grafico
plt.show()
