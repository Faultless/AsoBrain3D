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
package ab.j3d.view.jogl;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.media.opengl.GL;
import javax.media.opengl.GLException;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

import ab.j3d.MapTools;
import ab.j3d.Material;

import com.numdata.oss.MathTools;
import com.numdata.oss.TextTools;
import com.numdata.oss.ui.ImageTools;

/**
 * Provides loading and caching of textures for JOGL-based rendering.
 *
 * @author  G. Meinders
 * @version $Revision$ $Date$
 */
public class TextureCache
{
	/**
	 * Texture cache key for the normalization cube map, used for DOT3 bump
	 * mapping.
	 */
	public static final String NORMALIZATION_CUBE_MAP = "__normalizationCubeMap";

	/**
	 * Cached textures, by name.
	 */
	private final Map<String,Texture> _textures;

	/**
	 * Set of textures, by name, with an alpha channel.
	 */
	private final Set<String> _alpha;

	/**
	 * Construct new texture cache.
	 */
	public TextureCache()
	{
		_textures = new HashMap<String,Texture>();
		_alpha = new HashSet<String>();
	}

	/**
	 * Removes all cached textures from the cache.
	 */
	public void clear()
	{
		_textures.clear();
		_alpha.clear();
	}

	/**
	 * Returns whether the specified texture has an alpha channel.
	 *
	 * @param   texture     Name of the texture.
	 *
	 * @return  <code>true</code> if the texture has an alpha channel.
	 */
	public boolean hasAlpha( final String texture )
	{
		return _alpha.contains( texture );
	}

	/**
	 * Get {@link Texture} for the specified map.
	 *
	 * @param   gl          OpenGL pipeline.
	 * @param   texture     Name of the texture map.
	 *
	 * @return  Texture for the specified name; <code>null</code> if the name was
	 *          empty or no map by the given name was found.
	 */
	public Texture getTexture( final GL gl , final String texture )
	{
		Texture result = null;

		if ( TextTools.isNonEmpty( texture ) )
		{
			final Map<String, Texture> textures = _textures;
			result = textures.get( texture );

			if ( result == null && !textures.containsKey( texture ) )
			{
				final BufferedImage bufferedImage = MapTools.loadImage( texture );
				if ( bufferedImage != null )
				{
					final boolean autoMipmapGeneration = hasAutoMipMapGenerationSupport( gl );

					result = TextureIO.newTexture( createCompatibleTextureImage( bufferedImage , gl ) , autoMipmapGeneration );

					result.setTexParameteri( GL.GL_TEXTURE_WRAP_S , GL.GL_REPEAT );
					result.setTexParameteri( GL.GL_TEXTURE_WRAP_T , GL.GL_REPEAT );

					if ( autoMipmapGeneration )
					{
						try
						{
							/**
							 * Set generate mipmaps to true, this greatly increases performance and viewing pleasure in big scenes.
							 * @TODO need to find out if generated mipmaps are faster or if pregenerated mipmaps are faster
							 */
							result.setTexParameteri( GL.GL_GENERATE_MIPMAP , GL.GL_TRUE );

							/** Set texture magnification to GL_LINEAR to support mipmaps. */
							result.setTexParameteri( GL.GL_TEXTURE_MAG_FILTER , GL.GL_LINEAR );

							/** Set texture minification to GL_LINEAR_MIPMAP_NEAREST to support mipmaps. */
							result.setTexParameteri( GL.GL_TEXTURE_MIN_FILTER , GL.GL_LINEAR_MIPMAP_NEAREST );
						}
						catch ( GLException e )
						{
							/*
							 * If setting texture parameters fails, it's no
							 * severe problem. Catch any exception so the view
							 * doesn't crash.
							 */
							e.printStackTrace();
						}
					}

					final ColorModel colorModel = bufferedImage.getColorModel();
					if ( colorModel.hasAlpha() )
					{
						_alpha.add( texture );
					}
				}
			}

			_textures.put( texture , result );
		}

		return result;
	}

	/**
	 * Test if OpenGL supports auto-generated mipmaps.
	 *
	 * @param   gl  OpenGL pipeline.
	 *
	 * @return  <code>true</code> if OpenGL supports auto-generated mipmaps;
	 *          <code>false</code> otherwise.
	 */
	private static boolean hasAutoMipMapGenerationSupport( final GL gl )
	{
		return ( gl.isExtensionAvailable( "GL_VERSION_1_4" ) || gl.isExtensionAvailable( "GL_SGIS_generate_mipmap" ) );
	}

	/**
	 * Scales the given image, if necessary, such that it is compatible with the
	 * given OpenGL pipeline. The aspect ratio of the image may not be preserved.
	 *
	 * @param   image   Image to be scaled, if necessary.
	 * @param   gl      OpenGL pipeline.
	 *
	 * @return  Compatible texture image. If the given image already meets all
	 *          requirements, that same image is returned.
	 *
	 * @throws  IllegalStateException if the given OpenGL pipeline specifies a
	 *          non-positive maximum texture size.
	 */
	private static BufferedImage createCompatibleTextureImage( final BufferedImage image , final GL gl )
	{
		/*
		 * Textures must not exceed the maximum size. According to the OpenGL
		 * specification, this must be at least 64.
		 */
		final int[] maxTextureSizeBuffer = new int[ 1 ];
		gl.glGetIntegerv( GL.GL_MAX_TEXTURE_SIZE , maxTextureSizeBuffer , 0 );
		final int maximumTextureSize = Math.max( 64 , maxTextureSizeBuffer[ 0 ] );

		int scaledWidth  = Math.min( maximumTextureSize , image.getWidth()  );
		int scaledHeight = Math.min( maximumTextureSize , image.getHeight() );

		/*
		 * Texture sizes may need to be powers of two.
		 */
		if ( !gl.isExtensionAvailable( "GL_ARB_texture_non_power_of_two" ) )
		{
			scaledWidth  = MathTools.nearestPowerOfTwo( scaledWidth  );
			scaledHeight = MathTools.nearestPowerOfTwo( scaledHeight );
		}

		return ImageTools.createScaledInstance( image , scaledWidth , scaledHeight , false );
	}

	/**
	 * Get {@link Texture} for color map of {@link Material}.
	 *
	 * @param   gl              OpenGL pipeline.
	 * @param   material        Material to get color map texture from.
	 *
	 * @return Color map texture; <code>null</code> if face has no color map or no
	 *         texture coordinates.
	 */
	public Texture getColorMapTexture( final GL gl , final Material material )
	{
		final Texture result;

		if ( ( material != null ) && ( material.colorMap != null ) )
		{
			result = getTexture( gl , material.colorMap );
		}
		else
		{
			result = null;
		}
		return result;
	}

	/**
	 * Get {@link Texture} for bump map of {@link Material}.
	 *
	 * @param   gl          OpenGL pipeline.
	 * @param   material    MAterial to get bump map texture from.
	 *
	 * @return Color map texture; <code>null</code> if face has no color map or no
	 *         texture coordinates.
	 */
	public Texture getBumpMapTexture( final GL gl , final Material material )
	{
		Texture result = null;

		if ( ( material != null ) && TextTools.isNonEmpty( material.bumpMap ) )
		{
			final Map<String, Texture> textures = _textures;
			result = textures.get( material.bumpMap );

			if ( result == null )
			{
				BufferedImage bufferedImage = MapTools.loadImage( material.bumpMap );
				if ( bufferedImage != null )
				{
					bufferedImage = JOGLTools.createNormalMapFromBumpMap( bufferedImage );

					final boolean autoMipmapGeneration = hasAutoMipMapGenerationSupport( gl );

					result = TextureIO.newTexture( createCompatibleTextureImage( bufferedImage , gl ) , autoMipmapGeneration );

					result.setTexParameteri( GL.GL_TEXTURE_WRAP_S , GL.GL_REPEAT );
					result.setTexParameteri( GL.GL_TEXTURE_WRAP_T , GL.GL_REPEAT );

					if ( autoMipmapGeneration )
					{
						try
						{
							/**
							 * Set generate mipmaps to true, this greatly increases performance and viewing pleasure in big scenes.
							 * @TODO need to find out if generated mipmaps are faster or if pregenerated mipmaps are faster
							 */
							result.setTexParameteri( GL.GL_GENERATE_MIPMAP , GL.GL_TRUE );

							/** Set texture magnification to linear to support mipmaps. */
							result.setTexParameteri( GL.GL_TEXTURE_MAG_FILTER , GL.GL_LINEAR );

							/** Set texture minification to linear_mipmap)_nearest to support mipmaps */
							result.setTexParameteri( GL.GL_TEXTURE_MIN_FILTER , GL.GL_LINEAR_MIPMAP_NEAREST );
						}
						catch ( GLException e )
						{
							/*
							 * If setting texture parameters fails, it's no
							 * severe problem. Catch any exception so the view
							 * doesn't crash.
							 */
							e.printStackTrace();
						}
					}
				}

				textures.put( material.bumpMap , result );
			}
		}

		return result;
	}

	/**
	 * Get normalization cube map, used to perform DOT3 bump mapping. For each
	 * 3D texture coordinate, the value of the map represents the normalized
	 * vector from the origin in the direction of the coordinate.
	 *
	 * @param   gl  OpenGL pipeline.
	 *
	 * @return  Normalization cube map.
	 */
	public Texture getNormalizationCubeMap( final GL gl )
	{
		Texture result = _textures.get( NORMALIZATION_CUBE_MAP );
		if ( result == null )
		{
			result = JOGLTools.createNormalizationCubeMap( gl );
			_textures.put( NORMALIZATION_CUBE_MAP , result );
		}
		return result;
	}
}