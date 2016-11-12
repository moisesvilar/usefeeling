package com.usefeeling.android.cabinstill.slidingmenu;

import java.util.List;

public interface ISection {

	String getTitle();
	void setTitle(String title);
	void addSectionItem(long id, String title, int icon);
	List<ISectionItem> getSectionItems();
	void setSectionItems(List<ISectionItem> sectionItems);
	
}
