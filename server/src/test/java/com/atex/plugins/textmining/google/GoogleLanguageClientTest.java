package com.atex.plugins.textmining.google;

import com.atex.plugins.textmining.TextMiningClient;
import com.atex.plugins.textmining.TextMiningConfig;
import com.atex.plugins.textmining.TextMiningConfigBean;
import com.atex.plugins.textmining.service.TextMiningService;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.metadata.Annotation;
import com.polopoly.metadata.Hit;
import com.polopoly.testnext.base.ImportTestContent;
import com.polopoly.textmining.Document;
import com.polopoly.textmining.TextMiningUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(MockitoJUnitRunner.class)
@ImportTestContent(files={"TextMiningServiceTest.content"}, dir="/com/atex/plugins/textmining/calais/", once=true)


public class GoogleLanguageClientTest {
    private static final String TEXTMINING_ANNOTATION = "/textmining/resources/metadata/annotation";
    private static final String TEXTMINING_ADDENTITY = "/textmining/resources/metadata/annotation";
    @Inject
    private PolicyCMServer policyCMServer;

    Injector injector;

    TextMiningService service;

    @Before
    public void setUp () throws CMException {
    }

    @Test
    public void annotate() throws IOException {

        Map<String, String> entityMap = buildEntityMap();
        Map<String, String> topicMap  = buildTopicMap();

        TextMiningConfig config = new TextMiningConfigBean();
        config.setEntityMap(entityMap);
        config.setTopicMap(topicMap);
        config.setApiKey(buildJson());
        config.setDimensionName("IPTC");
        TextMiningClient cl = new GoogleMiningClient(config);

        String text = "Coronavirus: Swansea City's Steve Cooper hopes season can finish\n" +
                "Swansea City boss Steve Cooper hopes football's authorities will find a way to complete the season for the integrity of the game.\n" + "\n" +
                "All elite football in Britain has been suspended until at least 30 April because of the coronavirus pandemic.\n" + "\n" +
                "As clubs wait for news on when they will return to action, Cooper says Swansea are ready to adapt.\n" + "\n" +
                "All games in England's Premier League, EFL, Women's Super League and Women's Championship, and all fixtures in Scotland, Wales and Northern Ireland, are currently postponed.\n" + "\n" +
                "The Football Association has agreed that the current season can be extended indefinitely beyond the original end date of 1 June.\n";

        Document doc = new Document(text, TextMiningUtil.POLOPOLY_PLAN);

        Annotation analyze = cl.analyzeText(doc, policyCMServer);

        assertNotNull(analyze);

        assertNotNull(analyze.getTaxonomyId());
        assertNotNull(analyze.getTaxonomyName());

        assertNotNull(analyze.getHits());

        assertTrue(analyze.getHits().size() > 0);

        assertTrue(analyze.getTaxonomyId().equals("googleNL"));
        assertTrue(analyze.getTaxonomyName().equals("Google Natural Language"));

        for (Hit h : analyze.getHits()) {
            assertNotNull(h.getDimension());
        }

    }

    private String buildJson(){
        String json = "{" + "\"type\": \"service_account\",\n" + "\"project_id\": \"naturallanguage-272210\",\n" +
                "\"private_key_id\": \"b14efd02caa6c14cb0a7a3ae54ee3be66b22528a\",\n" +
                "\"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCfv3rNHa06Utx8\\n/3Wu8/Se0A04PFTpqAU7Ry/a/XyxPl72QsZXCq1GoIvIZjjBEtKnpOnNuM25C2Af\\nkiltu0vc7ezK1lYXdlU722dR6lpJEllNh9DmtgkCKtnUIYgtOYwNM5KXF4VcXWYp\\niP/eTiBn/5CN7vAnNxcIqHZzRe3GFt/TN5ttj8AuC/yNKNMP8Q/E3cEG86CA+8wg\\nsbKjbQYkkLoIXOx9pHppG0Nv1m3yq5vf1Dvi9gnndxPMSfbdWBL+9vwxlu2qBrkR\\nMa7GIXHxb8vF8Sq25iqUj63WQMhye+dqdYgZFIa3b1TEcm7/8l93vZytJ409C0Rq\\nrL4vdKXbAgMBAAECggEAG8J99/viuOJiT1RjImphRWG5El8lkE7yzcjCj6cCDpjc\\njqWu6rw/vSiz8biVvzNiqOA3AtlM4o2QABx5l4h3F+a+gcb6Qex9xj+LtkfntY/7\\nNcZWrmjOqvJAVSUYkWdRJ5AhNqFPOuw+DxSwBfUEm53lBE0TrXTcy1Pe9Ywe1A+q\\nPOGP9lNm5YPALwczxPnwG7T5StDyb1i7UPc7xIjhw14NUSJImhSm+8tdWQIqKWro\\nRYGg+cSWW6nZlrwf4qRRd+2XZ8na+5ZHYZT+MqTxm2GgkwGFPjK4WjTrPGPZpi9D\\nCbEI+F8I3TIiJrY5JCVX8SjOKEZP9dsndtIjlX+iDQKBgQDN85VRqRaymmG65Mwe\\nU5KoXZtkoltTOlzuOI7o4n0Dp3UVsrCZPMtSRblKoc/GTJkDF6mp3mE6DVrzzy+U\\n4Ye76RVVz0hM75K4xhXh1/ZO5m3DAu47gYOXCUnHJENTyvIR8rUqvfRGihUTjjP9\\nMsXOmL9vFW6MM8a8tcF804mITwKBgQDGkYkpDVRrNtXiJm/oTzfcsc7xharI+F45\\nru/7EO+wNev10PHYzZI5M2nb1/vceNreGOa1Da8NmH1RU+lUzhk8iy+QtLVn+HpA\\nP65Sh7T1ySxoZRfEROKR0Jd0oX0Zy1aCfP+jjzWT6rPy2q3l1w3KWW3vhuAfhmHY\\nta5RP5zatQKBgDU5k0hkY4pK3aD4Skw4GvnKH0O1tdZfie7CzSQgjGBqYZb29C7W\\nCFG91TIVeWXtG8oxzlYpM4VWOEA0Nj7KvlJw3WOsyyme7JM2FieksXIYmRtB4aGd\\nSg25h8igdYntM/wxdmKSbtPgEaxZHtR7z6Uh0q95oXU4WE8ixdtlq/B/AoGAY3JD\\n1TF90+w0lfxWE2yJN5g9Yixlah0pY44/VegXgpKeyA+K79+Umd0r6RR0aOiGBLFH\\n6ekegJqcOHzj4LsPXIkAm2JFb/q7jy3VajPY5zeHbAklLACr0Ac9swWll6OEgfmE\\nNi/kz12+HaRzbyaTG4JKUqmj2WX93wCuerZ3IfkCgYASyFrYsarza2BXiSRZCWYa\\n4EGPokmgm+mD97pIWWK6JeBERM292HitGOiejkT0waUaXjc8eaQJKUMG23K852uN\\nI+h7hLkxuhFVuUB1JbKPS5swQJ8Hvl0KdbUG0X7U+F/pAdW54+1g89APBYtYl6WD\\nGVNNOAmaPrM1UBLJ1lgSUg==\\n-----END PRIVATE KEY-----\\n\",\n" +
                "\"client_email\": \"adamgiles-43@naturallanguage-272210.iam.gserviceaccount.com\",\n" +
                "\"client_id\": \"103905603931005970400\",\n" +
                "\"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                "\"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                "\"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                "\"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/adamgiles-43%40naturallanguage-272210.iam.gserviceaccount.com\"" + "}";

        return json;
    }

    private Map<String, String> buildTopicMap(){
        Map<String, String> topicMap = new HashMap<>();
        topicMap.put("Adult","iptc-01");
        topicMap.put("Arts & Entertainment","iptc-01");
        topicMap.put("Autos & Vehicles","iptc-13");
        topicMap.put("Beauty & Fitness","iptc-");
        topicMap.put("Books & Literature","iptc-01");
        topicMap.put("Business & Industrial","iptc-04");
        topicMap.put("Computers & Electronics", "iptc-13");
        topicMap.put("Finance", "iptc-04");
        topicMap.put("Food & Drink","iptc-10");
        topicMap.put("Games","iptc-13");
        topicMap.put("Health","iptc-07");
        topicMap.put("Hobbies & Leisure","iptc-10");
        topicMap.put("Home & Garden","iptc-10");
        topicMap.put("Internet & Telecom","iptc-13");
        topicMap.put("Jobs & education","iptc-05");
        topicMap.put("Law & Government","iptc-02");
        topicMap.put("News","iptc-08");
        topicMap.put("Online Communities","iptc-14");
        topicMap.put("People & Society","iptc-08");
        topicMap.put("Pets & Animals","iptc-10");
        topicMap.put("Real Estate","iptc-08");
        topicMap.put("Reference","iptc-08");
        topicMap.put("Science","iptc-13");
        topicMap.put("Shopping","iptc-10");
        topicMap.put("Sports","iptc-15");
        topicMap.put("Travel","iptc-");
        return topicMap;
    }
    private Map<String, String> buildEntityMap(){
        Map<String, String> entityMap = new HashMap<>();
        entityMap.put("*","dimension.Tag");
        entityMap.put("UNKNOWN","");
        entityMap.put("PERSON","dimension.Person");
        entityMap.put("LOCATION","dimension.Location");
        entityMap.put("ORGANIZATION","dimension.Organisation");
        entityMap.put("EVENT","");
        entityMap.put("WORK_OF_ART","");
        entityMap.put("CONSUMER_GOOD","");
        entityMap.put("OTHER","");
        entityMap.put("PHONE_NUMBER","");
        entityMap.put("ADDRESS","");
        entityMap.put("DATE","");
        entityMap.put("NUMBER","");
        entityMap.put("PRICE","");
        return entityMap;
    }
}