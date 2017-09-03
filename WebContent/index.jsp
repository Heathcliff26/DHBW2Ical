<%@page import="de.heathcliff.DHBW2Ical.HTMLConnector"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>DHBW2Ical</title>
<script type="text/javascript">
	//Redirect to correct download direction
	function call_ics() {
		var e = document.getElementById("class_select").selectedIndex;
		var url = document.getElementById("class_select").options[e].value;
		window.location.assign("http://heathcliff.eu/DHBW2Ical/ical?uid=" + url);
	}

	//Used to display the URL of the downloadable ICAL file after selecting a course
	function displayURL() {
		id = document.getElementById("class_select").value;
		url_string = "http://heathcliff.eu/DHBW2Ical/ical?uid="
				+ id;
		document.getElementById("input_id").value = url_string;
	}
</script>
</head>
<body>
	<div style="width: 200px; margin: 0 auto;">
		<form>
			<%=HTMLConnector.getSelect() %>
			<br />
			<input style="text-align:center; width: 500px" align="center" type="text" id="input_id" disabled="true" name="urlbox">
			<br />
			<button onClick="call_ics()">Download</button>
		</form>
	</div>
</body>
</html>