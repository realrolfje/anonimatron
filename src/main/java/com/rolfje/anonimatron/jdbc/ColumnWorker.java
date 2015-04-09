package com.rolfje.anonimatron.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.rolfje.anonimatron.configuration.Column;

/**
 * Provides functionality for processing a single column value. By implementing
 * this interface, we can provide different behavior to the loop which runs
 * through all table/column values. This enables us to do a two-pass on the same
 * table with the same code, only replacing the implementation of this
 * interface.
 * 
 * @author rolf
 */
public interface ColumnWorker {

	/**
	 * @param results the resultset in which this worker runs. Can be used to
	 *            update the column value when anonymizing.
	 * @param column The column which was fetched from the configuration.
	 * @param columnType Overrides the column type from the configuration.
	 * @param databaseColumnValue The current value for this column.
	 * @param columnDisplaySize The displaysize according to the database for
	 *            this column.
	 * @return <code>true</code> if the columnworker is ready to process the
	 *         next value after this one, or <code>false</code> if the column
	 *         worker does not want to see more data for this column. If all
	 *         workers report <code>false</code> for all columns in a row, the
	 *         AnonimizerService will stop processing the rest of the resultset.
	 * @throws SQLException
	 */
	boolean processColumn(ResultSet results, Column column, String columnType, Object databaseColumnValue, int columnDisplaySize) throws SQLException;

}
