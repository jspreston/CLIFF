package org.mediameter.cliff.util;

import com.bericotech.clavin.extractor.LocationOccurrence;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.mediameter.cliff.extractor.ExtractedEntities;
import org.mediameter.cliff.extractor.OrganizationOccurrence;
import org.mediameter.cliff.extractor.PersonOccurrence;
import org.mediameter.cliff.extractor.SentenceLocationOccurrence;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class MuckUtils {

    public static ExtractedEntities entitiesFromNlpJsonString(String nlpJsonString){
        Map sentences = sentencesFromJsonString(nlpJsonString);
        return entitiesFromNlpSentenceMap(sentences);
    }

    public static Map sentencesFromJsonString(String nlpJsonString) {
        Gson gson = new Gson();
        Map content = gson.fromJson(nlpJsonString, Map.class);
        return content;
    }
        
    /**
     *  
     */
    private static ExtractedEntities entitiesFromNlpSentenceMap(Map mcSentences){
        ExtractedEntities entities = new ExtractedEntities();
        Iterator it = mcSentences.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            String storySentencesId = pairs.getKey().toString();
            if(storySentencesId.equals('_')){
                continue;
            }
            Map corenlp = (Map) pairs.getValue();
            List<Map> nlpSentences = (List<Map>) ((Map) corenlp.get("corenlp")).get("sentences");
            for(Map sentence:nlpSentences){ // one mc sentence could be multiple corenlp sentences
                String queuedEntityText = null;
                String lastEntityType = null;
                List<Map> tokens = (List<Map>) sentence.get("tokens");
                for (Map token : tokens){
                    String entityType = (String) token.get("ne"); 
                    String tokenText = (String) token.get("word");
                    if(entityType.equals(lastEntityType)){
                        queuedEntityText+= " "+tokenText;
                    } else {
                        if(queuedEntityText!=null && lastEntityType!=null){
                            //TODO: figure out if we need the character index here or not
                            switch(lastEntityType){
                            case "PERSON":
                                entities.addPerson(new PersonOccurrence(queuedEntityText, 0));
                                break;
                            case "LOCATION":
                                entities.addLocation(new SentenceLocationOccurrence(queuedEntityText, storySentencesId));
                                break;
                            case "ORGANIZATION":
                                entities.addOrganization(new OrganizationOccurrence(queuedEntityText, 0));
                                break;
                            }
                        }
                        queuedEntityText = tokenText;
                    }
                    lastEntityType = entityType;
                }
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        return entities;
    }

    public static ExtractedEntities entitiesFromLocationJsonString(String locationsJsonString) {
        Gson gson = new Gson();
        JsonObject content = gson.fromJson(locationsJsonString, JsonObject.class);

        ExtractedEntities entities = new ExtractedEntities();
        JsonArray mentions = content.getAsJsonArray("entitymentions");
        for(JsonElement e : mentions) {
            JsonObject mention = e.getAsJsonObject();
            String mentionType = mention.get("ner").getAsString();
            if (mentionType.toLowerCase().equals("location")) {
                String mentionText = mention.get("text").getAsString();
                int characterOffset = mention.get("characterOffsetBegin").getAsInt();
                LocationOccurrence location = new LocationOccurrence(mentionText, characterOffset);
                entities.addLocation(location);
            }
        }
        return entities;

    }
}
