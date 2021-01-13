package solution;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;

import baseclasses.DataLoadingException;
import baseclasses.IPassengerNumbersDAO;

/**
 * The PassengerNumbersDAO is responsible for loading an SQLite database
 * containing forecasts of passenger numbers for flights on dates
 */
public class PassengerNumbersDAO implements IPassengerNumbersDAO {
	
	private HashMap<String, Integer> passengerNumbers = new HashMap<String, Integer>();
	
	/**
	 * Returns the number of passenger number entries in the cache
	 * @return the number of passenger number entries in the cache
	 */
	@Override
	public int getNumberOfEntries() {
		// TODO Auto-generated method stub
		int numOfEntries = 0;
		for (@SuppressWarnings("unused") String k : passengerNumbers.keySet()) {
		    numOfEntries++;
		}
		return numOfEntries;
	}

	/**
	 * Returns the predicted number of passengers for a given flight on a given date, or -1 if no data available
	 * @param flightNumber The flight number of the flight to check for
	 * @param date the date of the flight to check for
	 * @return the predicted number of passengers, or -1 if no data available
	 */
	@Override
	public int getPassengerNumbersFor(int flightNumber, LocalDate date) {
		// TODO Auto-generated method stub
		int PassengerNumbersFor = -1;
		String key = date.toString()+"-"+Integer.toString(flightNumber);
		if(passengerNumbers.containsKey(key)) {
			PassengerNumbersFor = passengerNumbers.get(key);
		}
		return PassengerNumbersFor;	
	}

	/**
	 * Loads the passenger numbers data from the specified SQLite database into a cache for future calls to getPassengerNumbersFor()
	 * Multiple calls to this method are additive, but flight numbers/dates previously cached will be overwritten
	 * The cache can be reset by calling reset() 
	 * @param p The path of the SQLite database to load data from
	 * @throws DataLoadingException If there is a problem loading from the database
	 */
	@Override
	public void loadPassengerNumbersData(Path p) throws DataLoadingException {
		// TODO Auto-generated method stub
		String cURL = "jdbc:sqlite:" + p.toAbsolutePath();
		Connection c = null;
		
		try
		{
			//connect to DB
			c = DriverManager.getConnection(cURL);
			
			//run query
			Statement s = c.createStatement();
			s.setQueryTimeout(60);
			ResultSet rs = s.executeQuery("SELECT * FROM PassengerNumbers");
			
			//add results
			while (rs.next())
			{
				String key = (rs.getString("Date") + "-" + Integer.toString(rs.getInt("FlightNumber")));
				passengerNumbers.put(key, rs.getInt("Passengers"));
				//passengerNumbers.add(new PassengerNumbers(rs.getString("Date"), rs.getInt("FlightNumber"), rs.getInt("Passengers")));
			}
			
			c.close();
		}catch (Exception e)
		{
			System.err.println("Problem parsing SQL db: " + e);
			e.printStackTrace();
			throw new DataLoadingException(e); 	
		}
	}

	/**
	 * Removes all data from the DAO, ready to start again if needed
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		passengerNumbers.clear();
	}

}
