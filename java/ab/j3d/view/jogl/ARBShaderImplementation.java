/* ====================================================================
 * $Id$
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
package ab.j3d.view.jogl;

/**
 * Provides OpenGL Shading Language (GLSL) shaders using the ARB extension.
 * As such, it may be supported on OpenGL versions 1.4 and above.
 *
 * @author  G. Meinders
 * @version $Revision$ $Date$
 */
public class ARBShaderImplementation
	implements ShaderImplementation
{
	public Shader createShader( final Shader.Type type )
	{
		return new ARBShader( type );
	}

	public ShaderProgram createProgram( final String name )
	{
		return new ARBShaderProgram( name );
	}
}