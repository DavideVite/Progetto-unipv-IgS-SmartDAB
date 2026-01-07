# Guida Operativa allo Sviluppo (CONTRIBUTING)

Benvenuti nel repository.Per garantire ordine nel codice, questo team adotta il **GitHub Flow**
Queste norme sono vincolanti per tutti i collaboratori.

## 1. Il Main è "Blindato" (branch protection)
Il branch `main`  contiene solo codice pronto per la produzione. 

* **Divieto di Push Diretto:** Non è possibile fare `git push origin main`. Se ci provi, il server rifiuterà l'operazione. ( 
* **Solo Pull Request:** Ogni modifica deve arrivare sul main tramite una Pull Request (PR).
* **Code Review:** È richiesta l'approvazione di almeno **1 reviewer** per poter unire il codice

Le opzioni attive in settings del repository sono: 
- Require a pull request before merging with approvals
- Dismiss stale pull request approvals when new commits are pushed
- Do not allow bypassing the above settings

## 2. Strategia dei Branch
Non lavoriamo con branch personali (es. `mario/dev`), ma con **Feature Branch** legati ai task.

### Naming Convention
I branch devono seguire rigorosamente questo formato: `<tipo>/<descrizione-breve>`

| Tipo | Quando usarlo | Esempio |
| :--- | :--- | :--- |
| `feature/` | Nuove funzionalità | `feature/login-oauth` |
| `bugfix/` | Correzione di errori | `bugfix/header-mobile` |
| `refactor/` | Pulizia codice (senza cambio logica) | `refactor/db-queries` |

**Regola:** Un branch deve risolvere un solo problema e avere vita breve (giorni, non settimane)

## 3. Workflow Quotidiano
Segui questa procedura per evitare problemi di integrazione

1.  **Inizio Lavoro:** Parti sempre dal main aggiornato.
    ```bash
    git switch main
    git pull origin main
    git switch -c feature/nuova-funzionalita
    ```
2.  **Durante lo Sviluppo:** Fai commit frequenti.
3.  **Allineamento:** Se il task dura più di un giorno, scarica gli aggiornamenti degli altri ogni mattina sul tuo branch per evitare conflitti giganti alla fine:
    ```bash
    git pull origin main
    ```
Si consiglia l'uso di prima fetch e poi merge.    *(Risolvi subito eventuali conflitti se compaiono)*
Si permette l'uso del rebase solo in casi di massima siurezza sull'azione. 
    
    

### Stile dei Commit
Usa i **Conventional Commits** per chiarezza
* `feat: aggiunta validazione email`
* `fix: corretto errore 404 su pagina profilo`
* `docs: aggiornato README`

## 4. Pull Request e Merge
Quando il lavoro è pronto:
1.  Apri una PR verso `main`.
2.  Compila la descrizione spiegando **il perché** della modifica.
3.  Mantieni la PR piccola (max 200-400 righe) per facilitare la revisione. 

**Strategia di Merge:**
Utilizziamo **Squash and Merge**.
Tutti i tuoi commit intermedi ("wip", "fix typo") verranno schiacciati in un unico commit pulito sul main. Questo mantiene la storia del progetto leggibile. 


## 5. Uso di .gitignore 
Il file di testo .gitignore serve come da filtro per quei files che non si volgiono caricare sul repository remoto, ma si vogliono mantere solo locali. Ad esempio, il file .project prodotto da Eclipse è presente localmente ma non su GitHub. Il repository remoto rimane "puro" e contiene solo codice sorgente. 

**vantaggi**
1. Ognuno può mantenere file diversi in base ai diversi IDE che usa. 
2. Ognuno può tenere altri files rispetto a strettamente necessari 

Si consiglia di scrivere nel .gitignore : 
```
.project
.classpath
.settings/
bin/
```

**Attenzione**
il .gitignore ignora solo i file che non sono stati ancora tracciati. 
