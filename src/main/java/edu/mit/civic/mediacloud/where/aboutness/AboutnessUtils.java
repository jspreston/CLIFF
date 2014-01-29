package edu.mit.civic.mediacloud.where.aboutness;

import java.util.HashMap;
import java.util.List;

import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.gazetteer.FeatureClass;
import com.bericotech.clavin.gazetteer.FeatureCode;
import com.bericotech.clavin.resolver.ResolvedLocation;

public class AboutnessUtils {

	public static HashMap<ResolvedLocation,Integer> getCityCounts(List<ResolvedLocation> resolvedLocations){     
        HashMap<ResolvedLocation,Integer> cityCounts = new HashMap<ResolvedLocation,Integer>();
        for (ResolvedLocation resolvedLocation: resolvedLocations){
            if(resolvedLocation.geoname.featureClass!=FeatureClass.P){
                continue;
            }
            
            if(!cityCounts.containsKey(resolvedLocation)){
                cityCounts.put(resolvedLocation, 0);
            }
            cityCounts.put(resolvedLocation, cityCounts.get(resolvedLocation)+1);
        }
        return cityCounts;
    }
	public static HashMap<String,Integer> getStateCounts(List<ResolvedLocation> resolvedLocations){     
        HashMap<String,Integer> stateCounts = new HashMap<String,Integer>();
        for (ResolvedLocation resolvedLocation: resolvedLocations){
            if(resolvedLocation.geoname.admin1Code==null){
                continue;
            }
            String state = resolvedLocation.geoname.admin1Code;
            state = state + ", "+ resolvedLocation.geoname.primaryCountryCode;
            if(!stateCounts.containsKey(state)){
                stateCounts.put(state, 0);
            }
            stateCounts.put(state, stateCounts.get(state)+1);
        }
        return stateCounts;
    }
	public static HashMap<CountryCode,Integer> getCountryCounts(List<ResolvedLocation> resolvedLocations){     
        HashMap<CountryCode,Integer> countryCounts = new HashMap<CountryCode,Integer>();
        for (ResolvedLocation resolvedLocation: resolvedLocations){
            if(resolvedLocation.geoname.primaryCountryCode==CountryCode.NULL){
                continue;
            }
            CountryCode country = resolvedLocation.geoname.primaryCountryCode;
            if(!countryCounts.containsKey(country)){
                countryCounts.put(country, 0);
            }
            countryCounts.put(country, countryCounts.get(country)+1);
        }
        return countryCounts;
    }
    public static HashMap<CountryCode,Integer> getScoredCountryCounts(List<ResolvedLocation> resolvedLocations, String text){     
        HashMap<CountryCode,Integer> countryCounts = new HashMap<CountryCode,Integer>();
        
        //This is a rough sentence parsing hack to deal with test data - doesn't take into account !? - re-do once we have MediaCloud sentences again
        //For testing different approaches to this strategy
        /* 
        int headlineAndFirstSentenceIdx = text.indexOf('.');
        int secondSentenceIdx = text.indexOf('.', headlineAndFirstSentenceIdx + 1);
        int thirdSentenceIdx = text.indexOf('.', secondSentenceIdx + 1);
        int fourthSentenceIdx = text.indexOf('.', thirdSentenceIdx + 1);
        int fifthSentenceIdx = text.indexOf('.', fourthSentenceIdx + 1);
        */
        for (ResolvedLocation resolvedLocation: resolvedLocations){
            if(resolvedLocation.geoname.primaryCountryCode==CountryCode.NULL){
                continue;
            }
            int position = resolvedLocation.location.position;
            int percent10 = text.length()/10;
            
            int points = 1;
            if( position <= percent10){
            	points = 2;	
            } 
            
            CountryCode country = resolvedLocation.geoname.primaryCountryCode;
            if(!countryCounts.containsKey(country)){
                countryCounts.put(country, 0);
            }
            countryCounts.put(country, countryCounts.get(country)+points);
        }
        return countryCounts;
    }
    public static HashMap<String,Integer> getScoredStateCounts(List<ResolvedLocation> resolvedLocations, String text){     
        HashMap<String,Integer> stateCounts = new HashMap<String,Integer>();
        
        for (ResolvedLocation resolvedLocation: resolvedLocations){
            if(resolvedLocation.geoname.admin1Code==null){
                continue;
            }
            int position = resolvedLocation.location.position;
            int percent10 = text.length()/10;
            
            int points = 1;
            if( position <= percent10){
            	points = 2;	
            } 
            
            String state = resolvedLocation.geoname.admin1Code;
            if(!stateCounts.containsKey(state)){
            	stateCounts.put(state, 0);
            }
            stateCounts.put(state, stateCounts.get(state)+points);
        }
        return stateCounts;
    }
}