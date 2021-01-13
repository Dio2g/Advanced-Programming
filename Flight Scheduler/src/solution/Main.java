package solution;

import java.nio.file.Paths;

import baseclasses.DataLoadingException;
import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IRouteDAO;

/**
 * This class allows you to run the code in your classes yourself, for testing and development
 */
public class Main {

	public static void main(String[] args) {	
		IAircraftDAO aircraft = new AircraftDAO();
		ICrewDAO crew = new CrewDAO();
		IRouteDAO route = new RouteDAO();
		IPassengerNumbersDAO passengers = new PassengerNumbersDAO();
		
		//Scheduler run = new Scheduler(aircraft, crew, route, passengers, );
		
		try {
			aircraft.loadAircraftData(Paths.get("./data/aircraft.csv"));
			
		}
		catch (DataLoadingException dle) {
			System.err.println("Error loading aircraft data");
			dle.printStackTrace();
		}
		
		
		try {
			crew.loadCrewData(Paths.get("./data/crew.json"));
			
		}
		catch (DataLoadingException dle) {
			System.err.println("Error loading crew data");
			dle.printStackTrace();
		}
		
		try {
			route.loadRouteData(Paths.get("./data/routes.xml"));
			
		}
		catch (DataLoadingException dle) {
			System.err.println("Error loading route data");
			dle.printStackTrace();
		}
		
		try {
			passengers.loadPassengerNumbersData(Paths.get("./data/mini_passengers.db"));
			
		}
		catch (DataLoadingException dle) {
			System.err.println("Error loading route data");
			dle.printStackTrace();
		}
			
		
	}

}
