package vn.edu.hmu.test;

import vn.edu.hmu.dao.AdminDAO;
import vn.edu.hmu.model.ActionLog;
import vn.edu.hmu.dao.ArchiveDAO;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("TESTING DB INSERTS...");
        
        AdminDAO adminDAO = new AdminDAO();
        ActionLog log = new ActionLog(1, "test@hmu.edu.vn", "TEST_ACTION", "Testing reason", "{\"test\": \"details\"}");
        boolean logSuccess = adminDAO.insertActionLog(log);
        System.out.println("insertActionLog success: " + logSuccess);
        
        ArchiveDAO archiveDAO = new ArchiveDAO();
        boolean archiveSuccess = archiveDAO.insertArchiveM02("BAO_LUU", "QD-123", 1, "Nguyen Van A", "test@hmu.edu.vn", "SV123", "K1", 1);
        System.out.println("insertArchiveM02 success: " + archiveSuccess);
    }
}
