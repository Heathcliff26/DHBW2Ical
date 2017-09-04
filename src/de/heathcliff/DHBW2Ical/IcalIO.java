package de.heathcliff.DHBW2Ical;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Logger;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.property.DtStamp;;

public class IcalIO {
	
	private Logger log = Logger.getLogger(getClass());
	
	private int refreshRate;
	
	private String id;
	private String tempDirPath;
	private boolean useAlarm;
	
	private File parsedIcal;
	
	private TimeZone timezone;

	public IcalIO(String id, String path, boolean useAlarm) throws Exception {
		// read db.properties
		Properties prop = new Properties();
		InputStream inpStream = new FileInputStream(path + "WEB-INF/classes/dhbw-ical2ical.properties");
		prop.load(inpStream);
		refreshRate = Integer.parseInt((String) prop.get("refreshRate"));
		
		// initialize values
		this.id = id;
		this.tempDirPath = path + "/cache/";
		this.useAlarm = useAlarm;
		
		// log values for debug
		log.debug("ID: " + this.id);
		log.debug("Cache: " + this.tempDirPath);
		log.debug("Use alarm: " + this.useAlarm);
		
		// check if tempDir exists
		File tmpDir = new File(getTempDirPath());
		if (!tmpDir.exists()) {
			tmpDir.mkdir();
		}
		
		// load file
		if (this.useAlarm) {
			this.parsedIcal = new File(tempDirPath + "vorlesungsplan_" + id + "_alarm.ics");
		} else {
			this.parsedIcal = new File(tempDirPath + "vorlesungsplan_" + id + ".ics");
		}
		if (parsedIcal.exists() && (((new Date().getTime()) - parsedIcal.lastModified()) < ((long) (refreshRate * 60 * 1000)))) {
			log.debug("Use old File");
		} else {
			log.debug("parse ical");
			parseIcal(getDHBWIcal());
		}
	}
	
	private InputStream getDHBWIcal() throws Exception {
		return new URL("http://vorlesungsplan.dhbw-mannheim.de/ical.php?uid=" + getId()).openStream();
	}
	
	private void parseIcal(InputStream input) throws Exception {
		// get calendar
		CalendarBuilder builder = new CalendarBuilder();
		Calendar dhbwCalendar = builder.build(input);
		input.close();
		
		// get timezone
		this.timezone = TimeZoneRegistryFactory.getInstance().createRegistry().getTimeZone("Europe/Berlin");
		
		// create new fixed calendar
		Calendar newCalendar = new Calendar();
		newCalendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
		newCalendar.getProperties().add(Version.VERSION_2_0);
		newCalendar.getProperties().add(CalScale.GREGORIAN);
		VTimeZone tz = timezone.getVTimeZone();
		newCalendar.getComponents().add(tz);
		
		// copy events
		Iterator i = dhbwCalendar.getComponents().iterator();
		while (i.hasNext()) {
			Component component = (Component) i.next();
			if (component.getName().equals("VEVENT")) {
				// get times
				DateTime dtstart = getDateTime(component, "DTSTART");
				DateTime dtend = getDateTime(component, "DTEND");
				DateTime dtstamp = getDateTime(component, "DTSTAMP");
				
				// get other values
				String description = component.getProperty("SUMMARY").getValue();
				String uidValue = UUID.randomUUID() + "@group-e.dhbw-mannheim.de";
				
				// create new event in new calendar
				VEvent event = new VEvent(dtstart, dtend, description);
				event.getProperties().add(new DtStamp(dtstamp));
				event.getProperties().add(new Uid(uidValue));
				event.getProperties().add(new Location(component.getProperty("LOCATION").getValue()));
				if (useAlarm) {
					// get alarm time
					java.util.Calendar cal = java.util.Calendar.getInstance();
					cal.setTime(dtstart);
					cal.add(java.util.Calendar.DAY_OF_MONTH, -1);
					cal.set(java.util.Calendar.HOUR_OF_DAY, 20);
					cal.set(java.util.Calendar.MINUTE, 0);
					DateTime dtreminder = new DateTime(cal.getTime());
					
					// create alarm
					VAlarm alarm = new VAlarm(dtreminder);
					// add description
					alarm.getAction().setValue("DISPLAY");
					alarm.getDescription().setValue(description);
					
					// validate alarm
					alarm.validate();
					// add alarm to event
					event.getAlarms().add(alarm);
				}
				// validate event
				event.validate();
				
				// add event to calendar
				newCalendar.getComponents().add(event);
			}
		}
		
		// write calendar
		FileOutputStream icalOut = new FileOutputStream(parsedIcal);
		CalendarOutputter output = new CalendarOutputter();
		output.setValidating(true);
		output.output(newCalendar, icalOut);
		icalOut.close();
	}
	
	private DateTime getDateTime(Component component, String propName) throws Exception {
		DateTime dt = new DateTime(component.getProperty(propName).getValue());
		dt.setUtc(true);
		return dt;
	}
	
	public int getRefreshRate() {
		return this.refreshRate;
	}
	
	public String getTempDirPath() {
		return this.tempDirPath;
	}
	
	public String getId() {
		return this.id;
	}
	
	public File getParsedIcal() {
		return this.parsedIcal;
	}
	
	public boolean usesAlarm() {
		return this.useAlarm;
	}

}
