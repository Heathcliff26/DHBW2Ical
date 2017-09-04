package de.heathcliff.DHBW2Ical;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class SIcal
 */
@WebServlet("/SIcal")
public class SIcal extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Logger log = Logger.getLogger(getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SIcal() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// get Parameters
		String id = request.getParameter("uid");
		String alarm = request.getParameter("alarm");
		
		log.debug("Got request");
		
		if (id != null) {
			// get ical file
			try {
				boolean useAlarm = (alarm != null);
				IcalIO icalIO = new IcalIO(id, getServletContext().getRealPath("/"), useAlarm);
				File icalFile = icalIO.getParsedIcal();
				
				// prepare file transfer
				response.setContentType("application/octet-stream");
				response.setHeader("Content-disposition","attachment; filename=vorlesungsplan.ics");
				
				// send ical to client
				OutputStream out = response.getOutputStream();
				FileInputStream in = new FileInputStream(icalFile);
				byte[] buffer = new byte[4096];
				int length;
				while ((length = in.read(buffer)) > 0){
					out.write(buffer, 0, length);
				}
				in.close();
				out.flush();
			} catch (Exception e) {
				log.error("Could not load ical", e);
				response.sendError(500);
			}
		} else {
			response.sendRedirect("/DHBW2Ical");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
