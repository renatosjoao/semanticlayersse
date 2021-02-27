package org.home;

import java.util.List;
import org.home.model.ResultDocument;

public interface ISparqlService {

	
    public List<ResultDocument> LocalQuery(int currentPage, int numOfRecords);
    public List<ResultDocument> FederatedQuery(int currentPage, int numOfRecords);
    
	public int getNumberOfRows();
}
