import re
import argparse

def parse_hand(hand_str):
    return [tuple(map(int, domino.split('|'))) for domino in hand_str.split()]

def sort_dominoes(hand):
    return sorted(hand, key=lambda x: (min(x), max(x)))

def process_line(line):
    sections = re.split(r'\s{2,}', line.strip())  
    if len(sections[-1].split()) > 1:
        sections.extend(sections.pop().split())

    if len(sections) < 5:
        print(f"Errore: la riga non contiene abbastanza sezioni. Ne contiene: ", len(sections))
        return None
    
    player1_hand = parse_hand(sections[0])
    player2_hand = parse_hand(sections[1])
    
    sorted_player1_hand = sort_dominoes(player1_hand)
    sorted_player2_hand = sort_dominoes(player2_hand)


    sorted_line = (
        ' '.join([f"{a}|{b}" for a, b in sorted_player1_hand]) + ' ' * 5 +  
        ' '.join([f"{a}|{b}" for a, b in sorted_player2_hand]) + ' ' * 5 +   
        sections[2] + ' ' * 5 +                                             
        sections[3] + ' ' * 5 +                                             
        sections[4]                                                         
    )

    return sorted_line

def process_file(file_path):
    print(f"Processing file: {file_path}")
    
    with open(file_path, 'r') as file:
        lines = file.readlines()  

    modified_lines = []
    for line in lines:
        sorted_line = process_line(line)
        if sorted_line:
            modified_lines.append(sorted_line + '\n')  
    
    with open(file_path, 'w') as file:
        file.writelines(modified_lines)  


def main():
    parser = argparse.ArgumentParser(description="Processa più file di domino.")
    parser.add_argument('file_paths', nargs='+', help="I percorsi dei file di dati domino.")  
    args = parser.parse_args()

    for file_path in args.file_paths:
        process_file(file_path)


if __name__ == "__main__":
    main()
