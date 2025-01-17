/*
 * AsoBrain 3D Toolkit
 * Copyright (C) 1999-2019 Peter S. Heijnen
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
 */
package ab.j3d.awt.view.jogl;

import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;

/**
 * Provides information about the number of samples rendered.
 *
 * @author G. Meinders
 */
public class OcclusionQuery
{
	/**
	 * OpenGL query object.
	 */
	private int _object;

	/**
	 * Constructs a new occlusion query for the current GL context. The query
	 * begins immediately.
	 */
	public OcclusionQuery()
	{
		final GL gl = GLU.getCurrentGL();
		final GL2 gl2 = gl.getGL2();

		final int[] object = new int[ 1 ];
		gl2.glGenQueries( object.length , object , 0 );
		_object = object[ 0 ];

		gl2.glBeginQuery( GL2GL3.GL_SAMPLES_PASSED , object[ 0 ] );
	}

	/**
	 * Returns the number of samples that were rendered while the occlusion was
	 * active. The query is ended in the process and cannot be used afterwards.
	 *
	 * @return  Number of samples.
	 */
	public int getSampleCount()
	{
		final GL gl = GLU.getCurrentGL();
		final GL2 gl2 = gl.getGL2();

		gl2.glEndQuery( GL2GL3.GL_SAMPLES_PASSED );

		final int[] sampleCount = new int[ 1 ];
		gl2.glGetQueryObjectiv( _object , GL2ES2.GL_QUERY_RESULT , sampleCount , 0 );
		gl2.glDeleteQueries( 1 , new int[ _object ] , 0 );

		return sampleCount[ 0 ];
	}
}
