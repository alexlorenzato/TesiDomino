## Tmp

**Preliminare**

Estrae 4 righe dalla lista di comandi
```
head -n 4 lines 
```

**Script 0**

Divide file `lines` in sotto-file da 2 righe con formato `lineXY`

```
#!/bin/bash

split -l 2 lines line

count=1

for file in line*; do
    mv "$file" "line$count"
    ((count++))
done
```

**Script 1**

Crea file eseguibili da lanciare poi con `qsub`:
- cat crea file con nome file_name
- 

```
#!/bin/bash

for i in {1..2}; do
    file_name="runn$i.sh"
    
    cat << EOF > $file_name
#!/bin/bash
cd /work/lorenzato
./line$i > line$i.log
EOF

    chmod +x $file_name
done
```

**Script 2**

```

```



## Login

ssh lorenzato@turing.disi.unibo.it

password: d0min099

`cd /work/lorenzato`   

## Procedimento

1. Dividere il file ``allsets7.double.run`` con `split`.
    - `split -l 100 allsets7.double.run run_` per avere file da 100 righe ciascuno a partire dal file `allsets7.double.run`, nominati come run1, run2, etc

2. Creare script che permetta di creare una serie di file eseguibili così strutturati:
    ```
    #!/bin/bash
    cd /work/lorenzato/TesiDomino-main
    ../run1 > run1.log
    ```

    I file eseguibili (quelli con .sh) serviranno per essere lanciati con qsub, prenderanno i comandi da runXY e scriveranno i risultati in runXY.log.

    Lo script per creare gli script quindi è:
    ```
    #!/bin/bash

    output_dir="/work/lorenzato/TesiDomino-main"

    for i in {1..3}; do
        file_name="run_$i.sh"
        
        cat << 'EOF' > $file_name
    #!/bin/bash
    cd /work/lorenzato/TesiDomino-main
    ../run_$i > run_$i.log
    EOF

        chmod +x $file_name
    done
    ```

3. Creare script che permetta di lanciare gli altri script.

    Lo script è:
    ```
    for i in {1..3}; do
    qsub -N prova9_part_$i -- ./prova9_part_$i.sh
    done
    ```

    Dopo aver scritto e salvato questo script, renderlo eseguibile con `chmod +x nome_file.sh`

4. Raccogliere output degli script dai file Y.





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



