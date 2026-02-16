package org.vertex.engine.gui;

import org.vertex.engine.service.BooleanService;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serial;

public class GameFrame extends JFrame {
	@Serial
    private static final long serialVersionUID = -3130387824420425271L;
	private final static String TITLE = "Vertex v0.8";
	private final BoardPanel panel;
	private final GraphicsDevice gd;
	private Rectangle windowedBounds;
	private final Cursor blank;

    public GameFrame() {
		super(TITLE);
		blank = Toolkit.getDefaultToolkit().createCustomCursor(
				new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB),
				new Point(0, 0), "blank");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		panel = new BoardPanel(this);
		add(panel);
		pack();
		gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();
		setLocationRelativeTo(null);
		setVisible(true);
		toggleFullscreen();
		setCursor(blank);
		JLayeredPane layered = getLayeredPane();
		JPanel overlay = new JPanel();
		overlay.setOpaque(false);
		overlay.setBounds(0, 0, getWidth(), getHeight());
		overlay.setCursor(blank);
		layered.add(overlay, JLayeredPane.DRAG_LAYER);
		panel.requestFocusInWindow();
		panel.launch();
	}

	public void toggleFullscreen() {
		boolean wasFocused = panel.isFocusOwner();
		dispose();
		if (BooleanService.isFullscreen) {
			windowedBounds = getBounds();
			setUndecorated(true);
			setResizable(false);
			setExtendedState(JFrame.MAXIMIZED_BOTH);
		} else {
			setUndecorated(false);
			setResizable(true);
			setExtendedState(JFrame.NORMAL);
			if (windowedBounds != null) {
				setBounds(windowedBounds);
			}
		}
		BooleanService.isFullscreen = !BooleanService.isFullscreen;
		setVisible(true);
		setCursor(blank);
		if(wasFocused) { panel.requestFocus(); }
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(GameFrame::new);
	}
}
