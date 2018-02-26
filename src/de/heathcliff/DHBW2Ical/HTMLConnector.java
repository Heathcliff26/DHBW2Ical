package de.heathcliff.DHBW2Ical;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HTMLConnector {
	
	private static Logger log = Logger.getLogger(HTMLConnector.class);

	public static String getSelect() {
		Document doc;
		try {
			doc = Jsoup.connect("http://vorlesungsplan.dhbw-mannheim.de/ical.php").get();
		} catch (IOException e) {
			log.error("Could not load lessons", e);
			return "Error on loading lessons";
		}
		Element select = doc.getElementById("class_select");
		if (select != null) {
			return select.html();
		} else {
			log.error("Could not load lessons");
			return "Error on loading lessons";
		}
	}
}
