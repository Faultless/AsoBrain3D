/* $Id$
 * ====================================================================
 * AsoBrain 3D Toolkit
 * Copyright (C) 1999-2007 Peter S. Heijnen
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
package ab.j3d;

import java.awt.Image;

import com.numdata.oss.TextTools;
import com.numdata.oss.ui.ImageTools;

/**
 * This class defines a map to be using in a 3D environment.
 *
 * @author  Peter S. Heijnen
 * @version $Revision$ ($Date$, $Author$)
 */
public final class MapTools
{
	/**
	 * Map path prefix from where material map images are loaded.
	 */
	public static String imageMapDirectory = "maps";

	/**
	 * Map path suffix from where material map images are loaded.
	 */
	public static String imageMapFilenameSuffix = ".jpg";

	/**
	 * Get <code>Image</code> instance with map image.
	 *
	 * @return  Map image;
	 *          <code>null</code> if map has no image or the image could not be loaded.
	 */
	public static Image getImage( final String map )
	{
		return TextTools.isNonEmpty( map ) ? ImageTools.getImage( imageMapDirectory + '/' + map + imageMapFilenameSuffix ) : null;
	}
}