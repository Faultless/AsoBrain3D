/* $Id$
 * ====================================================================
 * AsoBrain 3D Toolkit
 * Copyright (C) 1999-2010 Peter S. Heijnen
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

import java.awt.*;
import javax.media.opengl.*;

import ab.j3d.*;
import ab.j3d.view.*;

/**
 * Handles OpenGL state changes that are complicated or may need to be cached.
 * This implementation performs no optimizations such as caching.
 *
 * @author  G. Meinders
 * @version $Revision$ $Date$
 */
public class GLStateHelper
{
	/**
	 * OpenGL interface.
	 */
	protected final GL _gl;

	/**
	 * Constructs a new instance.
	 *
	 * @param   gl  OpenGL interface.
	 */
	public GLStateHelper( final GL gl )
	{
		_gl = gl;
	}

	/**
	 * Set GL material properties.
	 *
	 * @param   color   Color to set.
	 */
	public final void setColor( final Color color )
	{
		final float[] rgba  = color.getRGBComponents( null );
		final float   red   = rgba[ 0 ];
		final float   green = rgba[ 1 ];
		final float   blue  = rgba[ 2 ];
		final float   alpha = rgba[ 3 ];

		setColor( red , green , blue , alpha );
	}

	/**
	 * Sets the current color and material to the given color.
	 *
	 * @param   red     Red component.
	 * @param   green   Green component.
	 * @param   blue    Blue component.
	 * @param   alpha   Alpha component.
	 */
	public void setColor( final float red , final float green , final float blue , final float alpha )
	{
		_gl.glColor4f( red, green, blue, alpha );
	}

	/**
	 * Sets GL material properties calculated from the given material,
	 * render style and alpha.
	 *
	 * @param   material    {@link Material} properties.
	 * @param   style       Render style to be applied.
	 * @param   extraAlpha  Extra alpha multiplier.
	 */
	public final void setMaterial( final Material material, final RenderStyle style, final float extraAlpha )
	{
		final float red;
		final float green;
		final float blue;
		final float alpha;

		if ( style.isFillEnabled() )
		{
			final Color materialDiffuse = new Color( material.diffuseColorRed , material.diffuseColorGreen , material.diffuseColorBlue , extraAlpha * material.diffuseColorAlpha );
			final Color diffuse = RenderStyle.blendColors( style.getFillColor() , materialDiffuse );
			final float[] diffuseComponents = diffuse.getRGBComponents( null );

			red   = diffuseComponents[ 0 ];
			green = diffuseComponents[ 1 ];
			blue  = diffuseComponents[ 2 ];
			alpha = diffuseComponents[ 3 ];
		}
		else
		{
			red   = material.diffuseColorRed;
			green = material.diffuseColorGreen;
			blue  = material.diffuseColorBlue;
			alpha = material.diffuseColorAlpha * extraAlpha;
		}

		setMaterial( material, red, green, blue, alpha );
	}

	/**
	 * Sets OpenGL material properties.
	 *
	 * @param   material    Material properties to be set.
	 * @param   red         Red component of the diffuse color to be set.
	 * @param   green       Green component of the diffuse color to be set.
	 * @param   blue        Blue component of the diffuse color to be set.
	 * @param   alpha       Alpha component to be set.
	 */
	protected void setMaterial( final Material material, final float red, final float green, final float blue, final float alpha )
	{
		final GL gl = _gl;
		gl.glColor4f( red, green, blue, alpha );
		gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT , new float[] { material.ambientColorRed , material.ambientColorGreen , material.ambientColorBlue , alpha }, 0 );
		gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE , new float[] { red, green, blue, alpha }, 0 );
		gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, new float[] { material.specularColorRed , material.specularColorGreen , material.specularColorBlue , alpha }, 0 );
		gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL.GL_EMISSION, new float[] { material.emissiveColorRed , material.emissiveColorGreen , material.emissiveColorBlue , alpha }, 0 );
		gl.glMaterialf( GL.GL_FRONT_AND_BACK, GL.GL_SHININESS, (float)material.shininess );
	}

	/**
	 * Enables or disables the specified capability.
	 *
	 * @param   capability  Capability to be enabled or disabled.
	 * @param   enabled     <code>true</code> to enable the capability.
	 */
	public void setEnabled( final int capability, final boolean enabled )
	{
		if ( enabled )
		{
			_gl.glEnable( capability );
		}
		else
		{
			_gl.glDisable( capability );
		}
	}

	/**
	 * Sets the blend function for pixel arithmetic.
	 *
	 * @param   source          Source factor.
	 * @param   destination     Destination factory.
	 */
	public void setBlendFunc( final int source, final int destination ) // 5%
	{
		_gl.glBlendFunc( source, destination );
	}
}