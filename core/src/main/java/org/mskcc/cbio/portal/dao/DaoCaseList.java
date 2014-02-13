/** Copyright (c) 2012 Memorial Sloan-Kettering Cancer Center.
**
** This library is free software; you can redistribute it and/or modify it
** under the terms of the GNU Lesser General Public License as published
** by the Free Software Foundation; either version 2.1 of the License, or
** any later version.
**
** This library is distributed in the hope that it will be useful, but
** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
** documentation provided hereunder is on an "as is" basis, and
** Memorial Sloan-Kettering Cancer Center 
** has no obligations to provide maintenance, support,
** updates, enhancements or modifications.  In no event shall
** Memorial Sloan-Kettering Cancer Center
** be liable to any party for direct, indirect, special,
** incidental or consequential damages, including lost profits, arising
** out of the use of this software and its documentation, even if
** Memorial Sloan-Kettering Cancer Center 
** has been advised of the possibility of such damage.  See
** the GNU Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public License
** along with this library; if not, write to the Free Software Foundation,
** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
**/

package org.mskcc.cbio.portal.dao;

import org.mskcc.cbio.portal.model.*;

import java.sql.*;
import java.util.*;

/**
 * Data access object for patient_List table
 */
public class DaoCaseList {

	/**
	 * Adds record to patient_list table.
	 */
    public int addCaseList(CaseList caseList) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int rows;
        try {
            con = JdbcUtil.getDbConnection(DaoCaseList.class);

            pstmt = con.prepareStatement("INSERT INTO patient_list (`STABLE_ID`, `CANCER_STUDY_ID`, `NAME`, `CATEGORY`," +
                    "`DESCRIPTION`)" + " VALUES (?,?,?,?,?)");
            pstmt.setString(1, caseList.getStableId());
            pstmt.setInt(2, caseList.getCancerStudyId());
            pstmt.setString(3, caseList.getName());
            pstmt.setString(4, caseList.getCaseListCategory().getCategory());
            pstmt.setString(5, caseList.getDescription());
            rows = pstmt.executeUpdate();
   			int listListRow = addCaseListList(caseList, con);
   			rows = (listListRow != -1) ? (rows + listListRow) : rows;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(DaoCaseList.class, con, pstmt, rs);
        }
        
        return rows;
    }

	/**
	 * Given a case list by stable Id, returns a case list.
	 */
    public CaseList getCaseListByStableId(String stableId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getDbConnection(DaoCaseList.class);
            pstmt = con.prepareStatement
                    ("SELECT * FROM patient_list WHERE STABLE_ID = ?");
            pstmt.setString(1, stableId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                CaseList caseList = extractCaseList(rs);
                caseList.setCaseList(getCaseListList(caseList, con));
                return caseList;
            }
			return null;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(DaoCaseList.class, con, pstmt, rs);
        }
    }

	/**
	 * Given a case list ID, returns a case list.
	 */
    public CaseList getCaseListById(int id) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getDbConnection(DaoCaseList.class);
            pstmt = con.prepareStatement
                    ("SELECT * FROM patient_list WHERE LIST_ID = ?");
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                CaseList caseList = extractCaseList(rs);
				caseList.setCaseList(getCaseListList(caseList, con));
                return caseList;
            }
			return null;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(DaoCaseList.class, con, pstmt, rs);
        }
    }

	/**
	 * Given a cancerStudyId, returns all case list.
	 */
    public ArrayList<CaseList> getAllCaseLists( int cancerStudyId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getDbConnection(DaoCaseList.class);

            pstmt = con.prepareStatement
                    ("SELECT * FROM patient_list WHERE CANCER_STUDY_ID = ? ORDER BY NAME");
            pstmt.setInt(1, cancerStudyId);
            rs = pstmt.executeQuery();
            ArrayList<CaseList> list = new ArrayList<CaseList>();
            while (rs.next()) {
                CaseList caseList = extractCaseList(rs);
                list.add(caseList);
            }
			// get case list-list
			for (CaseList caseList : list) {
				caseList.setCaseList(getCaseListList(caseList, con));
			}
            return list;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(DaoCaseList.class, con, pstmt, rs);
        }
    }

	/**
	 * Returns a list of all case lists.
	 */
    public ArrayList<CaseList> getAllCaseLists() throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getDbConnection(DaoCaseList.class);
            pstmt = con.prepareStatement
                    ("SELECT * FROM patient_list");
            rs = pstmt.executeQuery();
            ArrayList<CaseList> list = new ArrayList<CaseList>();
            while (rs.next()) {
                CaseList caseList = extractCaseList(rs);
                list.add(caseList);
            }
			// get case list-list
			for (CaseList caseList : list) {
				caseList.setCaseList(getCaseListList(caseList, con));
			}
            return list;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(DaoCaseList.class, con, pstmt, rs);
        }
    }

    /**
	 * Given a case id, determines if it exists
	 */
    public boolean caseIDExists(String caseID) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Patient patient = DaoPatient.getPatientByStableId(caseID);
        try {
            con = JdbcUtil.getDbConnection(DaoCaseList.class);
            pstmt = con.prepareStatement
                    ("SELECT * FROM patient_list_list WHERE PATIENT_ID = ?");
            pstmt.setInt(1, patient.getInternalId());
            rs = pstmt.executeQuery();
            return (rs.next());
        } catch (NullPointerException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(DaoCaseList.class, con, pstmt, rs);
        }
    }

	/**
	 * Clears all records from patient list & patient_list_list.
	 */
    public void deleteAllRecords() throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getDbConnection(DaoCaseList.class);
            pstmt = con.prepareStatement("TRUNCATE TABLE patient_list");
            pstmt.executeUpdate();
            pstmt = con.prepareStatement("TRUNCATE TABLE patient_list_list");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(DaoCaseList.class, con, pstmt, rs);
        }
    }

	/**
	 * Given a case list, gets list id from patient_list table
	 */
	private int getCaseListId(CaseList caseList) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getDbConnection(DaoCaseList.class);
            pstmt = con.prepareStatement("SELECT LIST_ID FROM patient_list WHERE STABLE_ID=?");
            pstmt.setString(1, caseList.getStableId());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("LIST_ID");
            }
            return -1;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(DaoCaseList.class, con, pstmt, rs);
        }
	}

	/**
	 * Adds record to patient_list_list.
	 */
    private int addCaseListList(CaseList caseList, Connection con) throws DaoException {
		
	// get case list id
	int caseListId = getCaseListId(caseList);
	if (caseListId == -1) {
            return -1;
        }
        
        if (caseList.getCaseList().isEmpty()) {
            return 0;
        }

        PreparedStatement pstmt  ;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder("INSERT INTO patient_list_list (`LIST_ID`, `PATIENT_ID`) VALUES ");
            for (String caseId : caseList.getCaseList()) {
                Patient patient = DaoPatient.getPatientByStableId(caseId);
                sql.append("('").append(caseListId).append("','").append(patient.getInternalId()).append("'),");
            }
            sql.deleteCharAt(sql.length()-1);
            pstmt = con.prepareStatement(sql.toString());
            return pstmt.executeUpdate();
        } catch (NullPointerException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(rs);
        }
    }

	/**
	 * Given a case list object (thus case list id) gets case list list.
	 */
	private ArrayList<String> getCaseListList(CaseList caseList, Connection con) throws DaoException {

        PreparedStatement pstmt  ;
        ResultSet rs = null;
        try {
            pstmt = con.prepareStatement
                    ("SELECT * FROM patient_list_list WHERE LIST_ID = ?");
            pstmt.setInt(1, caseList.getCaseListId());
            rs = pstmt.executeQuery();
            ArrayList<String> patientIds = new ArrayList<String>();
            while (rs.next()) {
                Patient patient = DaoPatient.getPatientByInternalId(rs.getInt("PATIENT_ID"));
				patientIds.add(patient.getStableId());
			}
            return patientIds;
        } catch (NullPointerException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(rs);
        }
	}

	/**
	 * Given a result set, creates a case list object.
	 */
    private CaseList extractCaseList(ResultSet rs) throws SQLException {
        CaseList caseList = new CaseList();
        caseList.setStableId(rs.getString("STABLE_ID"));
        caseList.setCancerStudyId(rs.getInt("CANCER_STUDY_ID"));
        caseList.setName(rs.getString("NAME"));
        caseList.setCaseListCategory(CaseListCategory.get(rs.getString("CATEGORY")));
        caseList.setDescription(rs.getString("DESCRIPTION"));
        caseList.setCaseListId(rs.getInt("LIST_ID"));
        return caseList;
    }
}