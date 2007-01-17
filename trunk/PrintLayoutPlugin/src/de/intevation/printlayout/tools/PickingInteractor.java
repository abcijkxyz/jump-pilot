/*
 * PickingInteractor.java
 * ------------------
 * (c) 2007 by Intevation GmbH
 *
 * @author Sascha L. Teichmann (teichmann@intevation.de)
 * @author Ludwig Reiter       (ludwig@intevation.de)
 *
 * This program is free software under the LGPL (>=v2.1)
 * Read the file LICENSE.txt coming with the sources for details.
 */
package de.intevation.printlayout.tools;

import java.awt.Graphics;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.NoninvertibleTransformException;

import org.apache.batik.swing.gvt.InteractorAdapter;
import org.apache.batik.swing.gvt.Overlay;

import org.apache.batik.dom.AbstractElement;
import org.apache.batik.dom.AbstractNode;

import de.intevation.printlayout.MatrixTools;
import de.intevation.printlayout.DocumentManager;

import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGDocument;

import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashSet;

public class PickingInteractor
extends      InteractorAdapter
implements   Overlay, Tool
{
	public static final String IDENTIFIER = "picking-tool";

	protected boolean inUse;
	protected boolean finished;
	
	protected DocumentManager documentManager;

	public PickingInteractor() {
	}

	public PickingInteractor(DocumentManager documentManager) {
		setDocumentManager(documentManager);
	}
	
	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	public DocumentManager getDocumentManager() {
		return documentManager;
	}

	public String getToolIdentifier() {
		return IDENTIFIER;
	}

	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}

	public boolean getInUse() {
		return inUse;
	}

	public boolean startInteraction(InputEvent ie) {
		finished = false;
		//return inUse;
		int mods = ie.getModifiers();
		return
			inUse
			 // && ie.getID() == MouseEvent.MOUSE_PRESSED 
			 && (mods & InputEvent.BUTTON1_MASK) != 0; 
		/*
		*/
	}
	
	public boolean endInteraction() {
		return finished;
	}
	
	public void mouseClicked(MouseEvent me) {

		finished = true;
		
		int x = me.getX();
		int y = me.getY();

		SVGDocument document = documentManager.getSVGDocument();

		SVGSVGElement element =
			(SVGSVGElement)document.getElementById(DocumentManager.DOCUMENT_SHEET);
		
		AffineTransform xform;
		try {
			xform	= getScreenXForm(documentManager.getSVGDocument()).createInverse();
		}
		catch(NoninvertibleTransformException nte) {
			nte.printStackTrace();
			return;
		}

		Point2D screenPoint   = new Point2D.Double(x, y);
		Point2D documentPoint = new Point2D.Double();

		xform.transform(screenPoint, documentPoint);

		SVGRect query = element.createSVGRect();
		query.setX((float)documentPoint.getX());
		query.setY((float)documentPoint.getY());

		query.setWidth(0.5f); // half mm
		query.setHeight(0.5f);

		NodeList result = element.getIntersectionList(query, null);

		int N = result.getLength();

		HashSet   alreadyFound = new HashSet();
		ArrayList ordered      = new ArrayList();

		for (int i = 0; i < N; ++i) {
			AbstractElement obj = (AbstractElement)result.item(i);
			AbstractElement last = null;
			do {
				AbstractElement parent = (AbstractElement)obj.getParentNode();
				if (parent == null)
					break;
				String id = parent.getAttributeNS(null, "id");
				if (id != null && id.startsWith(DocumentManager.OBJECT_ID)) {
					last = parent;
				}
				obj = parent;
			}
			while (obj != null && obj != element);

			if (last != null && alreadyFound.add(last.getAttributeNS(null, "id")))
				ordered.add(last);
		}

		N = ordered.size();

		if (N > 0) {
			System.out.println("found items:");
			for (int i = 0; i < N; ++i) {
				AbstractElement obj = (AbstractElement)ordered.get(i);
				System.out.println("\t'" +  obj.getAttributeNS(null, "id")+ "'");
			}
		}

		//System.err.println(screenPoint + " -> " + documentPoint);
	}
  
	protected static AffineTransform getScreenXForm(SVGDocument document) {
		SVGSVGElement element = 
			(SVGSVGElement)document.getElementById(DocumentManager.DOCUMENT_SHEET);

		SVGMatrix matrix = element.getScreenCTM();
	
		return MatrixTools.toJavaTransform(matrix);
	}

	public void paint(Graphics g) {
		if (!inUse)
			return;
	}
}
// end of file
