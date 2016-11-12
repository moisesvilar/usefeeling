package com.usefeeling.android.cabinstill.slidingmenu;

/**
 * Elemento dentro de sección de menú lateral.
 * @author Moisés Vilar.
 *
 */
public class SectionItem implements ISectionItem {

	private long id;
    private String title;
    private int icon;

    public SectionItem(long id, String title, int icon) {
        this.id = id;
        this.title = title;
        this.icon = icon;
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
	public int getIcon() {
		return this.icon;
	}

	@Override
	public void setIcon(int icon) {
		this.icon = icon;
	}

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

}
