package ab.j3d.geom;

import java.util.*;

import org.jetbrains.annotations.*;

/**
 * A triangle fan is a series of connected triangles with a central vertex.
 * Vertices 0, 1, 2 define the first triangle; vertices 0, 2, 3 define the
 * second triangle; then 0, 3, 4, etc.
 *
 * @author  Peter S. Heijnen
 * @version $Revision$ $Date$
 */
public class TriangleFan
	implements TessellationPrimitive
{
	/**
	 * Empty triangle array.
	 */
	private static final int[] NO_TRIANGLES = new int[ 0 ];

	/**
	 * Vertices that define the triangle fan.
	 */
	@NotNull
	protected final int[] _vertices;

	/**
	 * Cached triangles.
	 */
	private int[] _triangles;

	/**
	 * Construct triangle fan.
	 *
	 * @param   vertices    Vertices that define the triangle fan.
	 */
	public TriangleFan( @NotNull final int[] vertices )
	{
		//noinspection AssignmentToCollectionOrArrayFieldFromParameter
		_vertices = vertices;
		_triangles = null;
	}

	@NotNull
	@Override
	public int[] getVertices()
	{
		//noinspection ReturnOfCollectionOrArrayField
		return _vertices;
	}

	@NotNull
	@Override
	public int[] getTriangles()
	{
		int[] result = _triangles;
		if ( result == null )
		{
			final int[] vertices = _vertices;
			final int vertexCount = vertices.length;
			if ( vertexCount < 3 )
			{
				result = NO_TRIANGLES;
			}
			else
			{
				/*
				 * result[ 0 ] = { vertices[0], vertices[1], vertices[2] }
				 * result[ 1 ] = { vertices[0], vertices[2], vertices[3] }
				 * result[ 2 ] = { vertices[0], vertices[3], vertices[4] }
				 * result[ 3 ] = { vertices[0], vertices[4], vertices[5] }
				 * result[ 4 ] = { vertices[0], vertices[5], vertices[6] }
				*/
				final int v0 = vertices[ 0 ];
				int v1 = vertices[ 1 ];

				result = new int[ ( vertexCount - 2 ) * 3 ];
				int resultIndex = 0;

				for ( int vertexIndex = 2; vertexIndex < vertexCount; vertexIndex++ )
				{
					final int v2 = vertices[ vertexIndex ];
					result[ resultIndex++ ] = v0;
					result[ resultIndex++ ] = v1;
					result[ resultIndex++ ] = v2;
					v1 = v2;
				}
			}

			_triangles = result;
		}

		return result;
	}

	@Override
	public String toString()
	{
		return super.toString() + "{vertices=" + Arrays.toString( _vertices ) + '}';
	}
}