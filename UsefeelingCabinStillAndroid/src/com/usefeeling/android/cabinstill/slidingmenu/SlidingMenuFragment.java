package com.usefeeling.android.cabinstill.slidingmenu;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.DataFacade;

/**
 * Fragment del menú lateral.
 * @author Moisés Vilar.
 *
 */
public class SlidingMenuFragment extends Fragment implements OnChildClickListener {

	private ExpandableListView sectionListView;
	private DataFacade dataFacade;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	this.dataFacade = new DataFacade(this.getActivity());
        //Creamos la lista del menú lateral
        List<ISection> sectionList = createMenu();
        //Inicializamos view
        View view = inflater.inflate(R.layout.slidingmenu_fragment, container, false);
        this.sectionListView = (ExpandableListView) view.findViewById(R.id.slidingmenu_view);
        this.sectionListView.setGroupIndicator(null);
        //Inicializamos adaptador
        SectionListAdapter sectionListAdapter = new SectionListAdapter(this.getActivity(), sectionList);
        this.sectionListView.setAdapter(sectionListAdapter); 
        //Establecemos listeners
        this.sectionListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
              @Override
              public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
              }
            });
        this.sectionListView.setOnChildClickListener(this);
        //Expandimos los grupos del adaptador
        int count = sectionListAdapter.getGroupCount();
        for (int position = 0; position < count; position++) {
            this.sectionListView.expandGroup(position);
        }
        //Devolvemos el view
        return view;
    }

    /**
     * Crea la lista del menú lateral.
     * @return
     */
    private List<ISection> createMenu() {
        List<ISection> sectionList = new ArrayList<ISection>();
        //Account Section
        Section section = new Section(this.getString(R.string.account));
        section.addSectionItem(SectionItemsIds.PROFILE.getId(), this.getString(R.string.profile), R.drawable.ic_slidingmenu_profile);
        sectionList.add(section);
        //Explore Section
        section = new Section(this.getString(R.string.explore));
        section.addSectionItem(SectionItemsIds.VENUES.getId(), this.getString(R.string.places), R.drawable.ic_slidingmenu_places);
        section.addSectionItem(SectionItemsIds.EVENTS.getId(), this.getString(R.string.events), R.drawable.ic_slidingmenu_events);
        section.addSectionItem(SectionItemsIds.PROMOS.getId(), this.getString(R.string.promos), R.drawable.ic_slidingmenu_promos);
        sectionList.add(section);
        //Aplication Section
        section = new Section(this.getString(R.string.application));
        section.addSectionItem(SectionItemsIds.SETTINGS.getId(), this.getString(R.string.settings), R.drawable.ic_slidingmenu_settings);
        section.addSectionItem(SectionItemsIds.ABOUT.getId(), this.getString(R.string.about), R.drawable.ic_slidingmenu_about);
        sectionList.add(section);
        return sectionList;
    }
	
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
    	if (id == SectionItemsIds.PROFILE.getId()) {
    		ApplicationFacade.showUserProfileActivity(this.getActivity(), this.dataFacade.getCurrentUser());
		} else if (id == SectionItemsIds.VENUES.getId()) {
			ApplicationFacade.showPlacesActivity(this.getActivity());
		} else if (id == SectionItemsIds.EVENTS.getId()) {
			ApplicationFacade.showEventsActivity(this.getActivity());
		} else if (id == SectionItemsIds.PROMOS.getId()) {
			ApplicationFacade.showPromosActivity(this.getActivity());
		} else if (id == SectionItemsIds.SETTINGS.getId()) {
			ApplicationFacade.showSettingsActivity(this.getActivity());
		} else if (id == SectionItemsIds.ABOUT.getId()) {
			ApplicationFacade.showAboutActivity(this.getActivity());
		}
        return false;
    }

}
