/* $Id$
 * ====================================================================
 * (C) Copyright Numdata BV 2007-2009
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

import junit.framework.TestCase;

/**
 * Unit test for the {@link GLUTriangulator}.
 *
 * @author  G. Meinders
 * @version $Revision$ $Date$
 */
public class TestGLUTriangulator
	extends TestCase
{
	public void testTriangulation()
	{
		final GLUTriangulator triangulator = new GLUTriangulator();
		TriangulatorTester.testExtremeShapes( triangulator );
	}
}