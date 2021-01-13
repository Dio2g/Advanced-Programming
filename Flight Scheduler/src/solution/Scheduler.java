package solution;

import java.time.LocalDate;
import java.util.List;

import baseclasses.Aircraft;
import baseclasses.CabinCrew;
import baseclasses.DoubleBookedException;
import baseclasses.FlightInfo;
import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IRouteDAO;
import baseclasses.IScheduler;
import baseclasses.InvalidAllocationException;
import baseclasses.Pilot;
import baseclasses.Schedule;
import baseclasses.SchedulerRunner;

public class Scheduler implements IScheduler {
	
	@Override
	public Schedule generateSchedule(IAircraftDAO aircraftDAO, ICrewDAO crewDAO, IRouteDAO routeDAO, IPassengerNumbersDAO passengerDAO,
			LocalDate startDate, LocalDate endDate) {
		
		Schedule s = new Schedule(routeDAO, startDate, endDate);
		int lastRemainingAllocations = 0;
		int counter = 0;
		List<Aircraft> aircrafts = aircraftDAO.getAllAircraft();
		List<Pilot> pilots = crewDAO.getAllPilots();
		List<CabinCrew> cabincrew = crewDAO.getAllCabinCrew();
		//List<Route> routes = routeDAO.getAllRoutes();
		s.getCompletedAllocations();
		while(!s.isCompleted()) {
			
			List<FlightInfo> remainingAllocations = s.getRemainingAllocations();
			
			for(FlightInfo flight : remainingAllocations) {
				boolean test = false;
				
				int counter1 = 0;
				
				
				while(!test) {
					try {
						s.allocateAircraftTo(aircrafts.get(counter1), flight);
						test = true;
					} catch (DoubleBookedException e1) {
						counter1++;
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					}
				}
				
				
				test = false;
				counter1 = 0;
				while(!test) {
					try {
						s.allocateCaptainTo(pilots.get(counter1), flight);
						test = true;
					} catch (DoubleBookedException e1) {
						counter1++;
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					}
				}
				
				test = false;
				counter1 = 0;
				while(!test) {
					try {
						s.allocateFirstOfficerTo(pilots.get(counter1), flight);
						test = true;
					} catch (DoubleBookedException e1) {
						counter1++;
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					}
				}
				
				test = false;
				counter1 = 0;
				int allocated = 0;
				while(!test) {
					try {
						//for(int j = 0; j< s.getAircraftFor(flight).getCabinCrewRequired(); j++) {
							
							s.allocateCabinCrewTo(cabincrew.get(counter1), flight);
							allocated++;
						//}
							if(allocated == s.getAircraftFor(flight).getCabinCrewRequired()) {
								test = true;
							}
					} catch (DoubleBookedException e1) {
						counter1++;
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					}
				}
				
					
				try {
					s.completeAllocationFor(flight);
				} catch (InvalidAllocationException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
				
				if(lastRemainingAllocations == s.getRemainingAllocations().size()) {
					counter++;
				}
				
				if(counter>=50000) {
				s = new Schedule(routeDAO, startDate, endDate);
				lastRemainingAllocations = 0;
				counter = 0;
				}
				
				lastRemainingAllocations = s.getRemainingAllocations().size();	
				
				System.out.println("Remaining Allocations: " + s.getRemainingAllocations().size());	
					
			}	
			
		}
			
		return s;
	}

	@Override
	public void setSchedulerRunner(SchedulerRunner arg0) {
		
		//runner = arg0;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		//runner.reportBestScheduleSoFar(bestSchedule);
	}

}
