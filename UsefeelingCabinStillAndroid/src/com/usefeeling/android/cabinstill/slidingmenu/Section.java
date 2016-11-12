package com.usefeeling.android.cabinstill.slidingmenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Sección del menú lateral.
 * @author Moisés Vilar.
 *
 */
public class Section implements ISection {

	private String title;
    private List<ISectionItem> sectionItems = new ArrayList<ISectionItem>();

    public Section(String title) {
        this.title = title;
    }
	
	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void addSectionItem(long id, String title, int icon) {
		this.sectionItems.add(new SectionItem(id, title, icon));
	}

	@Override
	public List<ISectionItem> getSectionItems() {
		return this.sectionItems;
	}

	@Override
	public void setSectionItems(List<ISectionItem> sectionItems) {
		this.sectionItems = sectionItems;
	}

}
