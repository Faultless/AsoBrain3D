/*
 * $Id$
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

package ab.xml;

import java.io.*;

import org.jetbrains.annotations.*;
import org.xmlpull.v1.*;

/**
 * XML reader implementation that uses XML Pull.
 *
 * @author G. Meinders
 * @version $Revision$ $Date$
 */
class XmlPullReader
	implements XMLReader
{
	/**
	 * XML Pull parser to be used.
	 */
	@NotNull
	private final XmlPullParser _parser;

	/**
	 * Event type returned by the last call to {@link #next()}.
	 */
	@NotNull
	private XMLEventType _eventType;

	/**
	 * Target of the current processing instruction, if any.
	 */
	@Nullable
	private String _piTarget;

	/**
	 * Data of the current processing instruction, if any.
	 */
	@Nullable
	private String _piData;

	/**
	 * Text content of the current character data event. If set, the state of
	 * the underlying parser should be ignored and a character data event with
	 * the content of this string should be reported instead.
	 */
	@Nullable
	private String _characterData;

	/**
	 * Used to coalesce consecutive character data that is returned as separate
	 * events by {@link XmlPullParser#nextToken()}.
	 */
	@NotNull
	private final StringBuilder _characterDataBuilder;

	/**
	 * Constructs a new instance.
	 *
	 * @param   parser  XML Pull parser to be used.
	 */
	public XmlPullReader( final XmlPullParser parser )
	{
		_parser = parser;
		_eventType = XMLEventType.START_DOCUMENT;
		_piTarget = null;
		_piData = null;
		_characterData = null;
		_characterDataBuilder = new StringBuilder();
	}

	@NotNull
	@Override
	public XMLEventType getEventType()
	{
		return _eventType;
	}

	@NotNull
	@Override
	public XMLEventType next()
		throws XMLException
	{
		if ( _eventType == XMLEventType.END_DOCUMENT )
		{
			throw new IllegalStateException( "Not allowed after " + XMLEventType.END_DOCUMENT + " event." );
		}

		XMLEventType result;
		do
		{
			final int token;
			try
			{
				if ( _characterData == null )
				{
					try
					{
						token = _parser.nextToken();
					}
					catch ( IOException e )
					{
						throw new XMLException( e );
					}
				}
				else
				{
					token = _parser.getEventType();
					_characterData = null;
				}
			}
			catch ( XmlPullParserException e )
			{
				throw new XMLException( e );
			}

			if ( token == XmlPullParser.START_DOCUMENT )
			{
				result = XMLEventType.START_DOCUMENT;
			}
			else if ( token == XmlPullParser.END_DOCUMENT )
			{
				result = XMLEventType.END_DOCUMENT;
			}
			else if ( token == XmlPullParser.START_TAG )
			{
				result = XMLEventType.START_ELEMENT;
			}
			else if ( token == XmlPullParser.END_TAG )
			{
				result = XMLEventType.END_ELEMENT;
			}
			else if ( token == XmlPullParser.TEXT )
			{
				result = XMLEventType.CHARACTERS;
			}
			else if ( token == XmlPullParser.CDSECT )
			{
				result = XMLEventType.CHARACTERS;
			}
			else if ( token == XmlPullParser.ENTITY_REF )
			{
				result = XMLEventType.CHARACTERS;
			}
			else if ( token == XmlPullParser.IGNORABLE_WHITESPACE )
			{
				result = null;
			}
			else if ( token == XmlPullParser.PROCESSING_INSTRUCTION )
			{
				result = XMLEventType.PROCESSING_INSTRUCTION;
			}
			else if ( token == XmlPullParser.COMMENT )
			{
				result = null;
			}
			else if ( token == XmlPullParser.DOCDECL )
			{
				result = XMLEventType.DTD;
			}
			else
			{
				throw new XMLException( "Unknown token: " + token );
			}
		}
		while ( result == null );

		_eventType = result;
		updateProcessingInstructionFields();

		if ( result == XMLEventType.CHARACTERS )
		{
			coalesceCharacterData();
		}

		return result;
	}

	/**
	 * Updates the {@link #_piTarget} and {@link #_piData} fields. Must be
	 * called after {@link #_eventType} is updated.
	 */
	private void updateProcessingInstructionFields()
	{
		String piTarget = null;
		String piData = null;

		if ( _eventType == XMLEventType.PROCESSING_INSTRUCTION )
		{
			final String text = _parser.getText();
			final int length = text.length();

			int targetEnd = 1;
			while ( targetEnd < length )
			{
				final char c = text.charAt( targetEnd );
				if ( ( c == ' ' ) || ( c == '\t' ) || ( c == '\r' ) || ( c == '\n' ) )
				{
					break;
				}
				targetEnd++;
			}

			if ( targetEnd == length )
			{
				piTarget = text;
				piData = "";
			}
			else
			{
				int dataStart = targetEnd + 1;
				while ( dataStart < length )
				{
					final char c = text.charAt( dataStart );
					if ( ( c != ' ' ) && ( c != '\t' ) && ( c != '\r' ) && ( c != '\n' ) )
					{
						break;
					}
					dataStart++;
				}

				piTarget = text.substring( 0, targetEnd );
				piData = text.substring( dataStart );
			}
		}

		_piTarget = piTarget;
		_piData = piData;
	}

	/**
	 * Combines the current character data event and any following character
	 * data events into a single event.
	 *
	 * @throws  XMLException if an XML-related exception occurs.
	 */
	private void coalesceCharacterData()
		throws XMLException
	{
		final StringBuilder builder = _characterDataBuilder;
		builder.append( _parser.getText() );
		try
		{
			while ( true )
			{
				final int token = _parser.nextToken();
				if ( ( token == XmlPullParser.TEXT ) ||
				     ( token == XmlPullParser.CDSECT ) ||
				     ( token == XmlPullParser.ENTITY_REF ) )
				{
					builder.append( _parser.getText() );
				}
				else
				{
					break;
				}
			}
		}
		catch ( XmlPullParserException e )
		{
			throw new XMLException( e );
		}
		catch ( IOException e )
		{
			throw new XMLException( e );
		}

		_characterData = builder.toString();
		_characterDataBuilder.setLength( 0 );
	}

	@Override
	public String getNamespaceURI()
	{
		final XMLEventType eventType = _eventType;
		if ( ( eventType != XMLEventType.START_ELEMENT ) &&
		     ( eventType != XMLEventType.END_ELEMENT ) )
		{
			throw new IllegalStateException( "Not allowed for " + eventType );
		}

		return _parser.getNamespace( _parser.getPrefix() );
	}

	@NotNull
	@Override
	public String getLocalName()
	{
		final XMLEventType eventType = _eventType;
		if ( ( eventType != XMLEventType.START_ELEMENT ) &&
		     ( eventType != XMLEventType.END_ELEMENT ) )
		{
			throw new IllegalStateException( "Not allowed for " + eventType );
		}

		return _parser.getName();
	}

	@Override
	public int getAttributeCount()
	{
		if ( _eventType != XMLEventType.START_ELEMENT )
		{
			throw new IllegalStateException( "Not allowed for " + _eventType );
		}

		return _parser.getAttributeCount();
	}

	@Override
	public String getAttributeNamespaceURI( final int index )
	{
		if ( _eventType != XMLEventType.START_ELEMENT )
		{
			throw new IllegalStateException( "Not allowed for " + _eventType );
		}

		if ( ( index < 0 ) || ( index >= getAttributeCount() ) )
		{
			throw new IndexOutOfBoundsException( index + " (attributeCount: " + getAttributeCount() + ")" );
		}

		return ( _parser.getAttributePrefix( index ) == null ) ? null : _parser.getAttributeNamespace( index );
	}

	@NotNull
	@Override
	public String getAttributeLocalName( final int index )
	{
		if ( _eventType != XMLEventType.START_ELEMENT )
		{
			throw new IllegalStateException( "Not allowed for " + _eventType );
		}

		if ( ( index < 0 ) || ( index >= getAttributeCount() ) )
		{
			throw new IndexOutOfBoundsException( index + " (attributeCount: " + getAttributeCount() + ")" );
		}

		return _parser.getAttributeName( index );
	}

	@NotNull
	@Override
	public String getAttributeValue( final int index )
	{
		if ( _eventType != XMLEventType.START_ELEMENT )
		{
			throw new IllegalStateException( "Not allowed for " + _eventType );
		}

		if ( ( index < 0 ) || ( index >= getAttributeCount() ) )
		{
			throw new IndexOutOfBoundsException( index + " (attributeCount: " + getAttributeCount() + ")" );
		}

		return _parser.getAttributeValue( index );
	}

	@Override
	public String getAttributeValue( final String namespaceURI, @NotNull final String localName )
	{
		if ( _eventType != XMLEventType.START_ELEMENT )
		{
			throw new IllegalStateException( "Not allowed for " + _eventType );
		}

		return _parser.getAttributeValue( namespaceURI, localName );
	}

	@NotNull
	@Override
	public String getText()
	{
		if ( _eventType != XMLEventType.CHARACTERS )
		{
			throw new IllegalStateException( "Not allowed for " + _eventType );
		}

		return ( _characterData != null ) ? _characterData : _parser.getText();
	}

	@NotNull
	@Override
	public String getPITarget()
	{
		if ( _eventType != XMLEventType.PROCESSING_INSTRUCTION )
		{
			throw new IllegalStateException( "Not allowed for " + _eventType );
		}

		// noinspection ConstantConditions
		return _piTarget;
	}

	@NotNull
	@Override
	public String getPIData()
	{
		if ( _eventType != XMLEventType.PROCESSING_INSTRUCTION )
		{
			throw new IllegalStateException( "Not allowed for " + _eventType );
		}

		// noinspection ConstantConditions
		return _piData;
	}
}