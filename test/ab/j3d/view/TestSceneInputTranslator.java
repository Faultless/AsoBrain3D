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

import java.awt.event.MouseEvent;
import java.awt.Color;
import java.util.List;
import javax.swing.JPanel;

import junit.framework.TestCase;

import ab.j3d.model.Node3DCollection;
import ab.j3d.model.Object3D;
import ab.j3d.model.Face3D;
import ab.j3d.Matrix3D;
import ab.j3d.Vector3D;
import ab.j3d.TextureSpec;

/**
 * This class tests the {@link SceneInputTranslator} class.
 *
 * @author Peter S. Heijnen
 * @version $Revision$ $Date$
 * @see SceneInputTranslator
 */
public class TestSceneInputTranslator
extends TestCase
{
	/**
	 * Name of this class.
	 */
	private static final String CLASS_NAME = TestSceneInputTranslator.class.getName();

	/**
	 * Fixture instance.
	 */
	private SceneInputTranslator _translator;
	private ControlEvent _lastEvent;

	private Object3D _plane1;
	private Object3D _plane2;
	private Matrix3D _transform1;
	private Matrix3D _transform2;

	private Node3DCollection _collection;

	/**
	 * Setup text fixture.
	 *
	 * @throws Exception if there was a problem setting up the fixture.
	 */
	public void setUp()
	throws Exception
	{
		super.setUp();

		_collection = new Node3DCollection();

		final Matrix3D viewTransform = Matrix3D.INIT
		.set( 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, -1.0, 0.0, -500 );

		_plane1 = createPlane( 100.0 );
		_plane1.setTag( "Plane 1" );
		_transform1 = Matrix3D.getTransform( 90, 0, 0, 0, 0, 0 )
		.multiply( viewTransform );
		_collection.add( _transform1, _plane1 );

		_plane2 = createPlane( 100.0 );
		_plane2.setTag( "Plane 2" );
		_transform2 = Matrix3D.getTransform( 0, 0, 0, 0, 0, -75 )
		.multiply( viewTransform );
		_collection.add( _transform2, _plane2 );

		_translator = new SceneInputTranslator(new JPanel()){
			public boolean blaat = true;
			protected Node3DCollection getScene()
			{
				return _collection;
			}

			protected Projector getProjector()
			{
				return Projector.createInstance( Projector.PERSPECTIVE, 100, 100, 1, ViewModel.M, 10, 1000, Math.toRadians( 45 ) , 1.0);
			}
		};

		_translator.getEventQueue().addControl( new Control() {
			public ControlEvent handleEvent( final ControlEvent e )
			{
				_lastEvent = e;
				return e;
			}
		});
	}

	/**
	 * Tear down test fixture.
	 *
	 * @throws Exception if there was a problem tearing down the fixture.
	 */
	public void tearDown()
	throws Exception
	{
		_translator = null;

		super.tearDown();
	}

	/**
	 * Test the {@link SceneInputTranslator#getFacesAt} method.
	 *
	 * @throws Exception if the test fails.
	 */
	public void testGetFacesAt()
	throws Exception
	{
		System.out.println( CLASS_NAME + ".testGetFacesAt()" );

		System.out.println( "Selecting Plane 1" );
		List selection = _translator.getFacesAt( 50, 50);
		assertEquals( "The number of selected items is not 2, but " + selection.size() , 2, selection.size() );
		Object tag = ((Face3D)selection.get( 0)).getObject().getTag();
		assertEquals( "The selected object should have the tag Plane 1" , "Plane 1" , tag);

		System.out.println( "Selecting Plane 1" );
		selection = _translator.getFacesAt( 40, 40);
		assertEquals( "The number of selected items is not 2, but " + selection.size() , 2, selection.size() );
		tag = ((Face3D)selection.get( 0)).getObject().getTag();
		assertEquals( "The selected object should have the tag Plane 1" , "Plane 1" , tag);

		System.out.println( "Selecting nothing" );
		selection = _translator.getFacesAt( 50, 63);
		assertEquals( "The number of selected items is not 0, but " + selection.size() , 0, selection.size() );

		System.out.println( "Selecting Plane 2" );
		selection = _translator.getFacesAt( 50, 67);
		assertEquals( "The number of selected items is not 2, but " + selection.size() , 2, selection.size() );
		tag = ((Face3D)selection.get( 0)).getObject().getTag();
		assertEquals( "The selected object should have the tag Plane 2" , "Plane 2" , tag);

	}

	/**
	 * Test the {@link SceneInputTranslator#mousePressed} method.
	 *
	 * @throws Exception if the test fails.
	 */
	public void testMousePressed()
	throws Exception
	{
		System.out.println( CLASS_NAME + ".testMousePressed()" );

		int modifiers = MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK;
		MouseEvent e = new MouseEvent(new JPanel() , MouseEvent.MOUSE_PRESSED , 0l, modifiers, 0, 0, 1, false, MouseEvent.BUTTON1);
		_translator.mousePressed( e );

		assertTrue("The last event is not a MouseControlEvent", _lastEvent instanceof MouseControlEvent);
		MouseControlEvent event = (MouseControlEvent)_lastEvent;

		assertEquals( "The event modifiers have changed", modifiers, event.getModifiers() );
		assertEquals( "The mouse button is not 1", 1 , event.getButton() );
		assertEquals( "The event type should be MOUSE_PRESSED", MouseControlEvent.MOUSE_PRESSED , event.getType() );
		final int lastNumber = event.getNumber();


		modifiers = MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.CTRL_DOWN_MASK;
		e = new MouseEvent(new JPanel() , MouseEvent.MOUSE_PRESSED , 0l, modifiers, 50, 50, 1, false, MouseEvent.BUTTON2);
		_translator.mousePressed( e );

		assertTrue("The last event is not a MouseControlEvent", _lastEvent instanceof MouseControlEvent);
		event = (MouseControlEvent)_lastEvent;

		assertEquals( "The event modifiers have changed", modifiers, event.getModifiers() );
		assertEquals( "The mouse button is not 2", 2 , event.getButton() );
		assertEquals( "The event type should be MOUSE_PRESSED", MouseControlEvent.MOUSE_PRESSED , event.getType() );
		assertEquals( "The event number has changed", lastNumber, event.getNumber());
	}


	/**
	 * Test the {@link SceneInputTranslator#mouseReleased} method.
	 *
	 * @throws Exception if the test fails.
	 */
	public void testMouseReleased()
	throws Exception
	{
		System.out.println( CLASS_NAME + ".testMouseReleased()" );

		int modifiers = MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK;
		MouseEvent e = new MouseEvent(new JPanel() , MouseEvent.MOUSE_RELEASED , 0l, modifiers, 0, 0, 1, false, MouseEvent.BUTTON1);
		_translator.mouseReleased( e );

		assertTrue("The last event is not a MouseControlEvent", _lastEvent instanceof MouseControlEvent);
		MouseControlEvent event = (MouseControlEvent)_lastEvent;

		assertEquals( "The event modifiers have changed", modifiers, event.getModifiers() );
		assertEquals( "The mouse button is not 1", 1 , event.getButton() );
		assertEquals( "The event type should be MOUSE_RELEASED", MouseControlEvent.MOUSE_RELEASED , event.getType() );
		final int lastNumber = event.getNumber();


		modifiers = MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.CTRL_DOWN_MASK;
		e = new MouseEvent(new JPanel() , MouseEvent.MOUSE_RELEASED , 0l, modifiers, 0, 0, 1, false, MouseEvent.BUTTON3);
		_translator.mouseReleased( e );

		assertTrue("The last event is not a MouseControlEvent", _lastEvent instanceof MouseControlEvent);
		event = (MouseControlEvent)_lastEvent;

		assertEquals( "The event modifiers have changed", modifiers, event.getModifiers() );
		assertEquals( "The mouse button is not 3", 3 , event.getButton() );
		assertEquals( "The event type should be MOUSE_RELEASED", MouseControlEvent.MOUSE_RELEASED , event.getType() );
		assertEquals( "The event number has not increased", lastNumber + 1, event.getNumber());

	}

	/**
	 * Creates a new Object3D in a plane shape, with a top and a bottom face.
	 * @param size The size of the plane
	 * @return The plane object
	 */
	private Object3D createPlane( final double size )
	{
		final double halfSize = size / 2;
		final Vector3D lf = Vector3D.INIT.set( -halfSize , -halfSize , 0.0 );
		final Vector3D rf = Vector3D.INIT.set(  halfSize , -halfSize , 0.0 );
		final Vector3D rb = Vector3D.INIT.set(  halfSize ,  halfSize , 0.0 );
		final Vector3D lb = Vector3D.INIT.set( -halfSize ,  halfSize , 0.0 );

		final TextureSpec red     = new TextureSpec( Color.red     );
		final TextureSpec green   = new TextureSpec( Color.green   );

		final Object3D plane = new Object3D();
		/* top    */ plane.addFace( new Vector3D[] { lf , lb , rb , rf } , red     , false , false ); // Z =  size
		/* bottom */ plane.addFace( new Vector3D[] { lb , lf , rf , rb } , green   , false , false ); // Z = -size

		return plane;
	}

}