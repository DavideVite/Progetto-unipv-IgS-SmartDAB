package it.unipv.posfw.smartdab.ui;
import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


import it.unipv.posfw.smartdab.core.domain.model.casa.Hub;
import it.unipv.posfw.smartdab.core.service.PersistenzaManager;
import it.unipv.posfw.smartdab.ui.controller.AutenticazioneController;
import it.unipv.posfw.smartdab.ui.controller.MainController;
import it.unipv.posfw.smartdab.ui.view.autenticazione.ConfigurazionePinPanel;
import it.unipv.posfw.smartdab.ui.view.autenticazione.LoginProduttorePanel;

public class SmartDABApp {
   public static void main(String[] args) {
    	  
    	// prova per eliminare il pin e far partire tutto dall'inserimento password
    	   /* try {
    	        java.util.prefs.Preferences.userNodeForPackage(PersistenzaManager.class).clear();
    	        System.out.println("PIN eliminato");
    	    } catch (Exception e) {
    	        e.printStackTrace();
    	    } */
    	   
    	   SwingUtilities.invokeLater(() -> {
        	
            //prepariamo l'hub all'avvio, passiamo la password del produttore per sbloccare la creazione dei gestori
            Hub.getInstance("prod123");
            System.out.println("Sistema inizializzato");
        	   
        	String pinSalvato = PersistenzaManager.caricaPin();

       	//finestra di login
       	JFrame autFrame = new JFrame("Accesso");
           CardLayout cl = new CardLayout();
           JPanel container = new JPanel(cl);
           AutenticazioneController autController = new AutenticazioneController(container, cl);
           //cosa fare dopo che il PIN è stato inserito correttamente
           autController.setOnSuccess(() -> {
               autFrame.dispose(); // chiude la finestrella di login
               avviaApplicazionePrincipale(); // apre il MainFrame con la lista di stanze
           });
           //aggiunge i pannelli assegnando un nome
           container.add(new LoginProduttorePanel(autController), "LOGIN");
           container.add(new ConfigurazionePinPanel(autController), "PIN_SETUP");
           
           if (pinSalvato == null) {
        	   //primo avvio: non c'è pin, mostriamo password produttore
        	   cl.show(container, "LOGIN");
        	   System.out.println("Primo avvio: richiesta password produttore.");
           } else {
               // AVVII SUCCESSIVI: Il PIN esiste, saltiamo la password e andiamo al PIN
               cl.show(container, "PIN_SETUP");
               System.out.println("PIN trovato: salto alla schermata di sblocco.");	   
           }
           
           //assemblaggio del frame
           autFrame.add(container);
           autFrame.pack();
           autFrame.setSize(400, 300);
           autFrame.setLocationRelativeTo(null);
           autFrame.setVisible(true);
       });
   }

   private static void avviaApplicazionePrincipale() {
       // MainController gestisce tutto: model, view e controller
       MainController mainController = new MainController();
       mainController.mostraApplicazione();
   }
}
