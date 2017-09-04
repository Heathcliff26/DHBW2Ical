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
	//Redirect to correct download direction
	function call_ics() {
		id = document.getElementById("class_select").value;
		url_string = "http://heathcliff.eu/DHBW2Ical/ical?uid=" + id;
		window.location.assign(url_string);
	}

	//Used to display the URL of the downloadable ICAL file after selecting a course
	function displayURL() {
		id = document.getElementById("class_select").value;
		url_string = "http://heathcliff.eu/DHBW2Ical/ical?uid=" + id;
		document.getElementById("input_id").value = url_string;
	}
</script>
</head>
<body>
	<h1>Herzlich Willkommen</h1>
	<p>W�hlen sie einen Kurs aus und holen sie sich das Ical-File</p>
	<form>
		<%=HTMLConnector.getSelect()%>
		<br /> <input style="text-align: center; width: 500px" align="center"
			type="text" id="input_id" disabled="true" name="urlbox"> <br />
		<button type="button" onClick="call_ics()">Download</button>
	</form>
</body>
</html>