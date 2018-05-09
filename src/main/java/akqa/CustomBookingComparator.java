package akqa;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.TreeMap;

public class CustomBookingComparator implements Comparator<LocalTime>{

	private TreeMap<LocalTime, BookingStats> map;

    public CustomBookingComparator(TreeMap<LocalTime, BookingStats> map) {
        this.map = map;
    }
	
	@Override
	public int compare(LocalTime o1, LocalTime o2) {

		BookingStats stat1 = map.get(o1);
		BookingStats stat2 = map.get(o2);
		
		if(stat1.getRequestDateTime().compareTo(stat2.getRequestDateTime())>0){
			return 1;
		}
		else if(stat1.getRequestDateTime().compareTo(stat2.getRequestDateTime())<0) {
			return -1;
		}		
		return 0;
	}

}
