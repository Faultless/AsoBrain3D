/* $Id$
 * ====================================================================
 * (C) Copyright Numdata BV 2005-2005
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
package ab.j3d.pov;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import ab.j3d.Matrix3D;
import ab.j3d.TextureSpec;
import ab.j3d.Vector3D;
import ab.j3d.model.Box3D;
import ab.j3d.model.Camera3D;
import ab.j3d.model.Cylinder3D;
import ab.j3d.model.Face3D;
import ab.j3d.model.Light3D;
import ab.j3d.model.Node3D;
import ab.j3d.model.Node3DCollection;
import ab.j3d.model.Object3D;
import ab.j3d.model.Sphere3D;
import ab.j3d.model.ExtrudedObject2D;
import ab.j3d.view.ViewModel;
import ab.j3d.view.ViewModelNode;
import ab.j3d.view.ViewModelView;
import com.numdata.oss.ui.ImageTools;

/**
 * This class can be used to convert an AB-scene (ViewModel) to a POV-scene.
 *
 * @author  Rob Veneberg
 * @version $Revision$ $Date$
 */
public class AbToPovConverter
{
	/**
	 * Variable that will hold the converted scene.
	 */
	private PovScene _scene;

	/**
	 * Construct new AbToPovConverter and set the texturepath.
	 */
	public AbToPovConverter()
	{
		PovTexture.texturePath = "SODA_BaseComponents/images/textures/";
		_scene = new PovScene();
	}

	/**
	 * Workhorse of the converter. First the model is converted to a Node3DCollection and then all
	 * individual nodes are converted. Objects with texture mapping and/or multiple textures and extruded
	 * objects are converted as Object3D's.
	 *
	 * @param viewModel The model to be converted to a POV-scene.
	 * @return The resulting PovScene object.
	 */
	public PovScene convert( final ViewModel viewModel )
	{
		final PovScene         scene = _scene;
		final Node3DCollection nodes = getNode3DCollection( viewModel );

		for ( int i = 0 ; i < nodes.size() ; i++ )
		{
			final Node3D   node      = nodes.getNode( i );
			final Matrix3D transform = nodes.getMatrix( i );

			if ( node instanceof Object3D )
			{
				final Object3D object = (Object3D)node;

				if ( containsTextures( object ) || object instanceof ExtrudedObject2D )
				{
					scene.add( convertObject3D( object , transform ) );
					continue;
				}

				if ( object instanceof Box3D )
				{
					scene.add( convertBox3D( (Box3D)object ) );
				}
				else if ( object instanceof Sphere3D )
				{
					scene.add( convertSphere3D( (Sphere3D)object ) );
				}
				else if ( object instanceof Cylinder3D )
				{
					scene.add( convertCylinder3D( (Cylinder3D)object ) );
				}
				else
				{
					scene.add( convertObject3D( object , transform ) );
				}
			}
			else if (node instanceof Light3D )
			{
				//scene.add( convertLight3D( (Light3D)node , transform ) );
			}
			else if (node instanceof Camera3D )
			{
				//final Camera3D camera = (Camera3D)node;
				//scene.add( convertCamera3D( camera ) , transform );
			}
		}

		/**
		 * Add some ligt to the scene. Light is not fully implemented yet in AB.
		 */
		final Color    color    = new Color( 255 , 255 , 240 );
		final PovLight povLight = new PovLight( "light1" , 0.0 , 0.0 , 0.0 , new PovVector( color ) , true );

		povLight.setTransform( new PovMatrix( Matrix3D.INIT.setTranslation( -4000.0 , -4000.0 , 8000.0 ) ) );
		povLight.makeArea( new PovVector( 1.0 , 0.0 , 0.0 ) , 3.0 , new PovVector( 0.0 , 1.0 , 0.0 ) , 3.0 , false );
		povLight.adaptive = 1.0;
		scene.add( povLight );

		/**
		 * 2.2 is the best value for pc's.
		 */
		scene.gamma = 2.2f;

		return scene;
	}

	/**
	 * The AB-tree is traversed and all leafs are gathered in a Node3DCollection (could be method of ViewModel?)
	 *
	 * @return The resulting Node3DCollection.
	 */
	public static Node3DCollection getNode3DCollection( final ViewModel viewModel )
	{
		final Node3DCollection nodeCollection = new Node3DCollection();
		final Object[]         ids            = viewModel.getViewIDs();
		final ViewModelView    view           = viewModel.getView( ids[ 0 ] );

		if ( view != null )
		{
			final Object[] nodeIDs = viewModel.getNodeIDs();

			for ( int i = 0 ; i < nodeIDs.length ; i++ )
			{
				final Object        id         = nodeIDs[ i ];
				final ViewModelNode node       = viewModel.getNode( id );
				final Matrix3D      node2model = node.getTransform();
				final Node3D        node3D     = node.getNode3D();

				node3D.gatherLeafs( nodeCollection , Node3D.class , node2model , false );
			}
		}

		return nodeCollection;
	}

	/**
	 * The Camera3D is not fully implemented yet in AB. The PovCamera is temporarily constructed based
	 * on data obtained from the view.
	 *
	 * @param view The view to get the camera data from.
	 * @return The resulting PovCamera object.
	 */
	public static PovCamera convertCamera3D( final ViewModelView view )
	{
		Matrix3D viewTransform = view.getViewTransform();

		final Matrix3D swapYZ = Matrix3D.INIT.set( 1.0 , 0.0 ,  0.0 , 0.0 ,
		                                           0.0 , 1.0 ,  0.0 , 0.0 ,
		                                           0.0 , 0.0 , -1.0 , 0.0 );

		viewTransform = swapYZ.multiply( viewTransform.inverse() );

		/**
		 * The standard ratio used in POV is 4:3, wich means the rendered image will
		 * get deformed if resolutions with a ratio other than 4:3 are used. Since the view does not have a fixed
		 * ratio (the user can resize the view for example), the ratio also needs to be specified in the POV camera.
		 */
		final Component viewComponent = view.getComponent();
		final double    viewWidth     = (double)viewComponent.getWidth();
		final double    viewHeight    = (double)viewComponent.getHeight();
		final double    ratio         = viewWidth / viewHeight;
		final PovVector right         = new PovVector( ratio , 0.0 , 0.0 );

		final PovCamera povCamera = new PovCamera( "camera" , null , null , right ,  view.getFieldOfView() );
		povCamera.setTransform( new PovMatrix( viewTransform ) );

		return povCamera;
	}

	/**
	 * This method constructs a PovBox from a Box3D object.
	 *
	 * @param box The box to be converted to a PovBox object.
	 * @return The resulting PovBox object.
	 */
	public PovBox convertBox3D( final Box3D box )
	{
		final Face3D      face        = box.getFace( 0 );
		final TextureSpec textureSpec = face.getTexture();
		final PovTexture  povTexture  = new PovTexture( textureSpec );
		final PovBox      povBox      = new PovBox( "box" , box , povTexture );

		povBox.setTransform( new PovMatrix( box.getTransform() ) );

		povTexture.reflection = 0.05f;
		_scene.addTexture( povTexture.name , povTexture );
		povTexture.setDeclared();

		return povBox;
	}

	/**
	 * This method constructs a PovSphere from a Sphere3D object.
	 *
	 * @param sphere The sphere to be converted to a PovSphere object.
	 * @return The resulting PovSphere object.
	 */
	public PovSphere convertSphere3D( final Sphere3D sphere )
	{
		final Face3D      face        = sphere.getFace( 0 );
		final TextureSpec textureSpec = face.getTexture();
		final PovTexture  povTexture  = new PovTexture( textureSpec );

		povTexture.reflection = 0.05f;
		_scene.addTexture( povTexture.name , povTexture );
		povTexture.setDeclared();

		final PovSphere povSphere =  new PovSphere( "sphere" , Vector3D.INIT , sphere.dx / 2.0 , povTexture );
		povSphere.setTransform( new PovMatrix( sphere.xform ) );

		return povSphere;
	}

	/**
	 * This method constructs a PovCylinder from a Cylinder3D object.
	 *
	 * @param cylinder The cylinder to be converted to a PovCylinder object.
	 * @return The resulting PovCylinder object.
	 */
	public PovCylinder convertCylinder3D( final Cylinder3D cylinder )
	{
		final Face3D      face        = cylinder.getFace( 0 );
		final TextureSpec textureSpec = face.getTexture();
		final PovTexture  povTexture  = new PovTexture( textureSpec );

		povTexture.reflection = 0.05f;
		_scene.addTexture( povTexture.name , povTexture );
		povTexture.setDeclared();

		return new PovCylinder( "cylinder" , cylinder , povTexture );
	}

	/**
	 * This method converts an Object3D to a PovMesh2. All faces are converted to one or more mesh triangles and
	 * the face textures are up-mapped to the triangles.
	 *
	 * @param object The object to be converted to a Mesh2 object.
	 * @param transform The node2model transform.
	 * @return The resulting PovMesh2 object.
	 */
	public PovMesh2 convertObject3D( final Object3D object , final Matrix3D transform )
	{
		final PovMesh2 mesh        = new PovMesh2( "mesh2" );
		final int      numFaces    = object.getFaceCount();
		final double[] temp        = object.getPointCoords();
		final double[] pointCoords = transform.transform( temp , null , temp.length / 3 );

		/**
		 * Loop all faces and create triangles from 3 vertices. For every triangle
		 * the first vertex is the same (the face is always convex).
		 *
		 *  0  _________ 1
		 *    |\        |
		 *    | \       |
		 *    |  \      |
		 *    |   \     |
		 *    |    \    |
		 *    |     \   |
		 *    |      \  |
		 *    |       \ |
		 *  3 |________\| 2
		 *
		 * Triangle 1: ( 0 , 1 , 2 )
		 * Triangle 2: ( 0 , 2 , 3 )
		 */
		for ( int i = 0 ; i < numFaces ; i++ )
		{
			final Face3D      face              = object.getFace( i );
			final int[]       pointIndices      = face.getPointIndices();
			final TextureSpec texture           = face.getTexture();
			final boolean     uvMapping         = texture.isTexture();
			final int         faceVerticesCount = pointIndices.length;
			      int         textureWidth      = 0;
			      int         textureHeight     = 0;
			      PovVector   firstUvCoord      = null;
			      double      u;
			      double      v;

			if ( uvMapping )
			{
				final Image textureImage = texture.getTextureImage();
				ImageTools.waitFor( textureImage , ImageTools.SHARED_OBSERVER );
				textureWidth  = textureImage.getWidth( null );
				textureHeight = textureImage.getHeight( null );

				// If material has texture, get first uv-coordinate.
				u = (double)face.getTextureU( 0 ) / (double)textureWidth;
				v = (double)face.getTextureV( 0 ) / (double)textureHeight;

				firstUvCoord = new PovVector( u , v , 0.0 );
			}

			/**
			 * For every triangle, the first vertex is the same.
			 */
			double x = pointCoords[ pointIndices[ 0 ] * 3     ];
			double y = pointCoords[ pointIndices[ 0 ] * 3 + 1 ];
			double z = pointCoords[ pointIndices[ 0 ] * 3 + 2 ];

			final PovVector firstVertex  = new PovVector( x , y , z );

			for ( int j = 1 ; j < faceVerticesCount - 1 ; j++ )
			{
				/**
				 * Second vertex.
				 */
				x = pointCoords[ pointIndices[ j ] * 3     ];
				y = pointCoords[ pointIndices[ j ] * 3 + 1 ];
				z = pointCoords[ pointIndices[ j ] * 3 + 2 ];

				final PovVector secondVertex  = new PovVector( x , y , z );

				/**
				 * Third vertex.
				 */
				x = pointCoords[ pointIndices[ j + 1 ] * 3     ];
				y = pointCoords[ pointIndices[ j + 1 ] * 3 + 1 ];
				z = pointCoords[ pointIndices[ j + 1 ] * 3 + 2 ];

				final PovVector  thirdVertex = new PovVector( x , y , z );
				final PovTexture povTexture  = new PovTexture( texture );

				povTexture.reflection = 0.05f;
				_scene.addTexture( povTexture.name , povTexture );
				povTexture.setDeclared();

				if ( uvMapping )
				{
					// If material has texture, get second uv-coordinate.
					u = (double)face.getTextureU( j ) / (double)textureWidth;
					v = (double)face.getTextureV( j ) / (double)textureHeight;
					final PovVector secondUvCoord = new PovVector( u , v , 0.0 );

					// If material has texture, get third uv-coordinate.
					u = (double)face.getTextureU( j + 1 ) / (double)textureWidth;
					v = (double)face.getTextureV( j + 1 ) / (double)textureHeight;
					final PovVector thirdUvCoord = new PovVector( u , v , 0.0 );

					mesh.addTriangle( firstVertex , secondVertex , thirdVertex , firstUvCoord , secondUvCoord , thirdUvCoord , povTexture );
				}
				else
				{

					mesh.addTriangle( firstVertex , secondVertex , thirdVertex , null , null , null , povTexture );
				}
			 }
		 }

		 return mesh;
	}

	/**
	 * This method converts a Light3D object to a PovLight object. The Light3D class is not
	 * implemented correctly yet.
	 *
	 * @param light The Light3D object to be converted.
	 * @param transform The node2model transform.
	 * @return The resulting PovLight object.
	 */
	public static PovLight convertLight3D( final Light3D light , final Matrix3D transform )
	{
		final PovLight povLight = new PovLight( "light" , 0.0 , 0.0 , 0.0 , new PovVector( Color.WHITE ) , true );
		povLight.setTransform( new PovMatrix( transform ) );

		return povLight;
	}

	/**
	 * Method to check if an object contains textures (could be method of Object3D).
	 *
	 * @param object The object to check.
	 * @return True if the object contains textures, false otherwise.
	 */
	public static boolean containsTextures( final Object3D object )
	{
		final int     numFaces         = object.getFaceCount();
		      boolean containsTextures = false;

		for ( int i = 0 ; i < numFaces && !containsTextures ; i++ )
		{
			final Face3D face = object.getFace( i );
			final TextureSpec texture = face.getTexture();

			if ( texture.isTexture() )
				containsTextures = true;
		}

		return containsTextures;
	}
}