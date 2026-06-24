import vn.edu.hmu.dao.AdminDAO;
import vn.edu.hmu.model.ActionLog;
import java.sql.Timestamp;

public class TestInsertLog {
    public static void main(String[] args) {
        try {
            AdminDAO adminDAO = new AdminDAO();
            ActionLog log = new ActionLog();
            log.setAdminId(1);
            log.setTargetEmail("12345678");
            log.setActionType("SUSPEND");
            log.setReason("Test from script");
            log.setDetails("Test details");
            
            boolean ok = adminDAO.insertActionLog(log);
            if (ok) {
                System.out.println("INSERT SUCCESS!");
            } else {
                System.out.println("INSERT FAILED!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
