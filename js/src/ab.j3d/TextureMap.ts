/*
 * AsoBrain 3D Toolkit
 * Copyright (C) 1999-2021 Peter S. Heijnen
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

/**
 * This interface defines a texture map.
 *
 * @author  Peter S. Heijnen
 */
export default interface TextureMap
{
	/**
	 * Name of the texture.
	 */
	name: string;

	url: string;

	/**
	 * Physical width of map in meters. If available, this can be used to
	 * correctly scale the map in a virtual environment. <code>0.0</code> if
	 * indeterminate.
	 */
	physicalWidth: number;

	/**
	 * Physical height of map in meters. If available, this can be used to
	 * correctly scale the map in a virtual environment. <code>0.0</code> if
	 * indeterminate.
	 */
	physicalHeight: number;
};