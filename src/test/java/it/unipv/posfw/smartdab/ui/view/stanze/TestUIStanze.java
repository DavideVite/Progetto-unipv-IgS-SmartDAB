package it.unipv.posfw.smartdab.ui.view.stanze;
import javax.swing.*;
import java.awt.*;

import it.unipv.posfw.smartdab.ui.controller.StanzeController;

	public class TestUIStanze {


		public static void main(String[] args) {
	        SwingUtilities.invokeLater(() -> {
	            JFrame frame = new JFrame("Test Interfaccia Stanze");
	            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	            frame.setSize(800, 600);

	            CardLayout layout = new CardLayout();
	            JPanel container = new JPanel(layout);

	            // Il Controller (senza servizi per il test)
	            StanzeController controller = new StanzeController(container, layout, null);

	            StanzePanel elenco = new StanzePanel(controller);
	            StanzeFormPanel form = new StanzeFormPanel(controller);

	            controller.setViews(elenco, form);


	            container.add(elenco, "LISTA_STANZE");
	            container.add(form, "FORM_STANZA");

	            elenco.aggiungiRigaTabella("1", "Soggiorno", 25.5);
	            elenco.aggiungiRigaTabella("2", "Camera", 18.0);

	            frame.add(container);
	            frame.setLocationRelativeTo(null);
	            frame.setVisible(true);
	        });
	    }
	}
