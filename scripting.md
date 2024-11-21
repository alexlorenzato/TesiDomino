
# Accesso rapido


## Comandi esecuzione
python3 reorder.py cmd1.log cmd2.log cmd3.log cmd4.log cmd5.log cmd6.log cmd7.log cmd8.log cmd9.log cmd10.log cmd11.log cmd12.log cmd13.log cmd14.log cmd15.log cmd16.log cmd17.log cmd18.log cmd19.log cmd20.log cmd21.log cmd22.log cmd23.log cmd24.log cmd25.log cmd26.log cmd27.log cmd28.log cmd29.log cmd30.log cmd31.log cmd32.log cmd33.log cmd34.log cmd35.log cmd36.log cmd37.log cmd38.log cmd39.log cmd40.log cmd41.log


## Login

ssh lorenzato@turing.disi.unibo.it 
password: d0min099 
`cd /work/lorenzato` 


## Scaricare file

scp lorenzato@turing.disi.unibo.it:/work/lorenzato/mano3.log .

## Github pull via terminale

`git pull origin main`, lanciarlo dalla cartella TesiDomino
git clone git@github.com:alexlorenzato/TesiDomino.git, serve per il primo pull, altrimenti usare il comando sopra


## Esecuzione set_stats.py

`python3 set_stats.py --hand "0|1 0|3 0|6 1|3 1|4 1|5 2|2" cmd1.log`, questo comando restituisce: `java -cp ./TesiDomino-main Manual '0|0 0|1 0|2 0|3 0|5 0|6 1|3' '2|3 2|4 2|6 3|4 5|5 5|6 6|6'`




## Bug di righe inaccessibili

awk 'NR==973960' cmd1.log


# Scripting

## Procedimento

**Script 00**

Estrarre righe e aggiungere `java -cp ./TesiDomino-main Manual` ad ogni riga.

```
#!/bin/bash

# Estrarre le prime 4 righe da allsets7.double.run e salvarle nel file commands
head -n 4 allsets7.double.run > commands

# Leggi ogni riga di commands, sostituisci la stringa e riscrivi nel file
while read -r line; do
    # Sostituisci la parola "java" con "java -cp ./TesiDomino-main"
    modified_line=$(echo "$line" | sed 's/java /java -cp .\/TesiDomino-main /')
    
    # Scrivi la riga modificata su un nuovo file, oppure puoi sovrascrivere commands
    echo "$modified_line" >> commands_modified
done < commands

# Sovrascrivi il file originale 'commands' con quello modificato
mv commands_modified commands

```

**Script 0**

Divide file `commands` in sotto-file da 2 righe con formato `cmdX`

```
#!/bin/bash

split -l 2 commands cmd

count=1

for file in cmd*; do
    mv "$file" "cmd$count"
    ((count++))
done
```

**Script 1**

Crea file eseguibili `runnX.sh` da lanciare poi con `qsub`:
- cat crea file con nome `runnX.sh`
- questi file eseguiranno `cmdX` e scriveranno il log su `cmdX.log`

```
#!/bin/bash

for i in {1..2}; do
    file_name="runn$i.sh"
    
    cat << EOF > $file_name
#!/bin/bash
cd /work/lorenzato
./cmd$i > cmd$i.log
EOF

    chmod +x $file_name
done
```

**Script 2**

Lancia i file eseguibili `runnX.sh` creati dallo script 1.

```
#!/bin/bash

for i in {1..2}; do
    qsub runn$i.sh
done
```



  



## Comandi
> Screen

`run` lancia il set di istruzioni double

`screen`  apre uno screen da cui lancio run2, esco dallo screeen con CTRL+A+B

`screen -x`  mi ricollego allo screen, se ne creo di più specifico il nome dopo la x

nel caso uso screen, imparare i comandi base; se spezzetto in 50, devo loggare su 50 screen

<br>

> Qsub

`qsub nomescript` se anche sloggo lo script gira lo stesso

`qsub -o /dev/null -e /dev/null run2` importante per non trovare un casino di file nella home

`qstat` per vedere stato dello script

`for i in {1..50}; do qsub -o /dev/null -e /dev/null run$i; done`



`split` per spezzettare un file ma poi bisogna ricordarsi, si può spezzettare secondo diversi parameteri (man split per sapere di più)

`split -l 1000000` crea file da 1000000 di righe
c'é anche un modo di dare un nome specifico ai file che vengono creati

`run` lancia la roba che c'era nel file splittato e scrive l'output in un altro file

una volta splittato il file bisogna creare i file run1, run2, run3 etc posso decidere se mandarli in esecuzione con screen o qsub

qsub deve sapere in che directory sposstarsi perché non sa dove è Manual




