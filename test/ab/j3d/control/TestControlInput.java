/* $Id$
 * ====================================================================
 * (C) Copyright Numdata BV 2005-2006
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * ====================================================================
 */
package ab.j3d.control;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.List;
import javax.swing.JPanel;

import junit.framework.TestCase;

import com.numdata.oss.event.EventDispatcher;
import com.numdata.oss.event.EventFilter;

import ab.j3d.Matrix3D;
import ab.j3d.TextureSpec;
import ab.j3d.Vector3D;
import ab.j3d.model.Face3DIntersection;
import ab.j3d.model.Node3DCollection;
import ab.j3d.model.Object3D;
import ab.j3d.view.Projector;
import ab.j3d.view.ViewModel;

/**
 * This class tests the {@link ControlInput} class.
 *
 * @see     ControlInput
 *
 * @author  Mart Slot
 * @version $Revision$ $Date$
 */
public class TestControlInput
	extends TestCase
{
	/**
	 * Name of this class.
	 */
	private static final String CLASS_NAME = TestControlInput.class.getName();

	/**
	 * Creates a new Object3D in a plane shape, with a top and a bottom face.
	 *
	 * @param   size    The size of the plane
	 *
	 * @return  The plane object
	 */
	public static Object3D createPlane( final Object tag , final double size )
	{
		final double halfSize = size / 2.0;
		final Vector3D lf = Vector3D.INIT.set( -halfSize , -halfSize , 0.0 );
		final Vector3D rf = Vector3D.INIT.set(  halfSize , -halfSize , 0.0 );
		final Vector3D rb = Vector3D.INIT.set(  halfSize ,  halfSize , 0.0 );
		final Vector3D lb = Vector3D.INIT.set( -halfSize ,  halfSize , 0.0 );

		final TextureSpec red   = new TextureSpec( Color.red   );
		final TextureSpec green = new TextureSpec( Color.green );

		final Object3D result = new Object3D();
		result.setTag( tag );

		/* top    */result.addFace( new Vector3D[]{ lf , lb , rb , rf } , red   , false , false ); // Z =  size
		/* bottom */result.addFace( new Vector3D[]{ lb , lf , rf , rb } , green , false , false ); // Z = -size

		return result;
	}

	/**
	 * Test the {@link ControlInput#getIntersections} method.
	 *
	 * @throws  Exception if the test fails.
	 */
	public static void testGetIntersections()
		throws Exception
	{
		System.out.println( CLASS_NAME + ".testGetIntersections()" );

		final Vector3D v0 = Vector3D.INIT;
		List selection;

		final SceneInputTestTranslator translator = new SceneInputTestTranslator();

		final Object3D plane1 = createPlane( "Plane 1" , 100.0 );
		final Object3D plane2 = createPlane( "Plane 2" , 100.0 );
		final Object3D plane3 = createPlane( "Plane 3" , 100.0 );
		final Object3D plane4 = createPlane( "Plane 4" , 100.0 );
		final Object3D plane5 = createPlane( "Plane 5" , 100.0 );
		final Object3D plane6 = createPlane( "Plane 6" , 100.0 );
		final Object3D plane7 = createPlane( "Plane 7" , 100.0 );

		final Matrix3D transform1 = Matrix3D.getTransform(  90.0 ,  0.0 , 0.0 ,    0.0 ,   0.0 , 0.0 );
		final Matrix3D transform2 = Matrix3D.getTransform(  90.0 ,  0.0 , 0.0 ,   10.0 ,  -1.0 , 0.0 );
		final Matrix3D transform3 = Matrix3D.getTransform(  90.0 ,  0.0 , 0.0 ,  -10.0 , -20.0 , 0.0 );
		final Matrix3D transform4 = Matrix3D.getTransform(  45.0 , 90.0 , 0.0 , -150.0 ,   0.0 , 0.0 );
		final Matrix3D transform5 = Matrix3D.getTransform( -45.0 , 90.0 , 0.0 , -150.0 ,   0.0 , 0.0 );
		final Matrix3D transform6 = Matrix3D.getTransform(  90.0 ,  0.0 , 0.0 ,  150.0 ,   0.0 , 0.0 );
		final Matrix3D transform7 = Matrix3D.getTransform(   0.0 , 90.0 , 0.0 ,  150.0 ,   0.0 , 0.0 );

		final Node3DCollection scene = translator.getScene();
		scene.add( transform1 , plane1 );
		scene.add( transform2 , plane2 );
		scene.add( transform3 , plane3 );
		scene.add( transform4 , plane4 );
		scene.add( transform5 , plane5 );
		scene.add( transform6 , plane6 );
		scene.add( transform7 , plane7 );

		selection = translator.getIntersections( v0.set( -45.0 , -500.0 , 0.0 ) , v0.set( 0.0 , 1.0 , 0.0 ) );
		assertEquals( "Incorrect number of intersected faces;" , 2 , selection.size() );
		assertEquals( "The planes are not listed from front to back at pos #0" , "Plane 3" , ((Face3DIntersection)selection.get( 0 )).getObjectID() );
		assertEquals( "The planes are not listed from front to back at pos #1" , "Plane 1" , ((Face3DIntersection)selection.get( 1 )).getObjectID() );

		selection = translator.getIntersections( v0.set( 45.0 , -500.0 , 0.0 ) , v0.set( 0.0 , 1.0 , 0.0 ) );
		assertEquals( "Incorrect number of intersected faces;" , 2 , selection.size() );
		assertEquals( "The planes are not listed from front to back at pos #0" , "Plane 2" , ((Face3DIntersection)selection.get( 0 )).getObjectID() );
		assertEquals( "The planes are not listed from front to back at pos #1" , "Plane 1" , ((Face3DIntersection)selection.get( 1 )).getObjectID() );

		selection = translator.getIntersections( v0.set( 0.0 , -500.0 , 0.0 ) , v0.set( 0.0 , 1.0 , 0.0 ) );
		assertEquals( "Incorrect number of intersected faces;" , 3 , selection.size() );
		assertEquals( "The planes are not listed from front to back at pos #0" , "Plane 3" , ((Face3DIntersection)selection.get( 0 )).getObjectID() );
		assertEquals( "The planes are not listed from front to back at pos #1" , "Plane 2" , ((Face3DIntersection)selection.get( 1 )).getObjectID() );
		assertEquals( "The planes are not listed from front to back at pos #1" , "Plane 1" , ((Face3DIntersection)selection.get( 2 )).getObjectID() );

		selection = translator.getIntersections( v0.set( -125.0 , -500.0 , 0.0 ) , v0.set( 0.0 , 1.0 , 0.0 ) );
		assertEquals( "Incorrect number of intersected faces;" , 2 , selection.size() );
		assertEquals( "The planes are not listed from front to back at pos #0" , "Plane 4" , ((Face3DIntersection)selection.get( 0 )).getObjectID() );
		assertEquals( "The planes are not listed from front to back at pos #1" , "Plane 5" , ((Face3DIntersection)selection.get( 1 )).getObjectID() );

		selection = translator.getIntersections( v0.set( -149.9 , -500.0 , 0.0 ) , v0.set( 0.0 , 1.0 , 0.0 ) );
		assertEquals( "Incorrect number of intersected faces;" , 2 , selection.size() );
		assertEquals( "The planes are not listed from front to back at pos #0" , "Plane 4" , ((Face3DIntersection)selection.get( 0 )).getObjectID() );
		assertEquals( "The planes are not listed from front to back at pos #1" , "Plane 5" , ((Face3DIntersection)selection.get( 1 )).getObjectID() );

		selection = translator.getIntersections( v0.set( -150.0 , -500.0 , 0.0 ) , v0.set( 0.0 , 1.0 , 0.0 ) );
		assertEquals( "Incorrect number of intersected faces;" , 2 , selection.size() );

		selection = translator.getIntersections( v0.set( 200.0 , -25.0 , 0.0 ) , v0.set( -Math.sqrt( 0.5 ) , Math.sqrt( 0.5 ) , 0.0 ) );
		assertEquals( "Incorrect number of intersected faces;" , 2 , selection.size() );
		assertEquals( "The planes are not listed from front to back at pos #0" , "Plane 6" , ((Face3DIntersection)selection.get( 0 )).getObjectID() );
		assertEquals( "The planes are not listed from front to back at pos #1" , "Plane 7" , ((Face3DIntersection)selection.get( 1 )).getObjectID() );

		selection = translator.getIntersections( v0.set( 100.0 , 0.0 , -25.0 ) , v0.set( 1.0 , 0.0 , 0.0 ) );
		assertEquals( "The number of intersected faces is not 2, but " + selection.size() , 1 , selection.size() );
		assertEquals( "The planes are not listed from front to back at pos #0" , "Plane 7" , ((Face3DIntersection)selection.get( 0 )).getObjectID() );

		selection = translator.getIntersections( v0.set( 100.0 , 0.0 , -100.0 ) , v0.set( 1.0 , 0.0 , 0.0 ) );
		assertEquals( "Incorrect number of intersected faces;" , 0 , selection.size() );

		selection = translator.getIntersections( v0.set( -60.1  , 0.0 , -500.0) , v0.set( 0.0 , 0.0 , 1.0 ) );
		assertEquals( "Incorrect number of intersected faces;" , 0 , selection.size() );
	}

	/**
	 * Test the {@link ControlInput#mousePressed} method.
	 *
	 * @throws Exception if the test fails.
	 */
	public void testMousePressed()
		throws Exception
	{
		System.out.println( CLASS_NAME + ".testMousePressed()" );

		final SceneInputTestTranslator translator = new SceneInputTestTranslator();

		final Object3D plane1 = createPlane( "Plane 1" , 100.0 );
		final Object3D plane2 = createPlane( "Plane 2" , 100.0 );

		final Matrix3D transform1 = Matrix3D.getTransform( 90.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0   );
		final Matrix3D transform2 = Matrix3D.getTransform( 0.0  , 0.0 , 0.0 , 0.0 , 0.0 , -75.0 );

		final Node3DCollection scene = translator.getScene();
		scene.add( transform1 , plane1 );
		scene.add( transform2 , plane2 );

		int modifiers = MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK;
		MouseEvent e = new MouseEvent( new JPanel() , MouseEvent.MOUSE_PRESSED , 0L , modifiers , 0 , 0 , 1 , false , MouseEvent.BUTTON1 );
		translator.mousePressed( e );

		assertTrue("The last event is not a MouseControlEvent", translator.getLastEvent() instanceof ControlInputEvent );
		ControlInputEvent event = (ControlInputEvent)translator.getLastEvent();

		assertEquals( "The mouse button is not 1" , MouseEvent.BUTTON1 , event.getMouseButton() );
		assertEquals( "The event type should be MOUSE_PRESSED" , MouseEvent.MOUSE_PRESSED , event.getID() );
		final int lastNumber = event.getSequenceNumber();


		modifiers = MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.CTRL_DOWN_MASK;
		e = new MouseEvent( new JPanel() , MouseEvent.MOUSE_PRESSED , 0L , modifiers , 50 , 50 , 1 , false , MouseEvent.BUTTON2 );
		translator.mousePressed( e );

		assertTrue( "The last event is not a MouseControlEvent" , translator.getLastEvent() instanceof ControlInputEvent );
		event = (ControlInputEvent)translator.getLastEvent();

		assertEquals( "The mouse button is not 2" , MouseEvent.BUTTON2 , event.getMouseButton() );
		assertEquals( "The event type should be MOUSE_PRESSED" , MouseEvent.MOUSE_PRESSED , event.getID() );
		assertEquals( "The event number has changed" , lastNumber , event.getSequenceNumber() );
	}

	/**
	 * Test the {@link ControlInput#mouseReleased} method.
	 *
	 * @throws Exception if the test fails.
	 */
	public void testMouseReleased()
		throws Exception
	{
		System.out.println( CLASS_NAME + ".testMouseReleased()" );

		final SceneInputTestTranslator translator = new SceneInputTestTranslator();

		final Object3D plane1 = createPlane( "Plane 1" , 100.0 );
		final Object3D plane2 = createPlane( "Plane 2" , 100.0 );

		final Matrix3D transform1 = Matrix3D.getTransform( 90.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0   );
		final Matrix3D transform2 = Matrix3D.getTransform( 0.0  , 0.0 , 0.0 , 0.0 , 0.0 , -75.0 );

		final Node3DCollection scene = translator.getScene();
		scene.add( transform1 , plane1 );
		scene.add( transform2 , plane2 );

		int modifiers = MouseEvent.SHIFT_DOWN_MASK;
		MouseEvent e = new MouseEvent( new JPanel() , MouseEvent.MOUSE_RELEASED , 0L , modifiers , 0 , 0 , 1 , false , MouseEvent.BUTTON1 );
		translator.mouseReleased( e );

		assertTrue("The last event is not a MouseControlEvent" , translator.getLastEvent() instanceof ControlInputEvent );
		ControlInputEvent event = (ControlInputEvent)translator.getLastEvent();

		assertEquals( "The mouse button is not 1" , MouseEvent.BUTTON1 , event.getMouseButton() );
		assertEquals( "The event type should be MOUSE_RELEASED" , MouseEvent.MOUSE_RELEASED , event.getID() );
		final int lastNumber = event.getSequenceNumber();

		modifiers = MouseEvent.CTRL_DOWN_MASK;
		e = new MouseEvent( new JPanel() , MouseEvent.MOUSE_RELEASED , 0L , modifiers , 0 , 0 , 1 , false , MouseEvent.BUTTON3 );
		translator.mouseReleased( e );

		assertTrue( "The last event is not a MouseControlEvent" , translator.getLastEvent() instanceof ControlInputEvent );
		event = (ControlInputEvent)translator.getLastEvent();

		assertEquals( "The mouse button is not 3" , MouseEvent.BUTTON3 , event.getMouseButton() );
		assertEquals( "The event type should be MOUSE_RELEASED" , MouseEvent.MOUSE_RELEASED , event.getID() );
		assertEquals( "The event number has not increased" , lastNumber + 1 , event.getSequenceNumber() );
	}

	/**
	 * This inner class implements {@link ControlInput} for testing.
	 */
	public static class SceneInputTestTranslator
		extends ControlInput
	{
		/**
		  Static scene;
		 */
		private final Node3DCollection _scene;

		/**
		 * Last event that was handled.
		 */
		private EventObject _lastEvent;

		/**
		 * Create {@link SceneInputTestTranslator} with default scene.
		 */
		public SceneInputTestTranslator()
		{
			super( new JPanel() );

			_scene = new Node3DCollection();

			_lastEvent = null;
			final EventDispatcher eventQueue = getEventDispatcher();
			eventQueue.appendFilter( new EventFilter()
				{
					public EventObject filterEvent( final EventObject event )
					{
						_lastEvent = event;
						return event;
					}
				} );
		}

		protected Object getIDForObject( final Object3D object )
		{
			return object.getTag();
		}

		protected Projector getProjector()
		{
			return Projector.createInstance( Projector.PERSPECTIVE , 100 , 100 , 1.0 , ViewModel.M , 10.0 , 1000.0 , Math.toRadians( 45.0 ) , 1.0 );
		}

		protected Matrix3D getViewTransform()
		{
			return Matrix3D.INIT.set( 1.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 1.0 , 0.0 , 0.0 , -1.0 , 0.0 , -500.0 );
		}

		protected Node3DCollection getScene()
		{
			return _scene;
		}

		/**
		 * Get last event that was handled.
		 *
		 * @return  Last event that was handled.
		 */
		public EventObject getLastEvent()
		{
			return _lastEvent;
		}
	}
}