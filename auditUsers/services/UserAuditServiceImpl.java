package services;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCollection;

import dao.MongoClientPool;

/**
 * Created by mandeepsingh on 16/06/18.
 */
public class UserAuditServiceImpl implements UserAuditService {
    private static Logger logger = LoggerFactory.getLogger(UserAuditServiceImpl.class.getName());

    private UserAuditServiceImpl() {
    }

    private static class UserAuditSingletonHelper {
        private static final UserAuditServiceImpl INSTANCE = new UserAuditServiceImpl();
    }

    public static UserAuditServiceImpl getInstance() {
        return UserAuditSingletonHelper.INSTANCE;
    }

    public Boolean saveAuditAction(String userId, String action) throws Exception {

       // MongoCollection<Document> userAuditCollection = MongoClientPool.getUserAuditCollection();

        //Document audit = new Document("user", userId).append("action", action).append("timeInMillis",System.currentTimeMillis());
        logger.info("saving audit log for: " + userId);
        try {
           // userAuditCollection.insertOne(audit);
        } catch (ClassCastException e) {
            logger.error("ClassCastException while saving log", e);
            throw new ClassCastException(e.getMessage());
        } catch (Exception e) {
            logger.error("Exception while saving audit trail", e);
            throw new Exception(e.getMessage());
        }
        return true;
    }
}
