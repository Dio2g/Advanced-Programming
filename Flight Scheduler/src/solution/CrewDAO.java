package solution;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import baseclasses.CabinCrew;
import baseclasses.Crew;
import baseclasses.DataLoadingException;
import baseclasses.ICrewDAO;
import baseclasses.Pilot;
import baseclasses.Pilot.Rank;

/**
 * The CrewDAO is responsible for loading data from JSON-based crew files 
 * It contains various methods to help the scheduler find the right pilots and cabin crew
 */
public class CrewDAO implements ICrewDAO {
	
	ArrayList<Pilot> pilot = new ArrayList<Pilot>();
	ArrayList<CabinCrew> crew = new ArrayList<CabinCrew>();
	ArrayList<Crew> allCrew = new ArrayList<Crew>();

	/**
	 * Loads the crew data from the specified file, adding them to the currently loaded crew
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
	 */
	@Override
	public void loadCrewData(Path p) throws DataLoadingException {
		// TODO Auto-generated method stub
		try {
			
			BufferedReader br = Files.newBufferedReader(p);
			String json = ""; String line = "";
			while((line = br.readLine()) != null) { json = json + line; }
			
			JSONObject root = new JSONObject(json);
			JSONArray pilots = root.getJSONArray("pilots");
			JSONArray cabincrew =  root.getJSONArray("cabincrew");
			
			for(int i=0;i<pilots.length();i++) {
				Pilot p1 = new Pilot();
				String forename = pilots.getJSONObject(i).getString("forename");
				String surname = pilots.getJSONObject(i).getString("surname");
				String homebase = pilots.getJSONObject(i).getString("homebase");
				String rank = pilots.getJSONObject(i).getString("rank");
				JSONObject types = pilots.getJSONObject(i);
				JSONArray typeRatings = types.getJSONArray("typeRatings");
				ArrayList<String> typeRatingsArray = new ArrayList<String>();
				for(int j=0;j<typeRatings.length();j++) {
					typeRatingsArray.add(j, typeRatings.getString(j));
					//System.out.println(typeRatingsArray);
					p1.setQualifiedFor(typeRatingsArray .get(j));
				}
				p1.setForename(forename);
				p1.setSurname(surname);
				p1.setHomeBase(homebase);
				p1.setRank(Rank.valueOf(rank.toUpperCase()));
				pilot.add(p1);
				allCrew.add(p1);
			}
			
			for(int i=0;i<cabincrew.length();i++) {
				CabinCrew c1 = new CabinCrew();
				String forename = cabincrew.getJSONObject(i).getString("forename");
				String surname = cabincrew.getJSONObject(i).getString("surname");
				String homebase = cabincrew.getJSONObject(i).getString("homebase");
				JSONObject types = cabincrew.getJSONObject(i);
				JSONArray typeRatings = types.getJSONArray("typeRatings");
				ArrayList<String> typeRatingsArray  = new ArrayList<String>();
				for(int j=0;j<typeRatings.length();j++) {
					typeRatingsArray .add(j, typeRatings.getString(j));
					//System.out.println(typeRatingsArray);
					c1.setQualifiedFor(typeRatingsArray.get(j));
				}
				c1.setForename(forename);
				c1.setSurname(surname);
				c1.setHomeBase(homebase);
				crew.add(c1);	
				allCrew.add(c1);
				
			}
			
		} 
		catch (Exception e) {
			System.err.println("Problem parsing JSON file: " + e);
			e.printStackTrace();
			throw new DataLoadingException(e); 	
		}
		
	}
	
	/**
	 * Returns a list of all the cabin crew based at the airport with the specified airport code
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the cabin crew based at the airport with the specified airport code
	 */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBase(String airportCode) {
		// TODO Auto-generated method stub
		List<CabinCrew> crewByHomebase = new ArrayList<CabinCrew>();
		for(CabinCrew c1: crew) 
		{
			if(c1.getHomeBase().equalsIgnoreCase(airportCode))
			{
				crewByHomebase.add(c1);
			}
		}
		return crewByHomebase;
	}

	/**
	 * Returns a list of all the cabin crew based at a specific airport AND qualified to fly a specific aircraft type
	 * @param typeCode the type of plane to find cabin crew for
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the cabin crew based at a specific airport AND qualified to fly a specific aircraft type
	 */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBaseAndTypeRating(String typeCode, String airportCode) {
		// TODO Auto-generated method stub
		List<CabinCrew> CabinCrewByHomeBaseAndTypeRating = new ArrayList<CabinCrew>();
		for(CabinCrew c1: crew) 
		{
			if(c1.getHomeBase().equalsIgnoreCase(airportCode) && c1.getTypeRatings().contains(typeCode))
			{
				CabinCrewByHomeBaseAndTypeRating.add(c1);
			}
		}
		return CabinCrewByHomeBaseAndTypeRating;	
	}

	/**
	 * Returns a list of all the cabin crew currently loaded who are qualified to fly the specified type of plane
	 * @param typeCode the type of plane to find cabin crew for
	 * @return a list of all the cabin crew currently loaded who are qualified to fly the specified type of plane
	 */
	@Override
	public List<CabinCrew> findCabinCrewByTypeRating(String typeCode) {
		// TODO Auto-generated method stub
		List<CabinCrew> CabinCrewByTypeRating = new ArrayList<CabinCrew>();
		for(CabinCrew c1: crew) 
		{
			if(c1.getTypeRatings().contains(typeCode))
			{
				CabinCrewByTypeRating.add(c1);
			}
		}
		return CabinCrewByTypeRating;	
	}

	/**
	 * Returns a list of all the pilots based at the airport with the specified airport code
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the pilots based at the airport with the specified airport code
	 */
	@Override
	public List<Pilot> findPilotsByHomeBase(String airportCode) {
		// TODO Auto-generated method stub
		List<Pilot> PilotsByHomeBase = new ArrayList<Pilot>();
		for(Pilot p1: pilot) 
		{
			if(p1.getHomeBase().contains(airportCode))
			{
				PilotsByHomeBase.add(p1);
			}
		}
		return PilotsByHomeBase;	
	}

	/**
	 * Returns a list of all the pilots based at a specific airport AND qualified to fly a specific aircraft type
	 * @param typeCode the type of plane to find pilots for
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the pilots based at a specific airport AND qualified to fly a specific aircraft type
	 */
	@Override
	public List<Pilot> findPilotsByHomeBaseAndTypeRating(String typeCode, String airportCode) {
		// TODO Auto-generated method stub
		List<Pilot> PilotsByHomeBaseAndTypeRating = new ArrayList<Pilot>();
		for(Pilot p1: pilot) 
		{
			if(p1.getHomeBase().contains(airportCode) && p1.getTypeRatings().contains(typeCode))
			{
				PilotsByHomeBaseAndTypeRating.add(p1);
			}
		}
		return PilotsByHomeBaseAndTypeRating;	
	}

	/**
	 * Returns a list of all the pilots currently loaded who are qualified to fly the specified type of plane
	 * @param typeCode the type of plane to find pilots for
	 * @return a list of all the pilots currently loaded who are qualified to fly the specified type of plane
	 */
	@Override
	public List<Pilot> findPilotsByTypeRating(String typeCode) {
		// TODO Auto-generated method stub
		List<Pilot> PilotsByTypeRating = new ArrayList<Pilot>();
		for(Pilot p1: pilot) 
		{
			if(p1.getTypeRatings().contains(typeCode))
			{
				PilotsByTypeRating.add(p1);
			}
		}
		return PilotsByTypeRating;
	}

	/**
	 * Returns a list of all the cabin crew currently loaded
	 * @return a list of all the cabin crew currently loaded
	 */
	@Override
	public List<CabinCrew> getAllCabinCrew() {
		// TODO Auto-generated method stub
		ArrayList<CabinCrew> crewCopy = new ArrayList<CabinCrew>();
		for(CabinCrew c: crew) {
			crewCopy.add(c);
		}
		return crewCopy;
	}

	/**
	 * Returns a list of all the crew, regardless of type
	 * @return a list of all the crew, regardless of type
	 */
	@Override
	public List<Crew> getAllCrew() {
		// TODO Auto-generated method stub
		ArrayList<Crew> allCrewCopy = new ArrayList<Crew>();
		for(Crew c: allCrew) {
			allCrewCopy.add(c);
		}
		return allCrewCopy;
	}

	/**
	 * Returns a list of all the pilots currently loaded
	 * @return a list of all the pilots currently loaded
	 */
	@Override
	public List<Pilot> getAllPilots() {
		// TODO Auto-generated method stub
		ArrayList<Pilot> pilotCopy = new ArrayList<Pilot>();
		for(Pilot c: pilot) {
			pilotCopy.add(c);
		}
		return pilotCopy;
	}

	@Override
	public int getNumberOfCabinCrew() {
		// TODO Auto-generated method stub
		int numOfCabinCrew = 0;
		for(@SuppressWarnings("unused") CabinCrew c1: crew) 
		{
			numOfCabinCrew++;
		}
		return numOfCabinCrew;
	}

	/**
	 * Returns the number of pilots currently loaded
	 * @return the number of pilots currently loaded
	 */
	@Override
	public int getNumberOfPilots() {
		// TODO Auto-generated method stub
		int numOfPilots = 0;
		for(@SuppressWarnings("unused") Pilot p1: pilot) 
		{
			numOfPilots++;
		}
		return numOfPilots;
	}

	/**
	 * Unloads all of the crew currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		crew.clear();
		pilot.clear();
		allCrew.clear();
	}

}
