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
@ImportTestContent(files={"TextMiningServiceTest.content"}, dir="/com/atex/plugins/textmining/", once=true)


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
        config.setProviderName("google");
        config.setApiKey(buildJson());
        config.setDimensionName("IPTC");
        TextMiningClient cl = new GoogleMiningClient(config);

        String text = "Media Drum World\n" +
                "\n" +
                "Rebecca Drew MEDIA DRUM WORLD\n" +
                "\n" +
                "+44 (0) 333 321 1546\n" +
                "\n" +
                "www.mediadrumworld.com\n" +
                "\n" +
                "picturedesk@mediadrumworld.com\n" +
                "\n" +
                "Epic Road Trip Family\n" +
                "\n" +
                "By Rebecca Drew\n" +
                "\n" +
                "**EXCLUSIVE**\n" +
                "\n" +
                "BRITISH COUPLE were about to BUY A HOUSE when the seller pulled out, so they decided to travel EIGHT THOUSAND MILES across Europe in a van with a TODDLER IN TOW.\n" +
                "\n" +
                "Mum and businesswoman, Rachel Dix (30) and her carpenter husband, Sam (30) from Bristol, UK, first met when they were both working in a surf shop when they were just 18.\n" +
                "\n" +
                "Their friendship blossomed into romance and in April 2016 they were married and in January 2017 they welcomed their son, Ellis (3) into the world.\n" +
                "\n" +
                "Rachel and Sam have always had a love for the outdoors and nature and didn’t want their passion for adventure to end after they became parents.\n" +
                "\n" +
                "Despite this, the couple, who were living with Rachel’s mum at the time, were in the process of buying their own house, having paid the deposit and getting ready to commit to a hefty mortgage – when the seller suddenly pulled out.\n" +
                "\n" +
                "This shock move made the couple think about what they really wanted out of life and decided that they longed for freedom and to be able to travel and explore the world with Ellis whilst he was young enough to so he could develop a lifelong interest in the outdoors.\n" +
                "\n" +
                "So, in August 2018 Rachel and Sam bought a 2005 LDV Convoy for £3,500 with the aim to whet their appetite for travel by spending five months on the road exploring Europe with Ellis.\n" +
                "\n" +
                "Nine months and another £5,000 later, the family of three set off on their epic road trip across 18 countries including Belgium, Netherlands, Germany, Denmark, Sweden, Latvia, Poland, Slovenia and Italy.\n" +
                "\n" +
                "“Travel and adventure have always been a huge part of our lives – we are both pretty intrepid and have always enjoyed being outdoors and exploring new places. We didn't want that to change once we became parents,” said Rachel.\n" +
                "\n" +
                "“Parenthood also really made us question what was important to us, and we became really critical of how our actions tallied to our intentions. We wanted to spend more quality time together. We wanted to live more meaningfully and sustainably. We wanted to be free – outside of modern living.\n" +
                "\n" +
                "“More importantly, we wanted Ellis to experience the beauty of the world at a young age so that he could develop his own love and appreciation for nature. Van life just really offered us a means to fulfil our dream.\n" +
                "\n" +
                "“Sam was working full time [during the van renovation] and I was looking after baby Ellis, so we could only dedicate evenings and weekends to the refurbishment. It was really hard at times, especially during the winter months. It took a lot longer than expected, but we persevered, and it paid off.\n" +
                "\n" +
                "“We wanted to be really self-sufficient and able to go off-grid as much as possible. So, our whole solar-power and battery system was mega important. Having a large water supply to last as long as possible too. And then just designing a multi-purpose space that was adaptable to our needs and a growing toddler.\n" +
                "\n" +
                "“Norway is definitely top of our list. Norway is a country known for its dramatic and unparalleled beauty and for having some of the least restrictive camping laws. ‘Allenmannsrett’ or the ‘right to roam’ allows residents and visitors to explore the land and camp liberally – with a few exceptions.\n" +
                "\n" +
                "“Norwegian culture is deep rooted in its love for exploration and appreciation for nature and this shows in the way the land is respected and looked after. We highly recommend visiting Norway.”\n" +
                "\n" +
                "The family fully explored 15 countries on their road trip and visited; Belgium, Netherlands, Germany, Denmark, Norway, Sweden, Finland, Estonia, Latvia, Lithuania, Poland, Slovakia, Hungary, Slovenia and Italy – driving through Switzerland and France on their way back home to the UK.\n" +
                "\n" +
                "Ellis loved the adventure and Rachel and Sam hope that the incredible scenery and places he visited will stay with him for life.\n" +
                "\n" +
                "Now back in the UK, Rachel, Sam and Ellis are currently living in a rented house in Bristol whilst in self-isolation amidst the coronavirus outbreak and are getting ready to covert an American school bus to live on fulltime to accommodate their growing family before hopefully becoming largely self-sufficient by growing their own food.\n" +
                "\n" +
                "“Ellis adapted incredibly well, his ‘back garden’ became a vast glistening lake, or dramatic mountains or dense woodland with deer outside the window. Every day he woke up, he was immersed in nature and learning a wealth of knowledge just by experiencing it himself,” said Rachel.\n" +
                "\n" +
                "“At only two years old, he has visited eighteen countries; has seen the Aurora Borealis in Finland; witnessed centuries-old traditional sheepherding in the Polish Tatra Mountains; explored abandoned German war bunkers; hiked mountains with breath-taking views; clambered up waterfalls; been exposed to so many different cultures and languages and just about been on every train-related vehicle we could find in Europe. He loves trains.\n" +
                "\n" +
                "“He often recalls his own memories from his travels, even at such a young age, so I'm confident that these last few months have had an excellent impact on his life.\n" +
                "\n" +
                "“The freedom to go anywhere and park up is incredible. For Sam and I, it was a dream to have true quality time with our son that was free of distractions and work.\n" +
                "\n" +
                "“We felt like it brought us all closer together, focussed purely on the incredible moments that we were sharing as a family and creating lifelong memories to cherish forever. We witnessed first-hand the incredible deep learning that children benefit from by just experiencing the world around them.\n" +
                "\n" +
                "“For those dreaming of a life on the road – go for it. Do whatever it takes to achieve the life you want. Van life is all about living freely and minimally. You don't need a lot to accomplish this way of living.\n" +
                "\n" +
                "“We'd like to think that we could be an inspiration to other parents who would love to travel with their children.\n" +
                "\n" +
                "“It's scary committing to an alternative way of living, especially when you have kids, but the wealth of life skills they will develop exploring different landscapes and immersing in varying cultures will far outweigh any negatives.\n" +
                "\n" +
                "“Imagine having beaches and mountains as your back garden growing up.\n" +
                "\n" +
                "“For us, we would have driven back to the UK as soon as we had any inclination that borders were closing [if coronavirus was prevalent then] so that we could be close to family and healthcare if needed. Most of our fellow van lifers have done this.\n" +
                "\n" +
                "“It's difficult to give tips to other van lifers right now; it’s an unprecedented time and things are continually changing. But, we would say to follow the guidelines and restrictions based on where they are in the world.\n" +
                "\n" +
                "“To make sure they have appropriate cover for healthcare if needed and to know where to go in an emergency. To keep safe, and to protect their mental health as much as possible, especially if they are away from family and friends. Sending love to everyone.”\n" +
                "\n" +
                "For more information see www.instagram.com/thedixietribe";
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
        String json = "";

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