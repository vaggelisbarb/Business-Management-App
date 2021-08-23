package com.iNNOS.queryprocessor;

import java.sql.SQLException;

import com.iNNOS.model.*;

public interface IQueryProcessor {
	// Ενημέρωση της βάσης με το καινούριο έργο
	public boolean insertIntoProjectTable(Project project);

	public void insertIntoClientTable(Client client) throws SQLException;
}
