/* ====================================================================
 * $Id$
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
package ab.j3d.awt.view.jogl;

import java.nio.charset.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;

/**
 * Represents an OpenGL Shading Language (GLSL) shader object. This
 * implementation uses the ARB extension for GLSL. As such, it may be supported
 * on OpenGL versions 1.4 and above.
 *
 * @author  G. Meinders
 * @version $Revision$ $Date$
 */
public class ARBShader
	implements Shader
{
	/**
	 * Shader object.
	 */
	protected final int _shader;

	/**
	 * Constructs a new shader of the given type.
	 *
	 * @param   shaderType  Type of shader.
	 */
	public ARBShader( final Type shaderType )
	{
		if ( shaderType == null )
		{
			throw new NullPointerException( "shaderType" );
		}

		final GL gl = GLU.getCurrentGL();

		final int shader;
		switch ( shaderType )
		{
			default:
			case VERTEX:   shader = gl.glCreateShaderObjectARB( GL.GL_VERTEX_SHADER_ARB );   break;
			case FRAGMENT: shader = gl.glCreateShaderObjectARB( GL.GL_FRAGMENT_SHADER_ARB ); break;
		}
		_shader = shader;
	}

	public int getShaderObject()
	{
		return _shader;
	}

	public void setSource( final String... source )
	{
		final int[] length = new int[ source.length ];
		for ( int i = 0 ; i < source.length ; i++ )
		{
			length[ i ] = source[ i ].length();
		}

		final GL gl = GLU.getCurrentGL();

		final int shader = _shader;
		gl.glShaderSourceARB( shader, source.length , source , length , 0 );

		compile();
	}

	/**
	 * Compiles the shader.
	 *
	 * @throws  GLException if compilation fails.
	 */
	private void compile()
	{
		final GL gl = GLU.getCurrentGL();
		final int shader = _shader;

		gl.glCompileShaderARB( shader );

		final int[] compileStatus = new int[ 1 ];
		gl.glGetObjectParameterivARB( shader , GL.GL_OBJECT_COMPILE_STATUS_ARB , compileStatus , 0 );

		if ( compileStatus[ 0 ] == GL.GL_FALSE )
		{
			final int[] infoLogLength = new int[ 1 ];
			gl.glGetObjectParameterivARB( shader , GL.GL_OBJECT_INFO_LOG_LENGTH_ARB , infoLogLength , 0 );

			final String message;
			if( infoLogLength[ 0 ] == 0 )
			{
				message = "Vertex shader failed to compile.";
			}
			else
			{
				final byte[] infoLog = new byte[ infoLogLength[ 0 ] ];
				gl.glGetInfoLogARB( shader , infoLogLength[ 0 ] , infoLogLength , 0 , infoLog , 0 );
				message = new String( infoLog , 0 , infoLogLength[ 0 ] , Charset.forName( "iso-8859-1" ) );
			}

			throw new GLException( message );
		}
	}

	public void dispose()
	{
		final GL gl = GLU.getCurrentGL();
		gl.glDeleteObjectARB( _shader );
	}
}