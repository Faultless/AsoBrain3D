/* $Id$
 * ====================================================================
 * (C) Copyright Numdata BV 2009-2009
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
package ab.j3d.loader.max3ds;

import java.awt.geom.Point2D;
import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ab.j3d.Matrix3D;
import ab.j3d.Vector3D;

/**
 * Type   : {@link #OBJ_TRIMESH}.
 * Parent : {@link #NAMED_OBJECT}
 *
 * @noinspection JavaDoc,PublicField,InstanceVariableMayNotBeInitialized
 */
class TriangleMeshChunk
	extends Chunk
{
	List<Vector3D> _vertices;

	Point2D.Float[] _textureCoordinates;

	byte _color;

	FacesChunk _faces;

	Matrix3D _transform;

	TriangleMeshChunk( final DataInput dataInput , final int chunkType , final int remainingChunkBytes )
		throws IOException
	{
		super( dataInput , chunkType , remainingChunkBytes );
	}

	protected void processChildChunk( final DataInput dataInput , final int chunkType , final int remainingChunkBytes )
		throws IOException
	{
		switch ( chunkType )
		{
			case VERTEX_LIST:
				readVertexCoordinates( dataInput );
				break;

			case TEXT_COORDS:
				readTextureCoordinates( dataInput );
				break;

			case COORD_SYS:
				readCoordinateSystem( dataInput );
				break;

			case FACES_ARRAY:
				_faces = new FacesChunk( dataInput , chunkType , remainingChunkBytes );
				break;

			case VERTEX_OPTIONS:
				readOptions( dataInput );
				break;

			case MESH_COLOR:
				_color = dataInput.readByte();
				break;

			case MESH_TEXTURE_INFO:
				readMeshTextureInfo( dataInput );
				break;

			default : // Ignore unknown chunks
				skipFully( dataInput , remainingChunkBytes );
		}
	}

	private static void readMeshTextureInfo( final DataInput dataInput )
		throws IOException
	{
		/*final short type    =*/ dataInput.readShort();
		/*final float xTiling =*/ dataInput.readFloat();
		/*final float yTiling =*/ dataInput.readFloat();
		/*final float Xicon   =*/ dataInput.readFloat();
		/*final float Yicon   =*/ dataInput.readFloat();
		/*final float Zicon   =*/ dataInput.readFloat();

		final float[][] matrix = new float[ 4 ][ 3 ];
		for ( int i = 0; i < 4; i++ )
		{
			for ( int j = 0; j < 3; j++ )
			{
				matrix[ i ][ j ] = dataInput.readFloat();
			}
		}

		/*final float scaling   =*/ dataInput.readFloat();
		/*final float planIconW =*/ dataInput.readFloat();
		/*final float planIconH =*/ dataInput.readFloat();
		/*final float cylIconH  =*/ dataInput.readFloat();
	}

	private static void readOptions( final DataInput dataInput )
		throws IOException
	{
		final int numberOfOptions = dataInput.readUnsignedShort();
		for ( int i = 0; i < numberOfOptions; i++ )
		{
			/*final short option =*/ dataInput.readShort();
		}
	}

	private void readCoordinateSystem( final DataInput dataInput )
		throws IOException
	{
		final double rotationXX = (double)dataInput.readFloat();
		final double rotationYX = (double)dataInput.readFloat();
		final double rotationZX = (double)dataInput.readFloat();
		final double rotationXY = (double)dataInput.readFloat();
		final double rotationYY = (double)dataInput.readFloat();
		final double rotationZY = (double)dataInput.readFloat();
		final double rotationXZ = (double)dataInput.readFloat();
		final double rotationYZ = (double)dataInput.readFloat();
		final double rotationZZ = (double)dataInput.readFloat();
		final double originX    = (double)dataInput.readFloat();
		final double originY    = (double)dataInput.readFloat();
		final double originZ    = (double)dataInput.readFloat();

		_transform = Matrix3D.INIT.set( rotationXX , rotationXY , rotationXZ , originX ,
		                                rotationYX , rotationYY , rotationYZ , originY ,
		                                rotationZX , rotationZY , rotationZZ , originZ );
	}

	private void readVertexCoordinates( final DataInput dataInput )
		throws IOException
	{
		final int numberOfVertices = dataInput.readUnsignedShort();

		final List<Vector3D> vertices = new ArrayList<Vector3D>( numberOfVertices );
		for ( int i = 0 ; i < numberOfVertices ; i++ )
		{
			vertices.add( new Vector3D( (double)dataInput.readFloat() , (double)dataInput.readFloat() , (double)dataInput.readFloat() ) );
		}

		_vertices = vertices;
	}

	private void readTextureCoordinates( final DataInput dataInput )
		throws IOException
	{
		final Point2D.Float[] textureCoordinates = new Point2D.Float[ dataInput.readUnsignedShort() ];

		for ( int i = 0; i < textureCoordinates.length; i++ )
		{
			textureCoordinates[ i ] = new Point2D.Float( dataInput.readFloat() , dataInput.readFloat() );
		}

		_textureCoordinates = textureCoordinates;
	}
}