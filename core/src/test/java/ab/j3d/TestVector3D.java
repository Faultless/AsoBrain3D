/*
 * AsoBrain 3D Toolkit
 * Copyright (C) 2006-2013 Peter S. Heijnen
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
package ab.j3d;

import java.util.*;

import junit.framework.*;

/**
 * This class tests the {@link Vector3D} class.
 *
 * @author Peter S. Heijnen
 */
public class TestVector3D
extends TestCase
{
	/**
	 * Name of this class.
	 */
	private static final String CLASS_NAME = TestVector3D.class.getName();

	/**
	 * Test {@link Vector3D#getProperty} method.
	 */
	public void testGetProperty()
	{
		System.out.println( CLASS_NAME + ".testGetSetVector()" );

		System.out.println( " - null-arguments" );
		{
			final Properties p = new Properties();
			final Vector3D v = Vector3D.getProperty( p, "vector" );
			assertNull( "get for default vector failed", v );
		}

		System.out.println( " - default values" );
		{
			final Properties p = new Properties();
			final Vector3D v = Vector3D.getProperty( p, "vector" );
			assertNull( "get for default vector failed", v );
		}

		System.out.println( " - test set" );
		final Vector3D[] testVectors =
		{
		null,
		Vector3D.ZERO,
		Vector3D.ZERO.set( 1.0, 2.0, 3.0 ),
		Vector3D.ZERO.set( 0.0, 0.0, 0.0 ),
		Vector3D.ZERO.set( 0.0, 0.0, 1.0 ),
		Vector3D.ZERO.set( 0.0, 1.0, 0.0 ),
		Vector3D.ZERO.set( 1.0, 0.0, 0.0 ),
		};

		for ( final Vector3D in : testVectors )
		{
			final Properties p = new Properties();
			p.setProperty( "vector", String.valueOf( in ) );

			final Vector3D out = Vector3D.getProperty( p, "vector" );

			assertEquals( "set/get for vector (" + in + ") failed", in, out );
		}
	}

	/**
	 * Test {@link Vector3D#isNonZero()} method.
	 */
	public void testIsNonZero()
	{
		System.out.println( CLASS_NAME + ".testIsNonZero" );

		assertFalse( "Bad result from isNonZero( 0.0, 0.0, 0.0 )", new Vector3D( 0.0, 0.0, 0.0 ).isNonZero() );
		assertTrue( "Bad result from isNonZero( 0.0, 0.0, 1.0 )", new Vector3D( 0.0, 0.0, 1.0 ).isNonZero() );
		assertFalse( "Bad result from isNonZero( 0.0, 0.0, NaN )", new Vector3D( 0.0, 0.0, Double.NaN ).isNonZero() );
		assertTrue( "Bad result from isNonZero( 0.0, 1.0, 0.0 )", new Vector3D( 0.0, 1.0, 0.0 ).isNonZero() );
		assertTrue( "Bad result from isNonZero( 0.0, 1.0, 1.0 )", new Vector3D( 0.0, 1.0, 1.0 ).isNonZero() );
		assertFalse( "Bad result from isNonZero( 0.0, 1.0, NaN )", new Vector3D( 0.0, 1.0, Double.NaN ).isNonZero() );
		assertFalse( "Bad result from isNonZero( 0.0, NaN, 0.0 )", new Vector3D( 0.0, Double.NaN, 0.0 ).isNonZero() );
		assertFalse( "Bad result from isNonZero( 0.0, NaN, 1.0 )", new Vector3D( 0.0, Double.NaN, 1.0 ).isNonZero() );
		assertFalse( "Bad result from isNonZero( 0.0, NaN, NaN )", new Vector3D( 0.0, Double.NaN, Double.NaN ).isNonZero() );
		assertTrue( "Bad result from isNonZero( 1.0, 0.0, 0.0 )", new Vector3D( 1.0, 0.0, 0.0 ).isNonZero() );
		assertTrue( "Bad result from isNonZero( 1.0, 0.0, 1.0 )", new Vector3D( 1.0, 0.0, 1.0 ).isNonZero() );
		assertFalse( "Bad result from isNonZero( 1.0, 0.0, NaN )", new Vector3D( 1.0, 0.0, Double.NaN ).isNonZero() );
		assertTrue( "Bad result from isNonZero( 1.0, 1.0, 0.0 )", new Vector3D( 1.0, 1.0, 0.0 ).isNonZero() );
		assertTrue( "Bad result from isNonZero( 1.0, 1.0, 1.0 )", new Vector3D( 1.0, 1.0, 1.0 ).isNonZero() );
		assertFalse( "Bad result from isNonZero( 1.0, 1.0, NaN )", new Vector3D( 1.0, 1.0, Double.NaN ).isNonZero() );
		assertFalse( "Bad result from isNonZero( 1.0, NaN, 0.0 )", new Vector3D( 1.0, Double.NaN, 0.0 ).isNonZero() );
		assertFalse( "Bad result from isNonZero( 1.0, NaN, 1.0 )", new Vector3D( 1.0, Double.NaN, 1.0 ).isNonZero() );
		assertFalse( "Bad result from isNonZero( 1.0, NaN, NaN )", new Vector3D( 1.0, Double.NaN, Double.NaN ).isNonZero() );
		assertFalse( "Bad result from isNonZero( NaN, 0.0, 0.0 )", new Vector3D( Double.NaN, 0.0, 0.0 ).isNonZero() );
		assertFalse( "Bad result from isNonZero( NaN, 0.0, 1.0 )", new Vector3D( Double.NaN, 0.0, 1.0 ).isNonZero() );
		assertFalse( "Bad result from isNonZero( NaN, 0.0, NaN )", new Vector3D( Double.NaN, 0.0, Double.NaN ).isNonZero() );
		assertFalse( "Bad result from isNonZero( NaN, 1.0, 0.0 )", new Vector3D( Double.NaN, 1.0, 0.0 ).isNonZero() );
		assertFalse( "Bad result from isNonZero( NaN, 1.0, 1.0 )", new Vector3D( Double.NaN, 1.0, 1.0 ).isNonZero() );
		assertFalse( "Bad result from isNonZero( NaN, 1.0, NaN )", new Vector3D( Double.NaN, 1.0, Double.NaN ).isNonZero() );
		assertFalse( "Bad result from isNonZero( NaN, NaN, 0.0 )", new Vector3D( Double.NaN, Double.NaN, 0.0 ).isNonZero() );
		assertFalse( "Bad result from isNonZero( NaN, NaN, 1.0 )", new Vector3D( Double.NaN, Double.NaN, 1.0 ).isNonZero() );
		assertFalse( "Bad result from isNonZero( NaN, NaN, NaN )", new Vector3D( Double.NaN, Double.NaN, Double.NaN ).isNonZero() );
	}
}
