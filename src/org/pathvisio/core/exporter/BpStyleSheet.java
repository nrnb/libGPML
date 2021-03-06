// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2015 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// 
// http://www.apache.org/licenses/LICENSE-2.0 
//  
// Unless required by applicable law or agreed to in writing, software 
// distributed under the License is distributed on an "AS IS" BASIS, 
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// See the License for the specific language governing permissions and 
// limitations under the License.
//
package org.pathvisio.core.exporter;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;

/**
 * A simple class for reading / writing BioPAX style sheets.
 * <p> 
 * BioPAX style sheets store coordinates for entities in a BioPAX document. 
 * In principle, you can assign coordinates to any biopax entity, but in practice they are
 * most useful on Protein and SmallMolecule instances. 
 */
public class BpStyleSheet 
{
	private Map<String, Point2D> map = new HashMap<String, Point2D>();

	/**
	 * Store a coordinate for a given entity. 
	 * The coordinate should be the center of the entity representation.
	 * @param id entity identifier, this should correspond to the identifier of the BioPAX entity. 
	 * @param x x coordinate.
	 * @param y y coordinate
	 */
	public void add (String id, double x, double y)
	{
		Point2D point = new Point2D.Double(x, y);
		add (id, point);
	}
	
	/**
	 * Store a coordinate for a given entity. 
	 * The coordinate should be the center of the entity representation.
	 * @param id entity identifier, this should correspond to the identifier of the BioPAX entity. 
	 * @param point 2D coordinate.
	 */
	public void add (String id, Point2D point)
	{
		map.put (id, point);
	}
	
	/**
	 * Retrieve a coordinate from a read document.
	 * @param id entity identifier
	 * @returns null if id is not found.
	 */
	public Point2D get (String id)
	{
		return map.get(id);
	}
	
	private static final Namespace NAMESPACE = Namespace.getNamespace("http://biopax.org/bpss/2009");
	private static final String ROOT_TAG = "bpss";
	private static final String COORD_TAG = "coordinate";
	private static final String ATTR_X = "x";
	private static final String ATTR_ID = "id";
	private static final String ATTR_Y = "y";
	
	/**
	 * Write stylesheet to file in bpss file format.
	 * @param out OutputStream to write to.
	 */
	public void write(OutputStream out) throws IOException
	{
		Document doc = new Document();
		Element root = new Element (ROOT_TAG, NAMESPACE);
		doc.setRootElement(root);
		
		for (String key : map.keySet())
		{
			Point2D point = map.get(key); 
			Element coord = new Element(COORD_TAG, NAMESPACE);
			coord.setAttribute(ATTR_X, "" + point.getX());
			coord.setAttribute(ATTR_Y, "" + point.getY());
			coord.setAttribute(ATTR_ID, key);
			root.addContent(coord);
		}

		XMLOutputter xmlcode = new XMLOutputter(Format.getPrettyFormat());
		Format f = xmlcode.getFormat();
		f.setEncoding("UTF-8");
		f.setTextMode(Format.TextMode.PRESERVE);
		xmlcode.setFormat(f);

		//Send XML code to the outputstream 
		xmlcode.output(doc, out);
	}
	
	/**
	 * Read stylesheet from file in bpss file format.
	 * @param in InputSource (InputStream, Reader or File).
	 */
	public static void read(InputSource in) throws JDOMException, IOException
	{
		SAXBuilder builder  = new SAXBuilder(false); // no validation when reading the xml file
		Document doc = builder.build(in);
		Element root = doc.getRootElement();
		Namespace ns = root.getNamespace();
		if (!(root.getName().equals(ROOT_TAG) && ns.equals(NAMESPACE)))
		{
			throw new IOException ("Wrong file format");
		}

//		for (Object o : root.getChildren(COORD_TAG))
//		{
//			Element coord = (Element)o;
//			String key = coord.getAttributeValue(ATTR_ID);
//			double x = Double.parseDouble(coord.getAttributeValue(ATTR_X));
//			double y = Double.parseDouble(coord.getAttributeValue(ATTR_Y));
//		}
	}
}
