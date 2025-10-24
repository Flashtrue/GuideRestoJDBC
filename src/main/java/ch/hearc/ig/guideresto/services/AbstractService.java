package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.persistence.ConnectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractService {
    protected static final Logger logger = LogManager.getLogger();

    protected void executeInTransaction(Runnable operation) throws SQLException {
        Connection connection = ConnectionUtils.getConnection();
        try {
            connection.setAutoCommit(false);
            operation.run();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                logger.error("Erreur lors du rollback", ex);
            }
            throw e;
        }
    }
}