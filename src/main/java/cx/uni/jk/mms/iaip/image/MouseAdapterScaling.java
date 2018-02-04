/**
 *    Copyright (C) 2015  Peter Plaimer <dct-tool@tk.jku.at>
 *
 *    This file is part of the program
 *    InterActive Image Processing / Discrete Cosine Transformation (DCT) 
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cx.uni.jk.mms.iaip.image;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A Proxy for the methods of {@link MouseAdapter}, which scales
 * {@link MouseEvent} in-component coordinates by factor "scale".
 * 
 * Instead of your normal MouseAdapter implementation, register this proxy with
 * your swing component. Register your normal implementation with this proxy.
 */
public class MouseAdapterScaling extends MouseAdapter {

	private List<MouseAdapter> adapters = new CopyOnWriteArrayList<>();
	private Point lastPoint = null;
	private double scale = 1.0f;

	public MouseAdapterScaling() {
		this(1.0f);
	}

	public MouseAdapterScaling(double scale) {
		super();
		this.scale = scale;
	}

	public double getScale() {
		return this.scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public boolean addMouseAdapter(MouseAdapter a) {
		return this.adapters.contains(a) || this.adapters.add(a);
	}

	public boolean removeMouseAdapter(MouseAdapter a) {
		return this.adapters.contains(a) && this.adapters.remove(a);
	}

	private Point scale(Point p) {
		return new Point((int) (p.getX() / this.scale), (int) (p.getY() / this.scale));
	}

	/**
	 * apply scaling to the mouse event coordinates and save these scaled
	 * coordinates for comparison in future events.
	 * 
	 * @param e
	 * @return true if the scaled coordinates are different to the last scaled
	 *         coordinates.
	 */
	private boolean scaleAndCompare(MouseEvent e) {
		Point p = this.scale(e.getPoint());
		/** use translate as a trick to manipulate the coordinates in place. */
		e.translatePoint(-e.getX() + (int) p.getX(), -e.getY() + (int) p.getY());

		if (!e.getPoint().equals(this.lastPoint)) {
			this.lastPoint = e.getPoint();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.scaleAndCompare(e);
		for (MouseAdapter a : this.adapters) {
			a.mouseClicked(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.scaleAndCompare(e);
		for (MouseAdapter a : this.adapters) {
			a.mousePressed(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.scaleAndCompare(e);
		for (MouseAdapter a : this.adapters) {
			a.mouseReleased(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.scaleAndCompare(e);
		for (MouseAdapter a : this.adapters) {
			a.mouseEntered(e);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		this.scaleAndCompare(e);
		for (MouseAdapter a : this.adapters) {
			a.mouseExited(e);
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		this.scaleAndCompare(e);
		for (MouseAdapter a : this.adapters) {
			a.mouseWheelMoved(e);
		}
	}

	/**
	 * forwards the event only if the scaled coordinates are different to the
	 * scaled coordinates of the previous event (of any type handled by
	 * {@link MouseAdapter}).
	 * 
	 * @param e
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if (this.scaleAndCompare(e)) {
			for (MouseAdapter a : this.adapters) {
				a.mouseDragged(e);
			}
		}
	}

	/**
	 * forwards the event only if the scaled coordinates are different to the
	 * scaled coordinates of the previous event (of any type handled by
	 * {@link MouseAdapter}).
	 * 
	 * @param e
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		if (this.scaleAndCompare(e)) {
			for (MouseAdapter a : this.adapters) {
				a.mouseMoved(e);
			}
		}
	}

}
