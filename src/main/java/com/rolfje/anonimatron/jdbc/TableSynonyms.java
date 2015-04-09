package com.rolfje.anonimatron.jdbc;

import java.util.ArrayList;
import java.util.List;

import com.rolfje.anonimatron.configuration.Table;
import com.rolfje.anonimatron.synonyms.Synonym;

public class TableSynonyms extends Table {
	List<List<Synonym>> synonymrows = new ArrayList<List<Synonym>>();
	
	public TableSynonyms(Table t) {
		this.setName(t.getName());
		this.setColumns(t.getColumns());
	}

	public void addSynonyms(List<Synonym> synonymrow){
		synonymrows.add(synonymrow);
	}
	
	public List<List<Synonym>> getSynonymrows() {
		return synonymrows;
	}
}
