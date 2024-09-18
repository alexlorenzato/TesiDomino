## Login

ssh lorenzato@turing.disi.unibo.it
password: d0min099
cd /work/lorenzato   -> lavorare qui

## Scopo



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
qstat per vedere stato dello script

`for i in {1..50}; do qsub -o /dev/null -e /dev/null run$i; done`



`split` per spezzettare un file ma poi bisogna ricordarsi, si può spezzettare secondo diversi parameteri (man split per sapere di più)

`split -l 1000000` crea file da 1000000 di righe
c'é anche un modo di dare un nome specifico ai file che vengono creati

`run` lancia la roba che c'era nel file splittato e scrive l'output in un altro file

una volta splittato il file bisogna creare i file run1, run2, run3 etc posso decidere se mandarli in esecuzione con screen o qsub

qsub deve sapere in che directory sposstarsi perché non sa dove è Manual




