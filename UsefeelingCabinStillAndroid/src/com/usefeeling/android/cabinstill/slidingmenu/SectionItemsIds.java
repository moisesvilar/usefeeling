package com.usefeeling.android.cabinstill.slidingmenu;

public enum SectionItemsIds {

	PROFILE(101),
	VENUES(201),
	EVENTS(202),
	PROMOS(203),
	SETTINGS(301),
	ABOUT(302);
	
	private int id;
	
	private SectionItemsIds(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
}
