/* $Id$
 * ====================================================================
 * AsoBrain 3D Toolkit
 * Copyright (C) 2007-2007 Peter S. Heijnen
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
package ab.j3d.model;

import java.util.List;

import com.photoneffect.coldet.JCollisionModel3D;

import ab.j3d.Bounds3D;
import ab.j3d.Matrix3D;
import ab.j3d.Vector3D;

/**
 * This class performs collision tests on behalf of a {@link Object3D}.
 *
 * @see     Object3D#collidesWith(Matrix3D, Object3D)
 *
 * @author  Peter S. Heijnen
 * @version $Revision$ ($Date$, $Author$)
 */
class CollisionTester
{
	/**
	 * Object on behalf of which collision tests are performed.
	 */
	private Object3D _owner;

	/**
	 * Radius of bounding sphere.
	 *
	 * @see     #getBoundingSphereRadius()
	 */
	private double _boundingSphereRadius;

	/**
	 * Center-point of object.
	 *
	 * @see     #getCenterPoint()
	 */
	private Vector3D _centerPoint;

	/**
	 * Collision model.
	 *
	 * @see     #getCollisionModel()
	 */
	private JCollisionModel3D _collisionModel;

	/**
	 * Bounding box of object in the local coordinate system.
	 *
	 * @see     #getOrientedBoundingBox()
	 */
	private Bounds3D _orientedBoundingBox;

	/**
	 * Construct collision tester for the specified object.
	 */
	CollisionTester( final Object3D owner )
	{
		_owner = owner;

		_boundingSphereRadius = Double.NaN;
		_centerPoint          = null;
		_collisionModel       = null;
		_orientedBoundingBox  = null;
	}

	/**
	 * Invalidate all cached data.
	 */
	void invalidate()
	{
		_boundingSphereRadius = Double.NaN;
		_centerPoint          = null;
		_collisionModel       = null;
		_orientedBoundingBox  = null;
	}

	/**
	 * Test if this object collides with another.
	 *
	 * @param   fromOtherToThis     Transformation from other object to this.
	 * @param   other               Object to test collision with.
	 *
	 * @return  <code>true</code> if the objects collide;
	 *          <code>false</code> otherwise.
	 */
	public boolean testCollision( final Matrix3D fromOtherToThis, final CollisionTester other )
	{
		synchronized ( this )
		{
			synchronized ( other )
			{
				return testBoundingSphere( fromOtherToThis, other ) &&
				       testOrientedBoundingBox( fromOtherToThis, other ) &&
				       testColdet( fromOtherToThis, other );
			}
		}
	}

	/**
	 * Get radius of bounding sphere.
	 *
	 * @return  Radius of bounding sphere.
	 */
	public final double getBoundingSphereRadius()
	{
		double result = _boundingSphereRadius;
		if ( Double.isNaN( result ) )
		{
			final Vector3D centerPoint = getCenterPoint();
			final double   centerX     = centerPoint.x;
			final double   centerY     = centerPoint.y;
			final double   centerZ     = centerPoint.z;

			double squaredRadius = 0.0;

			final double[] vertexCoordinates = _owner._vertexCoordinates;
			for ( int i = 0 ; i < vertexCoordinates.length ; i += 3 )
			{
				final double relativeX = vertexCoordinates[ i     ] - centerX;
				final double relativeY = vertexCoordinates[ i + 1 ] - centerY;
				final double relativeZ = vertexCoordinates[ i + 2 ] - centerZ;

				squaredRadius = Math.max( squaredRadius , relativeX * relativeX + relativeY * relativeY + relativeZ * relativeZ );
			}

			result = Math.sqrt( squaredRadius );
			_boundingSphereRadius = result;
		}

		return result;
	}

	/**
	 * Get center point of object. Currently, this is the center of the
	 * bounding box of the object.
	 */
	public final Vector3D getCenterPoint()
	{
		Vector3D result = _centerPoint;
		if ( result == null )
		{
			final Bounds3D bounds = getOrientedBoundingBox();
			result = bounds.center();

			_centerPoint = result;
		}

		return result;
	}

	/**
	 * This method returns a collision model for this object that can be used
	 * for exact collision tests. This is a heavy model and has a considerable
	 * cost, so it should only be used as narrow phase test.
	 *
	 * @return  Collision model.
	 */
	private JCollisionModel3D getCollisionModel()
	{
		JCollisionModel3D result = _collisionModel;
		if ( result == null )
		{
			final double[]     vertexCoordinates = _owner._vertexCoordinates;
			final List<Face3D> faces             = _owner._faces;

			int nrTriangles = 0;

			for ( final Face3D face : faces )
			{
				final int[] triangles = face.triangulate();
				if ( triangles != null )
					nrTriangles += triangles.length / 3;
			}

			result = new JCollisionModel3D( nrTriangles );

			for ( final Face3D face : faces )
			{
				final int[] triangles = face.triangulate();
				if ( triangles != null )
				{
					final int[] vertexIndices = face.getVertexIndices();

					for ( int triangleIndex = 0 ; triangleIndex < triangles.length ; triangleIndex += 3 )
					{
						final int vi1 = vertexIndices[ triangles[ triangleIndex     ] ] * 3;
						final int vi2 = vertexIndices[ triangles[ triangleIndex + 1 ] ] * 3;
						final int vi3 = vertexIndices[ triangles[ triangleIndex + 2 ] ] * 3;

						result.addTriangle(
							(float)vertexCoordinates[ vi1     ] ,
							(float)vertexCoordinates[ vi1 + 1 ] ,
							(float)vertexCoordinates[ vi1 + 2 ] ,
							(float)vertexCoordinates[ vi2     ] ,
							(float)vertexCoordinates[ vi2 + 1 ] ,
							(float)vertexCoordinates[ vi2 + 2 ] ,
							(float)vertexCoordinates[ vi3     ] ,
							(float)vertexCoordinates[ vi3 + 1 ] ,
							(float)vertexCoordinates[ vi3 + 2 ] );
					}
				}
			}

			result.initEnd();

			_collisionModel = result;
		}

		return result;
	}

	/**
	 * Get bound box of this object in the object coordinate system (OCS).
	 */
	public final Bounds3D getOrientedBoundingBox()
	{
		Bounds3D result = _orientedBoundingBox;
		if ( result == null )
		{
			result = _owner.getBounds( null , null );
			_orientedBoundingBox = result;
		}

		return result;
	}

	/**
	 * Test bounding sphere intersection.
	 *
	 * @param   fromOtherToThis     Transformation from other object to this.
	 * @param   other               Object to test collision with.
	 *
	 * @return  <code>true</code> if the objects intersect;
	 *          <code>false</code> otherwise.
	 */
	private boolean testBoundingSphere( final Matrix3D fromOtherToThis , final CollisionTester other )
	{
		return testBoundingSphere( getCenterPoint() , getBoundingSphereRadius() , fromOtherToThis , other.getCenterPoint() , other.getBoundingSphereRadius() );
	}

	/**
	 * Test bounding sphere intersection.
	 *
	 * @param   center1     Center of sphere #1.
	 * @param   radius1     Radius of sphere #2.
	 * @param   from2to1    Transformation from sphere #2 to sphere #1.
	 * @param   center2     Center of sphere #2.
	 * @param   radius2     Radius of sphere #2.
	 *
	 * @return  <code>true</code> if the bounding spheres intersect;
	 *          <code>false</code> otherwise.
	 */
	private static boolean testBoundingSphere( final Vector3D center1 , final double radius1 , final Matrix3D from2to1 , final Vector3D center2 , final double radius2 )
	{
		final double maxDistance = radius1 + radius2;

		final double dx = from2to1.transformX( center2 ) - center1.x;
		final double dy = from2to1.transformY( center2 ) - center1.y;
		final double dz = from2to1.transformZ( center2 ) - center1.z;

		return ( dx * dx + dy * dy + dz * dz ) < ( maxDistance * maxDistance );
	}

	/**
	 * Test oriented bounding box (OBB) intersection.
	 *
	 * @param   fromOtherToThis     Transformation from other object to this.
	 * @param   other               Object to test collision with.
	 *
	 * @return  <code>true</code> if the objects intersect;
	 *          <code>false</code> otherwise.
	 */
	private boolean testOrientedBoundingBox( final Matrix3D fromOtherToThis , final CollisionTester other )
	{
		return testOrientedBoundingBox( getOrientedBoundingBox() , fromOtherToThis , other.getOrientedBoundingBox() );
	}

	/**
	 * Test oriented bounding box intersection.
	 *
	 * Borrowed code from <A href='http://channel9.msdn.com/ShowPost.aspx?PostID=276041'>XNA Oriented Bounding Box Intersection Test</A>,
	 * which was based on <A href='http://www.cs.unc.edu/~geom/theses/gottschalk/main.pdf'>Collision Queries using Oriented Boxes</A> by Stefan Gottschalk.
	 *
	 * @param   box1        Oriented bounding box #1.
	 * @param   from2to1    Transformation from box #2 to box #1.
	 * @param   box2        Oriented bounding box #2.
	 *
	 * @return  <code>true</code> if the bounding boxes intersect;
	 *          <code>false</code> otherwise.
	 */
	private static boolean testOrientedBoundingBox( final Bounds3D box1 , final Matrix3D from2to1 , final Bounds3D box2 )
	{
		final double absXX = Math.abs( from2to1.xx );
		final double absXY = Math.abs( from2to1.xy );
		final double absXZ = Math.abs( from2to1.xz );
		final double absYX = Math.abs( from2to1.yx );
		final double absYY = Math.abs( from2to1.yy );
		final double absYZ = Math.abs( from2to1.yz );
		final double absZX = Math.abs( from2to1.zx );
		final double absZY = Math.abs( from2to1.zy );
		final double absZZ = Math.abs( from2to1.zz );

		final double extents1X   = 0.5 * ( box1.v2.x - box1.v1.x );
		final double extents1Y   = 0.5 * ( box1.v2.y - box1.v1.y );
		final double extents1Z   = 0.5 * ( box1.v2.z - box1.v1.z );

		final double extents2X   = 0.5 * ( box2.v2.x - box2.v1.x );
		final double extents2Y   = 0.5 * ( box2.v2.y - box2.v1.y );
		final double extents2Z   = 0.5 * ( box2.v2.z - box2.v1.z );

		final double separationX = 0.5 * ( box2.v1.x + box2.v2.x - box1.v1.x - box1.v2.x );
		final double separationY = 0.5 * ( box2.v1.y + box2.v2.y - box1.v1.y - box1.v2.y );
		final double separationZ = 0.5 * ( box2.v1.z + box2.v2.z - box1.v1.z - box1.v2.z );

		return
		/* Test 1 X axis */ !( Math.abs( separationX ) > extents1X + Vector3D.dot( extents2X , extents2Y , extents2Z , absXX , absXY , absXZ ) ) &&
		/* Test 1 Y axis */ !( Math.abs( separationY ) > extents1Y + Vector3D.dot( extents2X , extents2Y , extents2Z , absYX , absYY , absYZ ) ) &&
		/* Test 1 Z axis */ !( Math.abs( separationZ ) > extents1Z + Vector3D.dot( extents2X , extents2Y , extents2Z , absZX , absZY , absZZ ) ) &&
		/* Test 2 X axis */ !( Math.abs( Vector3D.dot( from2to1.xx , from2to1.yx , from2to1.zx , separationX , separationY , separationZ ) ) > Vector3D.dot( extents1X , extents1Y , extents1Z , absXX , absYX , absZX ) + extents2X ) &&
		/* Test 2 Y axis */ !( Math.abs( Vector3D.dot( from2to1.xy , from2to1.yy , from2to1.zy , separationX , separationY , separationZ ) ) > Vector3D.dot( extents1X , extents1Y , extents1Z , absXY , absYY , absZY ) + extents2Y ) &&
		/* Test 2 Z axis */ !( Math.abs( Vector3D.dot( from2to1.xz , from2to1.yz , from2to1.zz , separationX , separationY , separationZ ) ) > Vector3D.dot( extents1X , extents1Y , extents1Z , absXZ , absYZ , absZZ ) + extents2Z ) &&
		/* Test 3 case 1 */ !( Math.abs( separationZ * from2to1.yx - separationY * from2to1.zx ) > extents1Y * absZX + extents1Z * absYX + extents2Y * absXZ + extents2Z * absXY ) &&
		/* Test 3 case 2 */ !( Math.abs( separationZ * from2to1.yy - separationY * from2to1.zy ) > extents1Y * absZY + extents1Z * absYY + extents2X * absXZ + extents2Z * absXX ) &&
		/* Test 3 case 3 */ !( Math.abs( separationZ * from2to1.yz - separationY * from2to1.zz ) > extents1Y * absZZ + extents1Z * absYZ + extents2X * absXY + extents2Y * absXX ) &&
		/* Test 3 case 4 */ !( Math.abs( separationX * from2to1.zx - separationZ * from2to1.xx ) > extents1X * absZX + extents1Z * absXX + extents2Y * absYZ + extents2Z * absYY ) &&
		/* Test 3 case 5 */ !( Math.abs( separationX * from2to1.zy - separationZ * from2to1.xy ) > extents1X * absZY + extents1Z * absXY + extents2X * absYZ + extents2Z * absYX ) &&
		/* Test 3 case 6 */ !( Math.abs( separationX * from2to1.zz - separationZ * from2to1.xz ) > extents1X * absZZ + extents1Z * absXZ + extents2X * absYY + extents2Y * absYX ) &&
		/* Test 3 case 7 */ !( Math.abs( separationY * from2to1.xx - separationX * from2to1.yx ) > extents1X * absYX + extents1Y * absXX + extents2Y * absZZ + extents2Z * absZY ) &&
		/* Test 3 case 8 */ !( Math.abs( separationY * from2to1.xy - separationX * from2to1.yy ) > extents1X * absYY + extents1Y * absXY + extents2X * absZZ + extents2Z * absZX ) &&
		/* Test 3 case 9 */ !( Math.abs( separationY * from2to1.xz - separationX * from2to1.yz ) > extents1X * absYZ + extents1Y * absXZ + extents2X * absZY + extents2Y * absZX );
		/* No separating axi => we have intersection */
	}

	/**
	 * Perform precise collision detection using the
	 * <A href='http://coldet.sourceforge.net/'>ColDet</A> library.
	 *
	 * @param   fromOtherToThis     Transformation from other object to this.
	 * @param   other               Object to test collision with.
	 *
	 * @return  <code>true</code> if the objects intersect;
	 *          <code>false</code> otherwise.
	 */
	private boolean testColdet( final Matrix3D fromOtherToThis , final CollisionTester other )
	{
		return testColdet( getCollisionModel() , fromOtherToThis , other.getCollisionModel() );
	}

	/**
	 * Perform precise collision detection using the
	 * <A href='http://coldet.sourceforge.net/'>ColDet</A> library.
	 *
	 * @param   model1      Collision model #1.
	 * @param   from2to1    Transformation from model #2 to model #1.
	 * @param   model2      Collision model #2.
	 *
	 * @return  <code>true</code> if the models intersect;
	 *          <code>false</code> otherwise.
	 */
	private static boolean testColdet( final JCollisionModel3D model1 , final Matrix3D from2to1 , final JCollisionModel3D model2 )
	{
		model1.setTransform(
			1.0f , 0.0f , 0.0f , 0.0f ,
			0.0f , 1.0f , 0.0f , 0.0f ,
			0.0f , 0.0f , 1.0f , 0.0f );

		model2.setTransform(
			(float)from2to1.xx , (float)from2to1.xy , (float)from2to1.xz , (float)from2to1.xo ,
			(float)from2to1.yx , (float)from2to1.yy , (float)from2to1.yz , (float)from2to1.yo ,
			(float)from2to1.zx , (float)from2to1.zy , (float)from2to1.zz , (float)from2to1.zo );

		return model1.collidesWith( model2 );
	}
}