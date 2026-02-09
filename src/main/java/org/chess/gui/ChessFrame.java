package org.chess.gui;

import java.awt.HeadlessException;
import java.io.Serial;

import javax.swing.JFrame;

public class ChessFrame extends JFrame {
	@Serial
    private static final long serialVersionUID = -3130387824420425271L;
	private final static String TITLE = "Chess";

    public ChessFrame() throws HeadlessException {
		super(TITLE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
        BoardPanel panel = new BoardPanel();
		add(panel);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		panel.launch();
	}
}
