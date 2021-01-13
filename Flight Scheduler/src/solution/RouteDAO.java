package solution;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.xml.parsers.*;

import baseclasses.DataLoadingException;
import baseclasses.IRouteDAO;
import baseclasses.Route;

import org.w3c.dom.*;
import org.w3c.dom.Document;

/**
 * The RouteDAO parses XML files of route information, each route specifying
 * where the airline flies from, to, and on which day of the week
 */
public class RouteDAO implements IRouteDAO {
	ArrayList<Route> route = new ArrayList<Route>();

	/**
	 * Finds all flights that depart on the specified day of the week
	 * @param dayOfWeek A three letter day of the week, e.g. "Tue"
	 * @return A list of all routes that depart on this day
	 */
	@Override
	public List<Route> findRoutesByDayOfWeek(String dayOfWeek) {
		// TODO Auto-generated method stub
		List<Route> RoutesByDayOfWeek = new ArrayList<Route>();
		for(Route r1: route) 
		{
			if(r1.getDayOfWeek().equals(dayOfWeek))
			{
				RoutesByDayOfWeek.add(r1);
			}
		}
		return RoutesByDayOfWeek;
	}

	/**
	 * Finds all of the flights that depart from a specific airport on a specific day of the week
	 * @param airportCode the three letter code of the airport to search for, e.g. "MAN"
	 * @param dayOfWeek the three letter day of the week code to search for, e.g. "Tue"
	 * @return A list of all routes from that airport on that day
	 */
	@Override
	public List<Route> findRoutesByDepartureAirportAndDay(String airportCode, String dayOfWeek) {
		// TODO Auto-generated method stub
		List<Route> RoutesByDepartureAirportAndDay = new ArrayList<Route>();
		for(Route r1: route) 
		{
			if(r1.getDepartureAirportCode().equals(airportCode) && r1.getDayOfWeek().equals(dayOfWeek))
			{
				RoutesByDepartureAirportAndDay.add(r1);
			}
		}
		return RoutesByDepartureAirportAndDay;	
	}

	/**
	 * Finds all of the flights that depart from a specific airport
	 * @param airportCode the three letter code of the airport to search for, e.g. "MAN"
	 * @return A list of all of the routes departing the specified airport
	 */
	@Override
	public List<Route> findRoutesDepartingAirport(String airportCode) {
		// TODO Auto-generated method stub
		List<Route> RoutesDepartingAirport = new ArrayList<Route>();
		for(Route r1: route) 
		{
			if(r1.getDepartureAirportCode().equals(airportCode))
			{
				RoutesDepartingAirport.add(r1);
			}
		}
		return RoutesDepartingAirport;
	}

	/**
	 * Finds all of the flights that depart on the specified date
	 * @param date the date to search for
	 * @return A list of all routes that depart on this date
	 */
	@Override
	public List<Route> findRoutesbyDate(LocalDate date) {
		// TODO Auto-generated method stub
		List<Route> RoutesbyDate = new ArrayList<Route>();
		for(Route r1: route) 
		{
			if(r1.getDayOfWeek().equals(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault())))
			{
				RoutesbyDate.add(r1);
			}
		}
		return RoutesbyDate;
	}

	/**
	 * Returns The full list of all currently loaded routes
	 * @return The full list of all currently loaded routes
	 */
	@Override
	public List<Route> getAllRoutes() {
		// TODO Auto-generated method stub
		ArrayList<Route> routeCopy = new ArrayList<Route>();
		for(Route r: route) {
			routeCopy.add(r);
		}
		return routeCopy;
	}

	/**
	 * Returns The number of routes currently loaded
	 * @return The number of routes currently loaded
	 */
	@Override
	public int getNumberOfRoutes() {
		// TODO Auto-generated method stub
		int numOfRoutes = 0;
		for(@SuppressWarnings("unused") Route r1: route) 
		{
			numOfRoutes++;
		}
		return numOfRoutes;
	}

	/**
	 * Loads the route data from the specified file, adding them to the currently loaded routes
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
	 */
	@Override
	public void loadRouteData(Path p) throws DataLoadingException {
		// TODO Auto-generated method stub
		try {
			
			
			//load the XML
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			Document doc = db.parse(Files.newInputStream(p));
			
			Element root =  doc.getDocumentElement(); //get root element
			
			NodeList children = root.getChildNodes();
			for(int i=0;i<children.getLength();i++) {
				Route r1 = new Route();
				Node c = children.item(i);
				if(c.getNodeName().equals("Route")) {
					NodeList grandchildren = c.getChildNodes();
					for(int j=0;j<grandchildren.getLength();j++) {
						
						Node d = grandchildren.item(j);
						if (d.getNodeName().equals("FlightNumber")) {
							r1.setFlightNumber(Integer.parseInt(d.getChildNodes().item(0).getNodeValue()));
							//System.out.println(d.getChildNodes().item(0).getNodeValue());
						}
						if(d.getNodeName().equals("DayOfWeek")) {//check 1-7 throw data loading.
							r1.setDayOfWeek(d.getChildNodes().item(0).getNodeValue());
							if (!((r1.getDayOfWeek().equals("Mon")) || (r1.getDayOfWeek().equals("Tue")) || (r1.getDayOfWeek().equals("Wed")) || (r1.getDayOfWeek().equals("Thu")) || (r1.getDayOfWeek().equals("Fri")) || (r1.getDayOfWeek().equals("Sat")) || (r1.getDayOfWeek().equals("Sun")))) {
								throw new DataLoadingException();
							}
							//System.out.println(d.getChildNodes().item(0).getNodeValue());
						}
						if(d.getNodeName().equals("DepartureTime")) {
							DateTimeFormatter parser = DateTimeFormatter.ofPattern("HH:mm");
							LocalTime localTime = LocalTime.parse(d.getChildNodes().item(0).getNodeValue(), parser);
							r1.setDepartureTime(localTime);
							//System.out.println(d.getChildNodes().item(0).getNodeValue())
						}
						if(d.getNodeName().equals("DepartureAirport")) {
							r1.setDepartureAirport(d.getChildNodes().item(0).getNodeValue());
							//System.out.println(d.getChildNodes().item(0).getNodeValue());
						}
						if(d.getNodeName().equals("DepartureAirportCode")) {
							r1.setDepartureAirportCode(d.getChildNodes().item(0).getNodeValue());
							//System.out.println(d.getChildNodes().item(0).getNodeValue());
						}
						if(d.getNodeName().equals("ArrivalTime")) {
							DateTimeFormatter parser = DateTimeFormatter.ofPattern("HH:mm");
							LocalTime localTime = LocalTime.parse(d.getChildNodes().item(0).getNodeValue(), parser);
							r1.setArrivalTime(localTime);
							//System.out.println(d.getChildNodes().item(0).getNodeValue());
						}
						if(d.getNodeName().equals("ArrivalAirport")) {
							r1.setArrivalAirport(d.getChildNodes().item(0).getNodeValue());
							//System.out.println(d.getChildNodes().item(0).getNodeValue());
						}
						if(d.getNodeName().equals("ArrivalAirportCode")) {
							r1.setArrivalAirportCode(d.getChildNodes().item(0).getNodeValue());
							//System.out.println(d.getChildNodes().item(0).getNodeValue());
						}
						if(d.getNodeName().equals("Duration")) {
							r1.setDuration(Duration.parse(d.getChildNodes().item(0).getNodeValue()));
							//System.out.println(d.getChildNodes().item(0).getNodeValue());
						}
						
					}
					if(r1.getArrivalAirport() == null || r1.getArrivalAirportCode() == null || r1.getArrivalTime() == null || r1.getDayOfWeek() == null || r1.getDepartureAirport() == null || r1.getDepartureAirportCode() == null || r1.getDepartureTime() == null || r1.getDuration() == null || r1.getFlightNumber() == 0) {
						throw new DataLoadingException();
					}
					route.add(r1);
				}
			}
			
			
			
		} 
		catch (Exception e) { 
			System.err.println("Error opening XML file: " + e);
			e.printStackTrace();
			throw new DataLoadingException(e);
		}

	}

	/**
	 * Unloads all of the crew currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		route.clear();
	}

}
