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
package ab.j3d.appearance;

/**
 * Abstract implementation of the {@link TextureMap} interface.
 *
 * @author  Peter S. Heijnen
 * @version $Revision$ ($Date$, $Author$)
 */
public abstract class AbstractTextureMap
	implements TextureMap
{
	/**
	 * Physical width of map in meters (<code>0.0</code>0 if undeterminate).
	 */
	private float _physicalWidth;

	/**
	 * Physical height of map in meters (<code>0.0</code>0 if undeterminate).
	 */
	private float _physicalHeight;

	/**
	 * Construct map with undeterminate physical dimensions.
	 */
	protected AbstractTextureMap()
	{
		_physicalWidth = 0.0f;
		_physicalHeight = 0.0f;
	}

	/**
	 * Construct map.
	 *
	 * @param   physicalWidth   Physical width in meters (<code>0.0</code> if
	 *                          undeterminate).
	 * @param   physicalHeight  Physical height in meters (<code>0.0</code> if
	 *                          undeterminate).
	 */
	protected AbstractTextureMap( final float physicalWidth, final float physicalHeight )
	{
		_physicalWidth = physicalWidth;
		_physicalHeight = physicalHeight;
	}

	@Override
	public float getPhysicalWidth()
	{
		return _physicalWidth;
	}

	/**
	 * Set physical width of map in meters. If available, this can be used to
	 * correctly scale the map in a virtual environment.
	 *
	 * @param   physicalWidth   Physical width of map in meters;
	 *                          <code>0.0</code> if indeterminate.
	 */
	public void setPhysicalWidth( final float physicalWidth )
	{
		_physicalWidth = physicalWidth;
	}

	@Override
	public float getPhysicalHeight()
	{
		return _physicalHeight;
	}

	/**
	 * Set physical height of map in meters. If available, this can be used to
	 * correctly scale the map in a virtual environment.
	 *
	 * @param   physicalHeight  Physical height of map in meters;
	 *                          <code>0.0</code> if indeterminate.
	 */
	public void setPhysicalHeight( final float physicalHeight )
	{
		_physicalHeight = physicalHeight;
	}
}