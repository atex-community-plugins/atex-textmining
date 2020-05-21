package com.atex.plugins.textmining.extraggo.parser;

import com.atex.plugins.textmining.extraggo.parser.common.Disambiguation;
import com.atex.plugins.textmining.extraggo.parser.common.Position;
import com.atex.plugins.textmining.extraggo.parser.entity.Entity;
import com.atex.plugins.textmining.extraggo.parser.keyword.Keyword;
import com.atex.plugins.textmining.extraggo.parser.keyword.TokenLevelDisambiguations;
import com.atex.plugins.textmining.extraggo.parser.keyword.TopDomains;
import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Parser {
    private Parser() {
    }

    public static Parser getInstance() {
        if (_this == null) {
            _this = new Parser();
        }

        return _this;
    }

    public Document parse(String json) {
        Document document = new Document();

        if (!Strings.isNullOrEmpty(json)) {
            JsonElement element = new JsonParser().parse(json);
            if (element != null) {
                JsonObject object = element.getAsJsonObject();

                JsonArray keywords = object.getAsJsonArray("keywords");
                document.setKeywords(parseKeywords(keywords));

                JsonArray namedEntities = object.getAsJsonArray("namedEntities");
                document.setEntities(parseEntities(namedEntities));
            }
        }

        return document;
    }

    private List<Keyword> parseKeywords(JsonArray contents) {
        List<Keyword> parsed = null;

        if (contents != null && contents.size() > 0) {
            parsed = new ArrayList<>();

            Iterator<JsonElement> keywords = contents.iterator();
            while (keywords.hasNext()) {
                JsonElement element = keywords.next();
                if (element != null) {
                    JsonObject obj = element.getAsJsonObject();
                    Keyword keyword = new Keyword();
                    keyword.setId(getIntValueOf(obj, "id"));
                    keyword.setName(getStringValueOf(obj, "name"));
                    keyword.setScore(getFloatValueOf(obj,"score"));
                    keyword.setPositions(getPositionsList(obj));
                    keyword.setDisambiguation(getDisambiguation(obj));
                    keyword.setTokenLevelDisambiguations(getTokenLevelDisambiguations(obj));
                    keyword.setTopDomains(getTopDomains(obj));

                    parsed.add(keyword);
                }
            }
        }

        return parsed;
    }

    private List<Entity> parseEntities(JsonArray contents) {
        List<Entity> parsed = null;

        if (contents != null && contents.size() > 0) {
            parsed = new ArrayList<>();

            Iterator<JsonElement> entities = contents.iterator();
            while (entities.hasNext()) {
                JsonElement element = entities.next();
                if (element != null) {
                    JsonObject obj = element.getAsJsonObject();
                    if (obj != null) {
                        Entity entity = new Entity();
                        entity.setId(getIntValueOf(obj, "id"));
                        entity.setName(getStringValueOf(obj, "name"));
                        entity.setLabel(getStringValueOf(obj, "label"));
                        entity.setPositions(getPositionsList(obj));
                        entity.setDisambiguation(getDisambiguation(obj));
                        parsed.add(entity);
                    }
                }
            }
        }

        return parsed;
    }

    private List<TopDomains> getTopDomains(JsonObject object) {
        List<TopDomains> domains = null;

        if (object != null) {
            JsonArray contents = object.getAsJsonArray("topDomains");
            if (contents != null && contents.size() > 0) {
                domains = new ArrayList<>();

                Iterator<JsonElement> elements = contents.iterator();
                while (elements.hasNext()) {
                    JsonElement element = elements.next();

                    if (element != null) {
                        JsonObject obj = element.getAsJsonObject();

                        float score = getFloatValueOf(obj, "score");
                        String name = getStringValueOf(obj, "name");

                        domains.add(new TopDomains(name, score));
                    }
                }
            }
        }

        return domains;
    }

    private List<TokenLevelDisambiguations> getTokenLevelDisambiguations(JsonObject object) {
        List<TokenLevelDisambiguations> disambiguation = null;

        if (object != null) {
            JsonArray contents = object.getAsJsonArray("tokenLevelDisambiguations");
            if (contents != null && contents.size() > 0) {
                disambiguation = new ArrayList<>();

                Iterator<JsonElement> elements = contents.iterator();
                while (elements.hasNext()) {
                    JsonElement element = elements.next();

                    if (element != null) {
                        JsonObject obj = element.getAsJsonObject();

                        float score = getFloatValueOf(obj, "score");
                        String senseId = getStringValueOf(obj, "senseID");
                        Position position = getPosition(obj);

                        disambiguation.add(new TokenLevelDisambiguations(senseId, score, position));
                    }
                }
            }
        }

        return disambiguation;
    }

    private Disambiguation getDisambiguation(JsonObject object) {
        Disambiguation disambiguation = null;

        if (object != null) {
            JsonObject content = object.getAsJsonObject("disambiguation");
            if (content != null) {
                float score = getFloatValueOf(content, "score");
                String senseId = getStringValueOf(content, "senseID");

                disambiguation = new Disambiguation(score, senseId);
            }
        }

        return disambiguation;
    }

    private Position getPosition(JsonObject object) {
        Position parsed = null;

        if (object != null) {
            JsonObject position = object.getAsJsonObject("position");

            if (position != null) {
                int start = getIntValueOf(position, "charOffsetBegin");
                int end = getIntValueOf(position, "charOffsetEnd");
                parsed = new Position(start, end);
            }
        }

        return parsed;
    }

    private List<Position> getPositionsList(JsonObject object) {
        List<Position> parsed = null;

        if (object != null) {
            JsonArray array = object.getAsJsonArray("positions");
            if (array != null && array.size() > 0) {
                parsed = new ArrayList<>();

                Iterator<JsonElement> positions = array.iterator();
                while (positions.hasNext()) {
                    JsonElement position = positions.next();
                    if (position != null) {
                        JsonObject obj = position.getAsJsonObject();
                        int start = getIntValueOf(obj, "charOffsetBegin");
                        int end = getIntValueOf(obj, "charOffsetEnd");
                        parsed.add(new Position(start, end));
                    }
                }
            }
        }

        return parsed;
    }

    private float getFloatValueOf(JsonObject object, String property) {
        float value = -1;

        if (object != null) {
            JsonElement element = object.get(property);

            if (element != null) {
                value = element.getAsFloat();
            }
        }

        return value;
    }

    private int getIntValueOf(JsonObject object, String property) {
        int value = -1;

        if (object != null) {
            JsonElement element = object.get(property);

            if (element != null) {
                value = element.getAsInt();
            }
        }

        return value;
    }

    private String getStringValueOf(JsonObject object, String property) {
        String value = null;

        if (object != null) {
            JsonElement element = object.get(property);

            if (element != null) {
                value = element.getAsString();
            }
        }

        return value;
    }

    private static Parser _this = null;
}