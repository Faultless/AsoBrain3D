/*
 * $Id$
 *
 * (C) Copyright Numdata BV 2006-2006 - All Rights Reserved
 *
 * This software may not be used, copied, modified, or distributed in any
 * form without express permission from Numdata BV. Please contact Numdata BV
 * for license information.
 */
package ab.j3d.control;

import java.awt.event.MouseEvent;
import java.util.EventObject;

import com.numdata.oss.event.EventDispatcher;

/**
 * This class provides low-level support for mouse-controlled operations.
 *
 * @author  Peter S. Heijnen
 * @version $Revision$ $Date$
 */
public class MouseControl
	implements Control
{
	/**
	 * Number of event sequence being captured. Set to <code>-1</code> if no
	 * capture is active.
	 */
	private int _capturedSequenceNumber = -1;

	/**
	 * This method only filters {@link ControlInputEvent}s and redirects specific
	 * {@link MouseEvent}-related events to the various member methods.
	 *
	 * @param   event   Event to be filtered.
	 *
	 * @return  Filtered event (may be same or modified);
	 *          <code>null</code> to discard event completely.
	 */
	public EventObject filterEvent( final EventObject event )
	{
		EventObject result = event;

		if ( event instanceof ControlInputEvent )
		{
			final ControlInputEvent controlInputEvent = (ControlInputEvent)event;

			final boolean captured = updateCaptureState( controlInputEvent );

			result = captured ? null : event;

			switch ( controlInputEvent.getID() )
			{
				case MouseEvent.MOUSE_PRESSED :
					if ( !captured )
						result = mousePressed( controlInputEvent );
					break;

				case MouseEvent.MOUSE_DRAGGED :
					if ( captured )
						mouseDragged( controlInputEvent );
					break;

				case MouseEvent.MOUSE_RELEASED :
					if ( captured && !controlInputEvent.isMouseButtonDown() )
					{
						mouseReleased( controlInputEvent );
						stopCapture( controlInputEvent );
					}
					break;

				case MouseEvent.MOUSE_CLICKED :
					if ( !captured )
						result = mouseClicked( controlInputEvent );

					break;
			}
		}

		return result;
	}

	/**
	 * Capture all events in the event sequence related to the specified input
	 * event. This normally invoked from the {@link #mousePressed} method to
	 * initiate a mouse-controlled operation.
	 * <p>
	 * Releasing this capture is normally not needed, since it will
	 * automatically be stopped when the event sequence is completed.
	 *
	 * @param   event   Scene input event whose sequence to capture.
	 *
	 * @see     #stopCapture
	 */
	protected void startCapture( final ControlInputEvent event )
	{
		final EventDispatcher eventDispatcher = event.getEventDispatcher();
		if ( !eventDispatcher.hasFocus( this ) )
			eventDispatcher.requestFocus( this );

		_capturedSequenceNumber = event.getSequenceNumber();
	}

	/**
	 * Stop capture of an event sequence.
	 *
	 * @param   event   Scene input event used to stop capture.
	 */
	protected void stopCapture( final ControlInputEvent event )
	{
		final EventDispatcher eventDispatcher = event.getEventDispatcher();
		if ( eventDispatcher.hasFocus( this ) )
			eventDispatcher.releaseFocus();

		_capturedSequenceNumber = -1;
	}

	/**
	 * This method is called by the {@link #filterEvent} method to test if the
	 * specified event is part of a captured sequence. It will also stop the
	 * capture automatically if a captured event sequence was terminated.
	 *
	 * @param   controlInputEvent     Scene input event to test against capture.
	 *
	 * @return  <code>true</code> if capture is valid and active;
	 *          <code>false</code> if no capture is active.
	 */
	protected boolean updateCaptureState( final ControlInputEvent controlInputEvent )
	{
		final int capturedEvent = _capturedSequenceNumber;

		final boolean result = ( capturedEvent == controlInputEvent.getSequenceNumber() );

		if ( !result && ( capturedEvent >= 0 ) )
			stopCapture( controlInputEvent );

		return result;
	}

	/**
	 * This method is called when one of the mouse buttons is pressed and
	 * released.
	 * <p>
	 * This method is not called during captured event sequences. Starting
	 * captures within this method does not seem to be a good idea either.
	 *
	 * @param   event   Scene input event.
	 *
	 * @return  Filtered event (may be same or modified);
	 *          <code>null</code> to discard event completely.
	 *
	 * @see     MouseEvent#MOUSE_CLICKED
	 */
	public EventObject mouseClicked( final ControlInputEvent event )
	{
		return event;
	}

	/**
	 * This method is called when one of the mouse buttons is pressed while no
	 * mouse event sequence capture has been started yet.
	 *
	 * @param   event   Scene input event.
	 *
	 * @return  Filtered event (may be same or modified);
	 *          <code>null</code> to discard event completely (e.g. because
	 *          a capture was started).
	 *
	 * @see     MouseEvent#MOUSE_PRESSED
	 */
	public EventObject mousePressed( final ControlInputEvent event )
	{
		return event;
	}

	/**
	 * This method is called when the mouse is dragged during a captured mouse
	 * event sequence.
	 *
	 * @param   event   Scene input event.
	 *
	 * @see     MouseEvent#MOUSE_DRAGGED
	 */
	public void mouseDragged( final ControlInputEvent event )
	{
	}

	/**
	 * This method is called when the mouse is released during a captured mouse
	 * event sequence. The event sequence will be terminated after this event.
	 *
	 * @param   event   Scene input event.
	 *
	 * @see     MouseEvent#MOUSE_RELEASED
	 */
	public void mouseReleased( final ControlInputEvent event )
	{
	}
}