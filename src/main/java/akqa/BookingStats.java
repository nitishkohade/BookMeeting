package akqa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class BookingStats {
	
	//Office duration
	private static LocalTime officeStartTime;
	private static LocalTime officeEndTime;	
	
	// Date and time for requesting the meeting by an employee
	private LocalDateTime requestDateTime;
	private String employeeID;
		
	//date and time and duration for booking meeting
	private LocalDate meetingDate;
	private LocalTime meetingTime;
	private LocalTime meetingEndTime;
	private LocalTime meetingDuration;
	
	public LocalTime getMeetingEndTime() {
		return meetingEndTime;
	}

	public void setMeetingEndTime(LocalTime meetingEndTime) {
		this.meetingEndTime = meetingEndTime;
	}
	
	public LocalDateTime getRequestDateTime() {
		return requestDateTime;
	}

	public void setRequestDateTime(LocalDateTime requestDateTime) {
		this.requestDateTime = requestDateTime;
	}
	
	public String getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
	}

	public LocalDate getMeetingDate() {
		return meetingDate;
	}

	public void setMeetingDate(LocalDate meetingDate) {
		this.meetingDate = meetingDate;
	}

	public LocalTime getMeetingTime() {
		return meetingTime;
	}

	public void setMeetingTime(LocalTime meetingTime) {
		this.meetingTime = meetingTime;
	}

	public LocalTime getMeetingDuration() {
		return meetingDuration;
	}

	public void setMeetingDuration(LocalTime meetingDuration) {
		this.meetingDuration = meetingDuration;
	}

	public BookingStats() {
		
	}
	
	public static LocalTime getOfficeStartTime() {
		return officeStartTime;
	}

	public static void setOfficeStartTime(LocalTime officeStartTime) {
		BookingStats.officeStartTime = officeStartTime;
	}

	public static LocalTime getOfficeEndTime() {
		return officeEndTime;
	}

	public static void setOfficeEndTime(LocalTime officeEndTime) {
		BookingStats.officeEndTime = officeEndTime;
	}
	
	public String toString() {
		return ""+requestDateTime+"|"+employeeID+"|"+meetingDate+"|"+
				meetingTime+"|"+meetingEndTime+"|"+meetingDuration;
	}
	
	
}
