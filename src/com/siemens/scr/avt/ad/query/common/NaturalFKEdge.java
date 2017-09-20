/**
 * 
 */
package com.siemens.scr.avt.ad.query.common;

import com.siemens.scr.avt.ad.query.model.FKEdge;
import com.siemens.scr.avt.ad.query.model.JoinTree;
import com.siemens.scr.avt.ad.query.model.TableVertex;

import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.utils.UserData;

/**
 * Natural FK requires the referencing column and the referenced column have the
 * same name.
 * 
 * @author Xiang Li
 *
 */
class NaturalFKEdge extends UndirectedSparseEdge implements FKEdge {

		public NaturalFKEdge(TableVertex from, TableVertex to, String fk) {// natural join
			super(from, to);
			this.addUserDatum(JoinTree.FK, fk, UserData.CLONE);
		}

		public String getFK() {
			return (String) this.getUserDatum(JoinTree.FK);
		}

		public String toJoin() {
			return getEndpoints().getFirst() + "." + getFK() + "="
					+ getEndpoints().getSecond() + "." + getFK();
		}

}