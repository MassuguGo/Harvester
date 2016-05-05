/*******************************************************************************
 * Copyright (c) 2010-2011 VIVO Harvester Team. For full list of contributors, please see the AUTHORS file provided.
 * All rights reserved.
 * This program and the accompanying materials are made available under the terms of the new BSD license which accompanies this distribution, and is available at http://www.opensource.org/licenses/bsd-license.html
 ******************************************************************************/
package org.vivoweb.test.harvester.fetch.nih;

//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
import junit.framework.TestCase;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.vivoweb.harvester.fetch.nih.PubmedHTTPFetch;
import org.vivoweb.harvester.util.InitLog;
import org.vivoweb.harvester.util.repo.JDBCRecordHandler;
//import org.vivoweb.harvester.util.repo.Record;
import org.vivoweb.harvester.util.repo.RecordHandler;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.SAXException;

/**
 * @author James Pence (jrpence@ctrip.ufl.edu)
 * @author Nicholas Skaggs (nskaggs@ctrip.ufl.edu)
 */
public class PubmedHTTPFetchTest extends TestCase {
//	/**
//	 * SLF4J Logger
//	 */
//	private static Logger log = LoggerFactory.getLogger(PubmedHTTPFetchTest.class);
	/** */
	private RecordHandler rh;
	
	@Override
	protected void setUp() throws Exception {
		InitLog.initLogger(null, null);
		this.rh = new JDBCRecordHandler("org.h2.Driver", "jdbc:h2:mem:TestPMSFetchRH", "sa", "", "recordTable", "dataField");
	}
	
	@Override
	protected void tearDown() throws Exception {
		if(this.rh != null) {
			this.rh.close();
		}
	}
	
	/**
	 * Test method for nothing
	 */
	public final void testNothing() {
		assertTrue(true);
	}
	
//	/**
//	 * Test method for {@link org.vivoweb.harvester.fetch.nih.PubmedHTTPFetch#main(java.lang.String[]) main(String... args)}
//	 * .
//	 * @throws IOException error
//	 * @throws ParserConfigurationException error
//	 * @throws SAXException error
//	 */
//	public final void testPubmedHTTPFetchMain() throws IOException, ParserConfigurationException, SAXException {
//		log.info("BEGIN testPubmedHTTPFetchMain");
//		
//		//test 10 records
//		new PubmedHTTPFetch("test@test.com", "1:8000[dp]", "10", "10", this.rh).execute();
//		assertTrue(this.rh.iterator().hasNext());
//		DocumentBuilder docB = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//		int count = 0;
//		for(Record r : this.rh) {
//			Document doc = docB.parse(new ByteArrayInputStream(r.getData().getBytes()));
//			Element elem = doc.getDocumentElement();
//			traverseNodes(elem.getChildNodes());
//			count++;
//		}
//		assertEquals(10, count);
//		log.info("END testPubmedHTTPFetchMain");
//	}
//	
//	/**
//	 * @param nodeList the nodes
//	 */
//	private void traverseNodes(NodeList nodeList) {
//		for(int x = 0; x < nodeList.getLength(); x++) {
//			Node child = nodeList.item(x);
//			String name = child.getNodeName();
//			if(!name.contains("#text")) {
//				log.trace(name);
//				traverseNodes(child.getChildNodes());
//			}
//		}
//	}
}
