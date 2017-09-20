package com.siemens.scr.avt.ad.query.common;


import com.siemens.scr.avt.ad.util.DicomUtils;

/**
 * A special XPathEntry for querying from the XML format header of DICOM.
 * 
 * <p>
 * The header table and header column have to be set before an entry can be created.
 * </p>
 * 
 * @author Xiang Li
 *
 */
public class DefaultDicomHeaderEntry extends XPathEntry {
	private static String headerTable;
	private static String headerColumn;
	
	public static DefaultDicomHeaderEntry createEntry(int tag){
		if(!check()){
			return null;
		}
		
		return new DefaultDicomHeaderEntry(tag);
	}
	
	/**
	 * Check whether the table and column for the header are already initialized.
	 * @return true if both the table and the column are set, false otherwise.
	 */
	private static boolean check(){
		return headerTable != null && headerTable.length() > 0 && headerColumn != null && headerColumn.length() > 0;
	}
	
	private DefaultDicomHeaderEntry( int tag){
		super(headerTable, headerColumn, buildPath(tag), "text()");
	}
	
	private static String buildPath(int tag){
		String path = "/DicomDataSet/DicomAttribute[@tag=\"" + DicomUtils.tagToString(tag) + "\"]/Value" ;
		return path;
	}

	static void setHeaderTable(String table) {
		DefaultDicomHeaderEntry.headerTable = table;
	}

	static void setHeaderColumn(String headerColumn) {
		DefaultDicomHeaderEntry.headerColumn = headerColumn;
	}
	
}
