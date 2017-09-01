package de.heathcliff.DHBW_ical2ical;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

public class IcalIO {
	
	private int refreshRate;
	
	private String id;
	private String tempDirPath;
	
	private File parsedIcal;

	public IcalIO(String id, String tempDirPath) throws Exception {
		// read db.properties
		Properties prop = new Properties();
		InputStream inpStream = getClass().getClassLoader().getResourceAsStream("dhbw-ical2ical.properties");
		prop.load(inpStream);
		refreshRate = (int) prop.get("refreshRate");
		
		// initialize values
		this.id = id;
		this.tempDirPath = tempDirPath;
		
		// load file
		File parsedIcal = new File(tempDirPath + "vorlesungsplan_" + id);
		if ((parsedIcal.lastModified() - (new Date().getTime())) < ((long) (refreshRate * 60 * 1000))) {
			this.parsedIcal = parsedIcal;
		} else {
			parseIcal(getDHBWIcal());
		}
	}
	
	private InputStream getDHBWIcal() throws Exception {
		return new URL("http://vorlesungsplan.dhbw-mannheim.de/ical.php?uid=" + getId()).openStream();
	}
	
	private void parseIcal(InputStream input) {
		
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
