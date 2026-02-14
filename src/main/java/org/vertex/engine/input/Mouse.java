package org.vertex.engine.input;

import org.vertex.engine.render.RenderContext;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mouse extends MouseAdapter {
	private int x, y;
	private boolean isHeld;
	private boolean prevHeld;

	private final RenderContext render;

	public Mouse(RenderContext render) {
		this.render = render;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isHeld() {
		return isHeld;
	}

	public void setHeld(boolean isPressed) {
		this.isHeld = isPressed;
	}

	public void update() {
		prevHeld = isHeld;
	}

	public boolean wasPressed() {
		return isHeld && !prevHeld;
	}

	public boolean wasReleased() {
		return !isHeld && prevHeld;
	}

	public Point getMousePosition() {
		return new Point(getX(), getY());
	}

	@Override
	public void mousePressed(MouseEvent e) {
		isHeld = true;
		double scale = render.getScale();
		int rawX = e.getX();
		int rawY = e.getY();
		x = (int)((rawX - render.getOffsetX()) / scale);
		y = (int)((rawY - render.getOffsetY()) / scale);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isHeld = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		double scale = render.getScale();
		int rawX = e.getX();
		int rawY = e.getY();
		x = (int)((rawX - render.getOffsetX()) / scale);
		y = (int)((rawY - render.getOffsetY()) / scale);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		double scale = render.getScale();
		int rawX = e.getX();
		int rawY = e.getY();
		x = (int)((rawX - render.getOffsetX()) / scale);
		y = (int)((rawY - render.getOffsetY()) / scale);
	}
}
