package akqa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * @author nitish
 *
 */
public class BookMeeting {
	
	private static TreeMap<LocalDate, TreeMap> bookedMeetings = new TreeMap(); //Booked meeting with date and time, duration, employeeid 
	private static File inputFile = new File(SourceFile.INPUTFILE.getSourceFile()); // Input file
	private static File outputFile = new File(SourceFile.OUTPUTFILE.getSourceFile()); // Output file
	
	public static void main(String[] args) {
		try {
			generateBookedMeetings(parseBookingRequest(inputFile));
			eliminateOverLapMeeting();
			sortBookedMeetingsChronologically();
			getBookedTimings();
			
			bookedMeetings = null;

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	   * This is the method which prints the output to the file.
	   * @return Nothing.
	   * @exception FileNotFoundException On filenot found.
	   * 
	   */
	public static void getBookedTimings() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter output = new PrintWriter(outputFile, "UTF-8");
		Iterator itrD = bookedMeetings.keySet().iterator();
		while(itrD.hasNext()) {
			LocalDate bookedDate = (LocalDate)itrD.next();
			output.println(bookedDate);
			Iterator itrT = bookedMeetings.get(bookedDate).keySet().iterator();
			while(itrT.hasNext()) {
				LocalTime bookedTime = (LocalTime)itrT.next();
				BookingStats stat = (BookingStats) bookedMeetings.get(bookedDate).get(bookedTime);
				output.print(bookedTime+" ");
				output.print(stat.getMeetingEndTime()+" ");
				output.print(stat.getEmployeeID());
				output.println();
			}
		}
		output.close();
	}

	/**
	   * This is to sort the booked meetings according to the timings order
	   * @return Nothing.
	   */
	public static void sortBookedMeetingsChronologically() {
		//It is to iterate over tree map
		Iterator itrD = bookedMeetings.keySet().iterator();
		while(itrD.hasNext()) {
			LocalDate date = (LocalDate)itrD.next();			
			TreeMap<LocalTime, BookingStats> temp = bookedMeetings.get(date);
			TreeMap<LocalTime, BookingStats> timeMap = new TreeMap<LocalTime, BookingStats>(new Comparator<LocalTime>() {
				@Override
				public int compare(LocalTime lt1, LocalTime lt2) {
					if(lt1.compareTo(lt2)>0){
						return 1;
					}
					else if(lt1.compareTo(lt2)<0) {
						return -1;
					}		
					return 0;
				}
			});
			timeMap.putAll(temp);
			bookedMeetings.put(date, timeMap);
		}
	}

	/**
	   * This is tp eliminate overlapping meetings 
	   * @return Nothing.
	   */
	public static void eliminateOverLapMeeting() {
		
		Iterator itrD = bookedMeetings.keySet().iterator();
		while(itrD.hasNext()) {
			LocalDate date = (LocalDate)itrD.next();			
			TreeMap<LocalTime, BookingStats> temp = bookedMeetings.get(date);
			TreeMap<LocalTime, BookingStats> timeMap = new TreeMap<LocalTime, BookingStats>(new CustomBookingComparator(temp));
			timeMap.putAll(temp);
			bookedMeetings.put(date, timeMap);
			Iterator itrT = bookedMeetings.get(date).keySet().iterator();
			LocalTime previousTime = null; // holds the past booked time
			while(itrT.hasNext()) {
				LocalTime currentTime = (LocalTime)itrT.next();	 // holds the current time
				if(previousTime != null && 
						(
								(timeMap.get(currentTime).getMeetingTime().isAfter(timeMap.get(previousTime).getMeetingTime())
										&&
										timeMap.get(currentTime).getMeetingTime()
										.isBefore(timeMap.get(previousTime).getMeetingTime()
												.plusHours(timeMap.get(previousTime).getMeetingDuration().getHour())
												.plusMinutes(timeMap.get(previousTime).getMeetingDuration().getMinute())))
								|| 
								(timeMap.get(previousTime).getMeetingTime().isAfter(timeMap.get(currentTime).getMeetingTime())
										&&
										timeMap.get(previousTime).getMeetingTime()
										.isBefore(timeMap.get(currentTime).getMeetingTime()
												.plusHours(timeMap.get(currentTime).getMeetingDuration().getHour())
												.plusMinutes(timeMap.get(currentTime).getMeetingDuration().getMinute())))
								)
						){
					timeMap.remove(currentTime);					
				}
				else
				{
					timeMap.get(currentTime).setMeetingEndTime(timeMap.get(currentTime).getMeetingTime().plusHours(timeMap.get(currentTime).getMeetingDuration().getHour()).plusMinutes(timeMap.get(currentTime).getMeetingDuration().getMinute()));
				}
				previousTime = currentTime;
			}
		}
	}

	/**
	   * This is to read the file of requested booking meetings
	   * @return HashMap
	   * @input File file
	   */
	public static HashMap parseBookingRequest(File file) throws IOException {
		HashMap<LocalDate, HashMap> map = new HashMap();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String[] officeHours  = br.readLine().split("\\s+");
		BookingStats.setOfficeStartTime(LocalTime.parse(officeHours[0]));
		BookingStats.setOfficeEndTime(LocalTime.parse(officeHours[1]));
		String line;
		int n = 0;
		while((line = br.readLine())!=null) {
			n=0;
			BookingStats stat = new BookingStats();
			for(int i=0;i<2;i++) {
				n++;
				StringTokenizer st = new StringTokenizer(line);
				while(st.hasMoreTokens()){
					if(n==1) {
						stat.setRequestDateTime(LocalDateTime.parse(st.nextToken()+"T"+st.nextToken()));
						stat.setEmployeeID(st.nextToken());
						line = br.readLine();
					}
					else {
						stat.setMeetingDate(LocalDate.parse(st.nextToken()));
						stat.setMeetingTime(LocalTime.parse(st.nextToken()));
						stat.setMeetingDuration(LocalTime.parse(st.nextToken()));
					}
				}
			}
			runConstraintCheck(stat, map);
		}
		return map;
	}
	
	/**
	   * This is to fulfill the booking based on certain conditions
	   * @return Nothing.
	   * @input BookingStats stat
	   * @input HashMap map
	   */
	public static void runConstraintCheck(BookingStats stat, HashMap<LocalDate, HashMap> map) {
		LocalDate meetingDate = stat.getMeetingDate();
		LocalTime meetingTime = stat.getMeetingTime();
		LocalTime meetingDuration = stat.getMeetingDuration();
		Boolean booleanRange = meetingTime.compareTo(BookingStats.getOfficeStartTime())>=0 && 
				meetingTime.compareTo(BookingStats.getOfficeEndTime())<=0 && 
				meetingTime.plusHours(meetingDuration.getHour()).plusMinutes(meetingDuration.getMinute()).compareTo(BookingStats.getOfficeStartTime())>=0 && 
				meetingTime.plusHours(meetingDuration.getHour()).plusMinutes(meetingDuration.getMinute()).compareTo(BookingStats.getOfficeEndTime())<=0;
				if(map.containsKey(meetingDate)) {
					HashMap<LocalTime, BookingStats> bookingMap = map.get(meetingDate);
					Boolean bool = bookingMap.containsKey(meetingTime);
					if(bool && booleanRange && stat.getRequestDateTime().compareTo(bookingMap.get(meetingTime).getRequestDateTime())<0) {
						bookingMap.put(meetingTime, stat);
						map.put(meetingDate, bookingMap);
					}
					else if(bool==false && booleanRange){
						bookingMap.put(meetingTime, stat);
						map.put(meetingDate, bookingMap);
					}
				}
				else if(booleanRange){
					HashMap<LocalTime, BookingStats> bookingMap = new HashMap();
					bookingMap.put(meetingTime, stat);
					map.put(meetingDate, bookingMap);
				}
	}

	/**
	   * This is to generate meetings in one single collection from file
	   * @return TreeMap
	   * @input HashMap map
	   */
	public static TreeMap generateBookedMeetings(HashMap<LocalDate, HashMap> map) {
		Iterator itr = map.keySet().iterator();
		while(itr.hasNext()) {
			LocalDate date = (LocalDate)itr.next();
			TreeMap<LocalTime, BookingStats> stats = new TreeMap(map.get(date));
			bookedMeetings.put(date, stats);
		}
		return bookedMeetings;
	}
}
