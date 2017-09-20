/**
 * 
 */
package com.siemens.scr.avt.ad.query.model;

import edu.uci.ics.jung.graph.impl.SimpleSparseVertex;
import edu.uci.ics.jung.utils.UserData;

public class TableVertex extends SimpleSparseVertex {
		public TableVertex(String tableName) {
			super();
			this.addUserDatum(JoinTree.TABLE_NAME, tableName,
					UserData.CLONE);
		}

		public String getTableName() {
			return (String) this.getUserDatum(JoinTree.TABLE_NAME);
		}
		
		public String toString(){
			return getTableName();
		}
		
		public int hashCode(){
			return getTableName().hashCode();
		}
}