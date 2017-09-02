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

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

public class IcalIO {
	
	private int refreshRate;
	
	private String id;
	private String tempDirPath;
	
	private File parsedIcal;
	
	private TimeZone timezone;

	public IcalIO(String id, String path) throws Exception {
		// read db.properties
		Properties prop = new Properties();
		InputStream inpStream = new FileInputStream(path + "WEB-INF/classes/dhbw-ical2ical.properties");
		prop.load(inpStream);
		refreshRate = Integer.parseInt((String) prop.get("refreshRate"));
		
		// initialize values
		this.id = id;
		this.tempDirPath = path + "/cache/";
		
		// load file
		File parsedIcal = new File(tempDirPath + "vorlesungsplan_" + id + ".ics");
		if (parsedIcal.exists() && (((new Date().getTime()) - parsedIcal.lastModified()) < ((long) (refreshRate * 60 * 1000)))) {
			this.parsedIcal = parsedIcal;
		} else {
			System.out.println("Parse ICAL");
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
		System.out.println(timezone.getID());
		
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
				
				// create new event in new calendar
				VEvent event = new VEvent(dtstart, dtend, component.getProperty("SUMMARY").getValue());
				String uidValue = UUID.randomUUID() + "@group-e.dhbw-mannheim.de";
				event.getProperties().add(new Uid(uidValue));
				event.getProperties().add(new Location(component.getProperty("LOCATION").getValue()));
				newCalendar.getComponents().add(event);
			}
		}
		
		// write calendar
		File parsedIcal = new File(tempDirPath + "vorlesungsplan_" + id + ".ics");
		FileOutputStream icalOut = new FileOutputStream(parsedIcal);
		CalendarOutputter output = new CalendarOutputter();
		output.setValidating(true);
		output.output(newCalendar, icalOut);
		icalOut.close();
	}
	
	private DateTime getDateTime(Component component, String propName) throws Exception {
		DateTime dt = new DateTime(component.getProperty(propName).getValue());
		dt.setTimeZone(timezone);
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

}
