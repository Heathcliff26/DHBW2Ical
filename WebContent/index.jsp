<%@page import="de.heathcliff.DHBW2Ical.HTMLConnector"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="/DHBW2Ical/res/main.css">
<link rel="shortcut icon" type="image/x-icon" href="/DHBW2Ical/res/favicon.ico">
<title>DHBW2Ical</title>
<script type="text/javascript">

	// form URL String
	function getURLString() {
		id = document.getElementById("class_select").value;
		url_string = "http://heathcliff.eu/DHBW2Ical/ical?uid=" + id;
		if (document.getElementById("alarm_check").checked) {
			url_string = url_string + "&alarm=true";
		}
		return url_string;
	}
	
	//Redirect to correct download direction
	function call_ics() {
		window.location.assign(getURLString());
	}

	//Used to display the URL of the downloadable ICAL file after selecting a course
	function displayURL() {
		document.getElementById("input_id").value = getURLString();
	}
</script>
</head>
<body>
	<h1>Herzlich Willkommen</h1>
	<p>Wählen sie einen Kurs aus und holen sie sich das Ical-File</p>
	<form>
		<select  onChange="displayURL()" id="class_select" name="Kurse" size="1" >
			<%=HTMLConnector.getSelect()%>
		</select>
		<br /><br />
		<input style="text-align: center; width: 500px" align="center"
			type="text" id="input_id" disabled="true" name="urlbox"><br />
		<br />
		<label><input type="checkbox" id="alarm_check" name="alarm" onchange="displayURL()">Mit Alarmen</label><br />
		<br />
		<button type="button" onClick="call_ics()">Download</button>
	</form>
</body>
</html>