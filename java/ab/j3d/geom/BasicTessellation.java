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
package ab.j3d.geom;

import java.util.*;

import ab.j3d.*;
import org.jetbrains.annotations.*;

/**
 * Basic {@link Tessellation} implementation.
 *
 * @author  Peter S. Heijnen
 * @version $Revision$ ($Date$, $Author$)
 */
public class BasicTessellation
	implements Tessellation
{
	/**
	 * Primitives that the tessellation consists of.
	 */
	@NotNull
	protected final List<TessellationPrimitive> _primitives;

	/**
	 * Primitives that the tessellation consists of.
	 */
	protected List<int[]> _triangles;

	/**
	 * Outlines of tessellated shapes.
	 */
	private final List<int[]> _outlines;

	/**
	 * Vertices used in this tessellation.
	 */
	private final List<? extends Vector3D> _vertices;

	/**
	 * Constructs a new tessellation.
	 * <dl>
	 *  <dt><strong>WARNING</strong></dt>
	 *  <dd>The supplied collections are used as-is for efficiency. Therefore,
	 *    changes to the collections are not isolated from this object!</dd>
	 * </dl>
	 *
	 *
	 * @param   vertices    Vertices used in the tessellation.
	 * @param   outlines    Outlines of tessellated shapes.
	 * @param   primitives  Primitives that define the tessellation.
	 */
	@SuppressWarnings( { "AssignmentToCollectionOrArrayFieldFromParameter" } )
	public BasicTessellation( @NotNull final List<? extends Vector3D> vertices, @NotNull final List<int[]> outlines, @NotNull final List<TessellationPrimitive> primitives )
	{
		_outlines = outlines;
		_vertices = vertices;
		_primitives = primitives;
	}

	@NotNull
	@Override
	public List<Vector3D> getVertices()
	{
		return Collections.unmodifiableList( _vertices );
	}

	@NotNull
	@Override
	public List<int[]> getOutlines()
	{
		return _outlines;
	}

	@NotNull
	@Override
	public Collection<TessellationPrimitive> getPrimitives()
	{
		return Collections.unmodifiableList( _primitives );
	}

}