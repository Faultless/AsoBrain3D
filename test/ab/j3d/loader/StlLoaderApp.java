/* $Id$
 * ====================================================================
 * AsoBrain 3D Toolkit
 * Copyright (C) 1999-2011 Peter S. Heijnen
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
package ab.j3d.loader;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import ab.j3d.*;
import ab.j3d.control.*;
import ab.j3d.model.*;
import ab.j3d.view.*;
import ab.j3d.view.jogl.*;
import com.numdata.oss.ui.*;

/**
 * This is a sample application for the {@link StlLoader} class.
 *
 * @author Peter S. Heijnen
 * @version $Revision$ $Date$
 */
public class StlLoaderApp
{
	/**
	 * Run application.
	 *
	 * @param   args    Command-line arguments.
	 *
	 * @throws  Exception if the application crashes.
	 */
	public static void main( final String[] args )
		throws Exception
	{
		final double unit = Scene.MM;
		final Matrix3D transform = Matrix3D.IDENTITY.rotateX( Math.toRadians( 180.0 ) );

		final StlLoader loader = new StlLoader();
		final Object3D object3D = loader.load( transform, StlLoaderApp.class.getResourceAsStream( "bordatore.stl" ) );

		final Bounds3D bounds = object3D.getOrientedBoundingBox();
		if ( bounds == null )
		{
			throw new RuntimeException( "No bounds?" );
		}

		final Vector3D size = bounds.size();
		final double toCM = 100.0 * unit;
		System.out.println( "size = " + Math.round( toCM * size.x ) + " x " + Math.round( toCM * size.y ) + " x " + Math.round( toCM * size.z ) + " cm" );

		final Vector3D viewFrom = new Vector3D( 0.0, -size.y - 0.5 / unit, bounds.v1.z - 0.5 / unit );
		final Vector3D viewAt = bounds.center();

		final Scene scene = new Scene( unit );
		Scene.addLegacyLights( scene );
		scene.addContentNode( "object", Matrix3D.IDENTITY, object3D );

		final RenderEngine renderEngine = new JOGLEngine();

		final View3D view = renderEngine.createView( scene );
		view.setBackground( Background.createGradient( new Color( 0x677988 ), new Color( 0x174772 ), new Color( 0x85A4BF ), new Color( 0x9FB8CE ) ) );

		view.setCameraControl( new FromToCameraControl( view, viewFrom, viewAt ) );

		final JPanel viewPanel = new JPanel( new BorderLayout() );
		viewPanel.add( view.getComponent(), BorderLayout.CENTER );
		viewPanel.add( view.createToolBar( new Locale( "nl" ) ), BorderLayout.SOUTH );

		final JFrame frame = WindowTools.createFrame( StlLoaderApp.class.getName(), 800, 600, viewPanel );
		frame.setVisible( true );
	}

	/**
	 * Utility/Application class is not supposed to be instantiated.
	 */
	private StlLoaderApp()
	{
	}
}