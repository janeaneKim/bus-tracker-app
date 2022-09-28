package com.tts.transitapp.model;

import java.util.Comparator;

// NOT USED
public class BusComparator implements Comparator<Bus> {

	@Override
	public int compare(Bus bus1, Bus bus2) {
        if (bus1.distance < bus2.distance) {
        	return -1;
        }
        if (bus1.distance > bus2.distance) {
        	return 1;
        }
        return 0;
	}
}