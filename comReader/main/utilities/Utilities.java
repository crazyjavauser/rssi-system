package utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import data.Reading;


// TODO: Auto-generated Javadoc
/**
 * Class with helper methods for various tasks.
 */
public final class Utilities {

	/** The Constant RSSI_OFFSET. */
	private static final int RSSI_OFFSET = 77;
	/** Parameters used in formula for converting the RSSIdec value to RSSIdBm. */
	private static final int POSITIVE_NUMBER_LIMIT = 128;
	
	/** The Constant SUBTRAHEND. */
	private static final int SUBTRAHEND = 256;
	
	/**
	 *  All helper methods are static so there is no need for
	 *  instantiation of this class. Therefore, the constructor is private. 
	 */
	private Utilities() {
		
	}
	
	/**
	 * Convert RSSI decimal value to RSSI dBm.
	 *
	 * @param rssiDecimalValue rssi decimal value
	 * @return int RSSI dBm value
	 */
	public static double convertRSSIDecToDbm(double rssiDecimalValue) {
		
		double rssiDbm = 0;
		
		if (rssiDecimalValue >= POSITIVE_NUMBER_LIMIT) {
			rssiDbm = (rssiDecimalValue - SUBTRAHEND) / 2 - RSSI_OFFSET;
		} else {
			rssiDbm = (rssiDecimalValue) / 2 - RSSI_OFFSET;
		}
		
		return rssiDbm;
	}
	
	/**
	 * Some signal strengths in the reading can be too far from the median value.
	 * These values should not be considered for averaging. For example, in the list: 
	 * 32, 33, 25, 32 - value '25' is inappropriate
	 * 
	 * @param reading the reading
	 * @return the reading
	 */
	public static Reading removeInappropriateValues(Reading reading) {
		
		// TODO: remove statistically inappropriate values from the reading
		
		return reading;
	}
	
	/**
	 * Calculates average signal strength of a single reading. Every receiver
	 * takes several samples of signal strength. These values need to be
	 * averaged.
	 * 
	 * For the reading 32, 32 , 33, 32, result will be 32,25
	 *
	 * @param reading the reading
	 * @return double average signal strength
	 */
	public static double  calculateReadingAverage(Reading reading) {
		
		if (reading == null) {
			return 0;
		}
		
		double result = 0;
		
		ArrayList<Double> signalStrengths = reading.getSignalStrengths();
		
		for (int i = 0; i < signalStrengths.size(); i++) {
			result += signalStrengths.get(i);
		}
		
		result = result / signalStrengths.size();
		
		return result;
	}
	
	
	/**
	 * Calculates batch signal averages. When reading from the COM port, during a single time interval
	 * (e.g. 250ms), for every receiver, several signal strengths are obtained. These signal strengths are
	 * averaged before being passed to the position localization algorithm (DataProcessor class). 
	 *
	 * @param batch list of signal strengths for several watches and receivers
	 * @return Hash map with average signal strength for every watch and every receiver
	 */
	public static HashMap<Integer, HashMap<Integer, Double>> calculateBatchSignalAverages(ArrayList<Reading> batch) {
		HashMap<Integer, HashMap<Integer, ArrayList<Double>>> allData = new HashMap<Integer, HashMap<Integer, ArrayList<Double>>>();
		int watchId = 0;
		int receiverId = 0;
		double averageStrengthValue = 0;
		ArrayList<Integer> watchIds = new ArrayList<Integer>(); 
		
		// populate the three-dimensional HashMap with data 
		for (Reading reading : batch) {
			
			watchId = reading.getWatchId();
			receiverId = reading.getReceiverId();
			averageStrengthValue = reading.getAverageStrengthValue();
			
			// add watchId to the list for later use
			if (!watchIds.contains(watchId)) {
				watchIds.add(watchId);
			}
			
			if (allData.get(watchId) == null) {
				allData.put(watchId, new HashMap<Integer, ArrayList<Double>>());
			}
			
			if (allData.get(watchId).get(receiverId) == null) {
				
				allData.get(watchId).put(receiverId, new ArrayList<Double>());
			}
			
			allData.get(watchId).get(receiverId).add(averageStrengthValue);
			
		}
		
		// calculate averages
		HashMap<Integer, HashMap<Integer, Double>> averagedAllData = new HashMap<Integer, HashMap<Integer, Double>>();
		
		int watchIdsSize = watchIds.size();
		for (int i = 0; i < watchIdsSize; i++) {
			
			HashMap<Integer, ArrayList<Double>> hashMap = allData.get(i);
			
			for (Map.Entry<Integer, ArrayList<Double>> entry : hashMap.entrySet()) {
				int receiverId2 = entry.getKey();
			    
			    if (averagedAllData.get(i) == null) {
			    	averagedAllData.put(i, new HashMap<Integer, Double>());
			    }
			    
			    averagedAllData.get(i).put(receiverId2, calculateArrayListAverage(entry.getValue()));
			}
		}
		
		return averagedAllData;
	}
	
	
	/**
	 * Helper method that calculates average value of signal strengths in the list.
	 *
	 * @param list list with signal strengths
	 * @return average value
	 */
	private static Double calculateArrayListAverage(ArrayList<Double> list) {
		
		double result = 0;
		
		for (int i = 0; i < list.size(); i++) {
			result += list.get(i);
		}
		
		result = result / list.size();
		
		return result;
	}
	
}