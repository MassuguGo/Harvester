package org.vivoweb.test.harvester.util;

import java.io.IOException;
import junit.framework.TestCase;
import org.apache.jena.ext.com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vivoweb.harvester.util.FileAide;
import org.vivoweb.harvester.util.InitLog;
import org.vivoweb.harvester.util.XMLGrep;

/**
 * @author Christopher Haines (chris@chrishaines.net)
 *
 */
public class XMLGrepTest extends TestCase {
	
	@SuppressWarnings("javadoc")
	private static Logger log = LoggerFactory.getLogger(XMLGrepTest.class);
	
	/**
	 * Temp directory
	 */
	private String tempDir;
	
	@SuppressWarnings("javadoc")
	protected static final String xmlContent1 = "<ns0:PERSON xmlns:ns0=\"http://uf.biztalk.shibperson\">" +
		"<UFID>83117145</UFID>" +
		"<GLID>vsposato</GLID>" +
		"<UFID2 />" +
		"<GLID2 />" +
		"<ACTIVE>A</ACTIVE>" +
		"<PROTECT>N</PROTECT>" +
		"<AFFILIATION>T</AFFILIATION>" +
		"<NAME type=\"21\">SPOSATO,VINCENT J</NAME>" +
		"<NAME type=\"33\">Sposato,Vincent J</NAME>" +
		"<NAME type=\"35\">Vincent</NAME>" +
		"<NAME type=\"36\">Sposato</NAME>" +
		"<NAME type=\"37\">J</NAME>" +
		"<NAME type=\"232\">Sposato,Vincent</NAME>" +
		"<ADDRESS>" +
		"<ADDRESS1 />" +
		"<ADDRESS2 />" +
		"<ADDRESS3>PO BOX 100152</ADDRESS3>" +
		"<CITY>GAINESVILLE</CITY>" +
		"<STATE>FL</STATE>" +
		"<ZIP>326100152</ZIP>" +
		"</ADDRESS>" +
		"<PHONE type=\"10\">(352) 294-5274   45274</PHONE>" +
		"<EMAIL type=\"1\">vsposato@ufl.edu</EMAIL>" +
		"<DEPTID>27010707</DEPTID>" +
		"<RELATIONSHIP type=\"195\">" +
		"<DEPTID>27010707</DEPTID>" +
		"<DEPTNAME>HA-AHC ESE</DEPTNAME>" +
		"</RELATIONSHIP>" +
		"<RELATIONSHIP type=\"203\">" +
		"<DEPTID>27010707</DEPTID>" +
		"<DEPTNAME>HA-AHC ESE</DEPTNAME>" +
		"</RELATIONSHIP>" +
		"<RELATIONSHIP type=\"223\">" +
		"<DEPTID>27010707</DEPTID>" +
		"<DEPTNAME>CREATEHA-AHC ESE</DEPTNAME>" +
		"</RELATIONSHIP>" +
		"<WORKINGTITLE>IT Expert, Sr. Software Engineer</WORKINGTITLE>" +
		"<DECEASED>N</DECEASED>" +
		"<LOA>Bronze</LOA>" +
		"<ACTION>RENAME</ACTION>" +
		"</ns0:PERSON>";
	
	@SuppressWarnings("javadoc")
	protected static final String xmlContent2 = "<ns0:PERSON xmlns:ns0=\"http://uf.biztalk.shibperson\">" +
		"<UFID>83117145</UFID>" +
		"<GLID>vsposato</GLID>" +
		"<UFID2 />" +
		"<GLID2 />" +
		"<ACTIVE>A</ACTIVE>" +
		"<PROTECT>N</PROTECT>" +
		"<AFFILIATION>T</AFFILIATION>" +
		"<NAME type=\"21\">SPOSATO,VINCENT J</NAME>" +
		"<NAME type=\"33\">Sposato,Vincent J</NAME>" +
		"<NAME type=\"35\">Vincent</NAME>" +
		"<NAME type=\"36\">Sposato</NAME>" +
		"<NAME type=\"37\">J</NAME>" +
		"<NAME type=\"232\">Sposato,Vincent</NAME>" +
		"<ADDRESS>" +
		"<ADDRESS1 />" +
		"<ADDRESS2 />" +
		"<ADDRESS3>PO BOX 100152</ADDRESS3>" +
		"<CITY>GAINESVILLE</CITY>" +
		"<STATE>FL</STATE>" +
		"<ZIP>326100152</ZIP>" +
		"</ADDRESS>" +
		"<PHONE type=\"10\">(352) 294-5274   45274</PHONE>" +
		"<EMAIL type=\"1\">vsposato@ufl.edu</EMAIL>" +
		"<DEPTID>27010707</DEPTID>" +
		"<RELATIONSHIP type=\"195\">" +
		"<DEPTID>27010707</DEPTID>" +
		"<DEPTNAME>HA-AHC ESE</DEPTNAME>" +
		"</RELATIONSHIP>" +
		"<RELATIONSHIP type=\"203\">" +
		"<DEPTID>27010707</DEPTID>" +
		"<DEPTNAME>HA-AHC ESE</DEPTNAME>" +
		"</RELATIONSHIP>" +
		"<RELATIONSHIP type=\"223\">" +
		"<DEPTID>27010707</DEPTID>" +
		"<DEPTNAME>CREATEHA-AHC ESE</DEPTNAME>" +
		"</RELATIONSHIP>" +
		"<WORKINGTITLE>IT Expert, Sr. Software Engineer</WORKINGTITLE>" +
		"<DECEASED>N</DECEASED>" +
		"<LOA>Bronze</LOA>" +
		"<IGNORE>YES</IGNORE>" +
		"</ns0:PERSON>";
	
	@SuppressWarnings("javadoc")
	protected static final String xmlContent3 = "<ns0:PERSON xmlns:ns0=\"http://uf.biztalk.shibperson\">" +
		"<UFID>83117145</UFID>" +
		"<GLID>vsposato</GLID>" +
		"<UFID2 />" +
		"<GLID2 />" +
		"<ACTIVE>A</ACTIVE>" +
		"<PROTECT>N</PROTECT>" +
		"<AFFILIATION>T</AFFILIATION>" +
		"<NAME type=\"21\">SPOS%%&*$#%&TO</NAME>" +
		"<NAME type=\"33\">Sposato,Vincent J</NAME>" +
		"<NAME type=\"35\">Vin<tagcent></NAME>" +
		"<NAME type=\"36\">&&&Qw_SposatoErTy&&&</NAME>" +
		"<NAME type=\"37\">J</NAME>" +
		"<NAME type=\"232\">WH%AT&IS><ML???</NAME>" +
		"<ADDRESS>" +
		"<ADDRESS1 />" +
		"<ADDRESS2 />" +
		"<ADDRESS3>PO BOX 100152</ADDRESS3>" +
		"<CITY>GAINESVILLE</CITY>" +
		"<STATE>FL</STATE>" +
		"<ZIP>326100152</ZIP>" +
		"</ADDRESS>" +
		"<PHONE type=\"10\">(352) 294-5274   45274</PHONE>" +
		"<EMAIL type=\"1\">vsposato@ufl.edu</EMAIL>" +
		"<DEPTID>27010707</DEPTID>" +
		"<RELATIONSHIP type=\"195\">" +
		"<DEPTID>27010707</DEPTID>" +
		"<DEPTNAME>HA-AHC ESE</DEPTNAME>" +
		"</RELATIONSHIP>" +
		"<RELATIONSHIP type=\"203\">" +
		"<DEPTID>27010707</DEPTID>" +
		"<DEPTNAME>HA-AHC ESE</DEPTNAME>" +
		"</RELATIONSHIP>" +
		"<RELATIONSHIP type=\"223\">" +
		"<DEPTID>27010707</DEPTID>" +
		"<DEPTNAME>CREATEHA-AHC ESE</DEPTNAME>" +
		"</RELATIONSHIP>" +
		"<WORKINGTITLE>IT Expert, Sr. Software Engineer</WORKINGTITLE>" +
		"<DECEASED>N</DECEASED>" +
		"<LOA>Bronze</LOA>" +
		"<IGNORE>YES</IGNORE>" +
		"</ns0:PERSON>";
	
	@SuppressWarnings("javadoc")
	protected static final String xmlContent4 =
		"<ns0:PERSON xmlns:ns0=\"http://uf.biztalk.shibperson\"><UFID>73218810</UFID><GLID>npeacock</GLID><ACTIVE>A</ACTIVE><PROTECT>N</PROTECT><AFFILIATION>A</AFFILIATION><NAME type=\"21\">PEACOCK,NEAL JOSEPH</NAME><NAME type=\"33\">Peacock,Neal Joseph</NAME><NAME type=\"35\">Neal</NAME><NAME type=\"36\">Peacock</NAME><NAME type=\"37\">Joseph</NAME><NAME type=\"232\">Peacock,Neal Joseph</NAME><ADDRESS><ADDRESS1 /><ADDRESS2 /><ADDRESS3 /><CITY /><STATE /><ZIP /></ADDRESS><EMAIL type=\"1\">npeacock@ufl.edu</EMAIL><DEPTID>ST010000</DEPTID><RELATIONSHIP type=\"208\"><DEPTID>ST010000</DEPTID><DEPTNAME>REGISTRAR STUDENTS</DEPTNAME></RELATIONSHIP><RELATIONSHIP type=\"215\"><DEPTID>ST010000</DEPTID><DEPTNAME>REGISTRAR STUDENTS</DEPTNAME></RELATIONSHIP><RELATIONSHIP type=\"223\"><DEPTID>ST010000</DEPTID><DEPTNAME>REGISTRAR STUDENTS</DEPTNAME></RELATIONSHIP><WORKINGTITLE>(=o)<--<</WORKINGTITLE><DECEASED>N</DECEASED><LOA>Invalid</LOA><ACTION>CREATE</ACTION></ns0:PERSON>";
	/**
	 * Destination dir for all files matching expression
	 */
	private String destination;
	
	/**
	 * Alternate destination directory for files that do not match
	 */
	private String altDest;
	
	/**
	 * Destination for malformed or exception throwing xml files.
	 */
	private String errDest;
	
	/**
	 * Source dir for input xml messages 
	 */
	private String src;
	
	/**
	 * Source xml file name 
	 */
	private String srcFile;
	
	@Override
	protected void setUp() throws Exception {
		InitLog.initLogger(null, null);
		this.tempDir = Files.createTempDir().getAbsolutePath();
		// load input models
		this.src = this.tempDir+"/soapsrc/";
		this.srcFile = "test";
		FileAide.createFolder(this.src);
		this.altDest = this.tempDir+"/altDest/";
		FileAide.createFolder(this.altDest);
		this.errDest = this.tempDir+"/errDest/";
		FileAide.createFolder(this.errDest);
		this.destination = this.tempDir+"/desination/";
		FileAide.createFolder(this.destination);
	}
	
	@Override
	protected void tearDown() {
		try {
			FileAide.delete(this.tempDir);
		} catch(IOException e) {
			log.error("Error:", e);
		}
	}
	
//	private void testValueAndTag(String ignoredest, String src, String srcFile, String xmlContent3) throws IOException {
	
	/**
	 * This the test case to test the Xml grep functionality on the bases of a Tagname and value.  All files with <tag> value<tag> will be moved to specified destination dir
	 * @throws IOException
	 */
	@SuppressWarnings("javadoc")
	public void testValueAndTagPositiveTest() throws IOException {
		log.trace("testValueAndTagPositiveTest");
		createSrcFile(XMLGrepTest.xmlContent2);
		XMLGrep xmlGrep = new XMLGrep(this.src, this.destination, this.altDest, this.errDest, "YES", "IGNORE");
		xmlGrep.execute();
		assertTrue(FileAide.exists(this.destination + this.srcFile));
		assertFalse(FileAide.exists(this.src + this.srcFile));
		assertFalse(FileAide.exists(this.altDest + this.srcFile));
	}
	
	@SuppressWarnings("javadoc")
	public void testValueAndTagNegativeTest() throws IOException {
		log.trace("testValueAndTagNegativeTest");
		createSrcFile(XMLGrepTest.xmlContent1);
		XMLGrep xmlGrep = new XMLGrep(this.src, this.destination, this.altDest, this.errDest, "YES", "IGNORE");
		xmlGrep.execute();
		assertFalse(FileAide.exists(this.destination + this.srcFile));
		assertFalse(FileAide.exists(this.src + this.srcFile));
		assertTrue(FileAide.exists(this.altDest + this.srcFile));
	}
	//private void testValueOnly(String renamedest, String src, String srcFile, String xmlContent1) throws IOException {
	
	/**
	 * This the test case to test the Xml grep functionality on the bases of only tag value.  All files with specified Tag value will be moved to specified destination dir.
	 * @throws IOException
	 */
	@SuppressWarnings("javadoc")
	public void testValueOnlyPositiveTest() throws IOException {
		log.trace("testValueOnlyPositiveTest");
		createSrcFile(XMLGrepTest.xmlContent1);
		XMLGrep xmlGrep = new XMLGrep(this.src, this.destination,this.altDest, this.errDest, "RENAME", null);
		xmlGrep.execute();
		assertTrue(FileAide.exists(this.destination + this.srcFile));
		assertFalse(FileAide.exists(this.src + this.srcFile));
		assertFalse(FileAide.exists(this.altDest + this.srcFile));
	}
	
	@SuppressWarnings("javadoc")
	public void testValueOnlyNegativeTest() throws IOException {
		log.trace("testValueOnlyNegativeTest");
		createSrcFile(XMLGrepTest.xmlContent2);
		XMLGrep xmlGrep = new XMLGrep(this.src, this.destination, this.altDest, this.errDest, "RENAME", null);
		xmlGrep.execute();
		assertFalse(FileAide.exists(this.destination + this.srcFile));
		assertFalse(FileAide.exists(this.src + this.srcFile));
		assertTrue(FileAide.exists(this.altDest + this.srcFile));
	}

	@SuppressWarnings("javadoc")
	public void testTagOnlyPositiveTest() throws IOException {
		log.trace("testTagOnlyPositiveTest");
		createSrcFile(XMLGrepTest.xmlContent2);
		XMLGrep xmlGrep = new XMLGrep(this.src, this.destination, this.altDest, this.errDest, null, "IGNORE");
		xmlGrep.execute();
		assertTrue(FileAide.exists(this.destination + this.srcFile));
		assertFalse(FileAide.exists(this.src + this.srcFile));
		assertFalse(FileAide.exists(this.altDest + this.srcFile));
	}
	
	@SuppressWarnings("javadoc")
	public void testTagOnlyNegativeTest() throws IOException {
		log.trace("testTagOnlyNegativeTest");
		createSrcFile(XMLGrepTest.xmlContent1);
		XMLGrep xmlGrep = new XMLGrep(this.src, this.destination, this.altDest, this.errDest, null, "IGNORE");
		xmlGrep.execute();
		assertFalse(FileAide.exists(this.destination + this.srcFile));
		assertFalse(FileAide.exists(this.src + this.srcFile));
		assertTrue(FileAide.exists(this.altDest + this.srcFile));
	}

	/**
	 * This will test a tag and a value being passed without an alternate destination, and is
	 * expected to be matched
	 * @throws IOException
	 */
	@SuppressWarnings("javadoc")
	public void testValueAndTagPositiveTestNoAltDestination() throws IOException {
		log.trace("testValueAndTagPositiveTestNoAltDestination");
		createSrcFile(XMLGrepTest.xmlContent2);
		XMLGrep xmlGrep = new XMLGrep(this.src, this.destination, null, this.errDest, "YES", "IGNORE");
		xmlGrep.execute();
		assertTrue(FileAide.exists(this.destination + this.srcFile));
		assertFalse(FileAide.exists(this.src + this.srcFile));
		assertFalse(FileAide.exists(this.altDest + this.srcFile));
	}
	
	/**
	 * This will test a tag and a value being passed without an alternate destination, and is
	 * expected to NOT be matched
	 * @throws IOException
	 */
	@SuppressWarnings("javadoc")
	public void testValueAndTagNegativeTestNoAltDestination() throws IOException {
		log.trace("testValueAndTagNegativeTestNoAltDestination");
		createSrcFile(XMLGrepTest.xmlContent1);
		XMLGrep xmlGrep = new XMLGrep(this.src, this.destination, null, this.errDest, "YES", "IGNORE");
		xmlGrep.execute();
		assertFalse(FileAide.exists(this.destination + this.srcFile));
		assertTrue(FileAide.exists(this.src + this.srcFile));
		assertFalse(FileAide.exists(this.altDest + this.srcFile));
	}
	//private void testValueOnly(String renamedest, String src, String srcFile, String xmlContent1) throws IOException {
	
	/**
	 * This will test a value being passed without an alternate destination, and is
	 * expected to be matched
	 * @throws IOException
	 */
	@SuppressWarnings("javadoc")
	public void testValueOnlyPositiveTestNoAltDestination() throws IOException {
		log.trace("testValueOnlyPositiveTestNoAltDestination");
		createSrcFile(XMLGrepTest.xmlContent1);
		XMLGrep xmlGrep = new XMLGrep(this.src, this.destination,null, this.errDest, "RENAME", null);
		xmlGrep.execute();
		assertTrue(FileAide.exists(this.destination + this.srcFile));
		assertFalse(FileAide.exists(this.src + this.srcFile));
		assertFalse(FileAide.exists(this.altDest + this.srcFile));
	}
	
	/**
	 * This will test a value being passed without an alternate destination, and is
	 * expected to NOT be matched
	 * @throws IOException
	 */
	@SuppressWarnings("javadoc")
	public void testValueOnlyNegativeTestNoAltDestination() throws IOException {
		log.trace("testValueOnlyNegativeTestNoAltDestination");
		createSrcFile(XMLGrepTest.xmlContent2);
		XMLGrep xmlGrep = new XMLGrep(this.src, this.destination, null, this.errDest, "RENAME", null);
		xmlGrep.execute();
		assertFalse(FileAide.exists(this.destination + this.srcFile));
		assertTrue(FileAide.exists(this.src + this.srcFile));
		assertFalse(FileAide.exists(this.altDest + this.srcFile));
	}

	/**
	 * This will test a tag being passed without an alternate destination, and is
	 * expected to be matched
	 * @throws IOException
	 */
	@SuppressWarnings("javadoc")
	public void testTagOnlyPositiveTestNoAltDestination() throws IOException {
		log.trace("testTagOnlyPositiveTestNoAltDestination");
		createSrcFile(XMLGrepTest.xmlContent2);
		XMLGrep xmlGrep = new XMLGrep(this.src, this.destination, null, this.errDest, null, "IGNORE");
		xmlGrep.execute();
		assertTrue(FileAide.exists(this.destination + this.srcFile));
		assertFalse(FileAide.exists(this.src + this.srcFile));
		assertFalse(FileAide.exists(this.altDest + this.srcFile));
	}
	
	/**
	 * This will test a tag being passed without an alternate destination, and is
	 * expected to NOT be matched
	 * @throws IOException
	 */
	@SuppressWarnings("javadoc")
	public void testTagOnlyNegativeTestNoAltDestination() throws IOException {
		log.trace("testTagOnlyNegativeTestNoAltDestination");
		createSrcFile(XMLGrepTest.xmlContent1);
		XMLGrep xmlGrep = new XMLGrep(this.src, this.destination, null, this.errDest, null, "IGNORE");
		xmlGrep.execute();
		assertFalse(FileAide.exists(this.destination + this.srcFile));
		assertTrue(FileAide.exists(this.src + this.srcFile));
		assertFalse(FileAide.exists(this.altDest + this.srcFile));
	}

	@SuppressWarnings("javadoc")
	public void testMalformedXMLSource() throws IOException {
		log.trace("testMalformedXMLSource");
//		createSrcFile(XMLGrepTest.xmlContent3);
//		XMLGrep xmlGrep = new XMLGrep(this.src, this.destination, null, this.errDest, null, "IGNORE");
//		xmlGrep.execute();
//		assertTrue(FileAide.exists(this.errDest + this.srcFile));
//		assertFalse(FileAide.exists(this.src + this.srcFile));
//		assertFalse(FileAide.exists(this.destination + this.srcFile));
//		assertFalse(FileAide.exists(this.altDest + this.srcFile));
		
		createSrcFile(XMLGrepTest.xmlContent4);
		XMLGrep xmlGrep2 = new XMLGrep(this.src, this.destination, null, this.errDest, null, "IGNORE");
		xmlGrep2.execute();
		assertTrue(FileAide.exists(this.errDest + this.srcFile));
		assertFalse(FileAide.exists(this.src + this.srcFile));
		assertFalse(FileAide.exists(this.destination + this.srcFile));
		assertFalse(FileAide.exists(this.altDest + this.srcFile));
		
	}
	
	@SuppressWarnings("javadoc")
	public void testEmptyFile() {
		log.trace("testEmptyFile");
		createEmptyFile();
		XMLGrep xmlGrep = new XMLGrep(this.src, this.destination, null, this.errDest, null, "IGNORE");
		xmlGrep.execute();
	}
	
	@SuppressWarnings("javadoc")
	private void createSrcFile(String xmlContent) {
		try {
//			System.out.println(System.getProperty("user.dir"));
			//FileAide.createFolder(this.src);
			FileAide.createFile(this.src + this.srcFile);
			FileAide.setTextContent(this.src + this.srcFile, xmlContent);
		} catch(IOException e) {
			log.error("Error:", e);
		}
	}
	
	@SuppressWarnings("javadoc")
	private void createEmptyFile()
	{
		try{
			FileAide.createFile(this.src + this.srcFile);
		} catch (IOException e) {
			log.error("Error:", e);
		}
	}
}
