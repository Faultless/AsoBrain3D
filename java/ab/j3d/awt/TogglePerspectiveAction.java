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
package ab.j3d.awt;

import java.awt.event.*;
import java.util.*;

import ab.j3d.view.*;
import com.numdata.oss.*;
import com.numdata.oss.ui.*;

/**
 * This action toggles perspective projection on/off.
 *
 * @author  Peter S. Heijnen
 * @version $Revision$ $Date$
 */
public class TogglePerspectiveAction
	extends ToggleAction
{
	/**
	 * The {@link View3D} this action belongs to.
	 */
	private final View3D _view;

	/**
	 * Create action.
	 *
	 * @param   locale  Preferred locale for internationalization.
	 * @param   view    View to create action for.
	 */
	public TogglePerspectiveAction( final Locale locale, final View3D view )
	{
		super( ResourceBundleTools.getBundle( TogglePerspectiveAction.class, locale ), "togglePerspective" );
		_view = view;
	}

	@Override
	public void actionPerformed( final ActionEvent e )
	{
		setValue( !getValue() );
	}

	@Override
	public boolean getValue()
	{
		final View3D view = _view;
		return ( view.getProjectionPolicy() == ProjectionPolicy.PERSPECTIVE );
	}

	@Override
	public void setValue( final boolean value )
	{
		final View3D view = _view;
		switch ( view.getProjectionPolicy() )
		{
			case PARALLEL:
				view.setProjectionPolicy( ProjectionPolicy.PERSPECTIVE );
				break;

			case PERSPECTIVE:
				view.setProjectionPolicy( ProjectionPolicy.PARALLEL );
				break;
		}
	}
}