/*
 * $Id$
 *
 * (C) Copyright Numdata BV 2005-2005 - All Rights Reserved
 *
 * This software may not be used, copied, modified, or distributed in any
 * form without express permission from Numdata BV. Please contact Numdata BV
 * for license information.
 */
package ab.j3d.view;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.List;

import ab.j3d.Vector3D;
import ab.j3d.model.Face3D;
import ab.j3d.model.IntersectionSupport;
import ab.j3d.model.Node3DCollection;

/**
 * The SceneInputTranslator listens to events on a component and dispatches
 * ControlEvents to the {@link ControlEventQueue}. At this moment, the
 * translator only listens to mouse pressed and mouse released events, but this
 * could be extended in the future.
 *
 * @author  Mart Slot
 * @version $Revision$ $Date$
 */
public abstract class SceneInputTranslator
	implements MouseListener, MouseMotionListener
{
	/**
	 * The component to listen to for events.
	 */
	private Component _component;

	/**
	 * The number of the current series of events. A mouse click, drag and
	 * release all have the same number, after the release, the number is
	 * increased.
	 */
	private int _eventNumber = 0;

	/**
	 * Wether or not the mouse has been dragged since the last mousepress event
	 */
	private boolean _mouseDragged = false;

	/**
	 * The ControlEventQueue that dispatches the ControlEvents to registered
	 * listeners.
	 */
	private ControlEventQueue _eventQueue;

	/**
	 * Construct new EventHandler.
	 * @param component
	 */
	public SceneInputTranslator( final Component component )
	{
		_component = component;
		_component.addMouseListener( this );
		_component.addMouseMotionListener( this );

		_eventQueue = new ControlEventQueue();
	}

	/**
	 * Returns the ControlEventQueue that dispatches the ControlEvents to registered
	 * listeners.
	 * @return This translators ControlEventQueue.
	 */
	public ControlEventQueue getEventQueue()
	{
		return _eventQueue;
	}

	/**
	 * Returns a collection of Object3Ds that should be checked for intersection
	 * The objects are assumed to be translated to viewing coordinates.
	 *
	 * @return The collection of Object3Ds that should be checked for intersection
	 * @see #getFacesAt
	 */
	protected abstract Node3DCollection getScene();

	/**
	 * Returns the projector for this scene. The projector is used to convert
	 * the mouse click location to world coordinates.
	 * @return  The projector for this scene.
	 * @see     #getFacesAt
	 */
	protected abstract Projector getProjector();

	/**
	 * Called from {@link SceneInputTranslator#getFacesAt}. This method can walk
	 * through the objects in the <code>scene</code> to throw away any object
	 * that does  not need to be tested for intersection.
	 * Though implemented in this class, this method simply returns the scene
	 * that is given to it. Classes that extends this class may override it to
	 * increase efficiency.
	 * @param scene     The Object3Ds in the scene
	 * @param projector The projector for this scene
	 * @param clickX    The x position of the mouse click
	 * @param clickY    The y position of the mouse click
	 * @return  A {@link Node3DCollection} with the faces that should be tested
	 *          for intersection.
	 * @see     #getFacesAt
	 */
	protected Node3DCollection siftRelevantObjects( final Node3DCollection scene, final Projector projector, final int clickX, final int clickY )
	{
		return scene;
	}

	/**
	 * Returns the {@link Face3D}s in the world, below the point clickX, clickY
	 * on the viewing plane. This method calls <code>getScene</code> to get the
	 * objects in the scene, <code>getProjector</code> to get the projector for
	 * the scene, and <code>siftRelevantFaces</code> before checking
	 * intersection. Subclasses do not need to override this method, overriding
	 * the three other methods should suffice.
	 * @param clickX    The x position of the mouse click
	 * @param clickY    The y position of the mouse click
	 * @return  The {@link Face3D}s that lie below the point clickX, clickY.
	 * @see     #getScene
	 * @see     #getProjector
	 * @see     #siftRelevantObjects
	 */
	protected List getFacesAt( final int clickX, final int clickY )
	{
		final Node3DCollection scene = getScene();
		final Projector projector = getProjector();

		siftRelevantObjects(scene, projector, clickX, clickY);

		final Vector3D lineStart = projector.screenToWorld(clickX, clickY, 0);
		final Vector3D lineEnd = projector.screenToWorld( clickX, clickY, -1000000);

		final IntersectionSupport support = new IntersectionSupport( );

		return support.getIntersectingFaces( scene, lineStart, lineEnd );
	}

	/**
	 * Invoked when a mouse button has been pressed on a component.
	 */
	public void mousePressed( final MouseEvent e ) {
		final int type = MouseControlEvent.MOUSE_PRESSED;
		final int x = e.getX();
		final int y = e.getY();
		final int button = e.getButton();
		final int modifiers = e.getModifiersEx();
		final List nodesClicked = getFacesAt(x, y);

		final MouseControlEvent event = new MouseControlEvent(_eventNumber, type, modifiers, x, y, button, _mouseDragged, nodesClicked );

		_eventQueue.dispatchEvent( event );
	}

	/**
	 * Invoked when a mouse button has been released on a component.
	 */
	public void mouseReleased( final MouseEvent e )
	{
		final int type = MouseControlEvent.MOUSE_RELEASED;
		final int x = e.getX();
		final int y = e.getY();
		final int button = e.getButton();
		final int modifiers = e.getModifiersEx();
		final List nodesClicked = new LinkedList();

		final MouseControlEvent event = new MouseControlEvent(_eventNumber, type, modifiers, x, y, button, _mouseDragged, nodesClicked );

		_eventQueue.dispatchEvent( event );

		_eventNumber++;
		_mouseDragged = false;
	}

	/**
	 * Invoked when the mouse button has been clicked (pressed and released) on a
	 * component.
	 */
	public void mouseClicked( final MouseEvent e ) { }

	/**
	 * Invoked when the mouse enters a component.
	 */
	public void mouseEntered( final MouseEvent e ) { }

	/**
	 * Invoked when the mouse exits a component.
	 */
	public void mouseExited( final MouseEvent e ) { }

	/**
	 * Invoked when a mouse button is pressed on a component and then dragged.
	 * <code>MOUSE_DRAGGED</code> events will continue to be delivered to the
	 * component where the drag originated until the mouse button is released
	 * (regardless of whether the mouse position is within the bounds of the
	 * component).
	 * <p/>
	 * Due to platform-dependent Drag&Drop implementations,
	 * <code>MOUSE_DRAGGED</code> events may not be delivered during a native
	 * Drag&Drop operation.
	 */
	public void mouseDragged( final MouseEvent e ) { }

	/**
	 * Invoked when the mouse cursor has been moved onto a component but no buttons
	 * have been pushed.
	 */
	public void mouseMoved( final MouseEvent e ) { }

}