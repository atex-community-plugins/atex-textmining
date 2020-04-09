package com.atex.plugins.textmining.calais;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.atex.plugins.textmining.TextMiningConfigPolicy;
import com.atex.plugins.textmining.TextMiningService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.metadata.Annotation;
import com.polopoly.metadata.Hit;
import com.polopoly.testnext.base.ImportTestContent;
import com.polopoly.testnj.TestNJRunner;
import com.polopoly.textmining.Document;
import com.polopoly.textmining.TextMiningUtil;


@RunWith(TestNJRunner.class)
@ImportTestContent(files={"TextMiningServiceTest.content"}, dir="/com/atex/plugins/textmining/calais/", once=true)
public class TextMiningServiceTestIT {

    private static final String TEXTMINING_ANNOTATION = "/textmining/resources/metadata/annotation";
    private static final String TEXTMINING_ADDENTITY = "/textmining/resources/metadata/annotation";

    @Inject
    private PolicyCMServer policyCMServer;

    Injector injector;

    TextMiningService service;
    
    static String TEXT = "<p>The latest FIFA global rankings were published on Thursday. While there were changes for eight of the top ten ranked teams, the Spanish team's top ranking remained unchanged. Germany's rank of number two also remained unchanged. All the teams are all-male, with women ranked separately.</p> <p>Portugal, Colombia and Uruguay each moved up one to be ranked third, fourth and fifth respectively. Argentina dropped three places to be ranked sixth, tied in the rank with Brazil who moved up three. Switzerland and Italy each moved down one, ranked eighth and ninth respectively. Greece moved up three to be ranked tenth.</p> <p>Azerbaijan is ranked the highest in the team's history at 85th, up six from the previous rankings published in March. Afghanistan is the second team in this ranking to have a team best ranking with a 122nd ranking, up 5 from the previous ranking period. Scotland was the biggest mover in this ranking period, climbing 15 spots for a ranking of 22. Liberia had the biggest drop, moving down 22 spots to 119. This was down only one more team than New Zealand who dropped 21 spots to be ranked 111.</p> <p>Later this year, Spain is scheduled to play the Netherlands, Chile and Australia at the 2014 FIFA World Cup during pool play. The Netherlands dropped four places to be ranked fifteenth. Australia rose four places to be ranked 59th. Chile rose one place to be ranked fourteenth.</p> <p>Several members of the Spanish national team are currently scheduled to play in the 2013–14 UEFA Champions League semi-finals, including Diego Costa, Koke and David Villa who play for Atletico Madrid and Sergio Ramos, Iker Casillas, Álvaro Arbeloa, Nacho, Xabi Alonso, Isco.</p> <p><i><a href=\"http://en.wikinews.org/wiki/Spain%27s_men_remain_on_top_of_FIFA_global_rankings_for_April_2014\">Wikinews</a>&nbsp;<a href=\"http://creativecommons.org/licenses/by/2.5/legalcode\">CC BY 2.5</a></i></p>"; 

    @Before
    public void setUp () throws CMException {
        TextMiningConfigPolicy config = (TextMiningConfigPolicy) policyCMServer.getPolicy(new ExternalContentId("com.atex.plugins.textmining.calais.TextMiningServiceTest.config"));
        injector = Guice.createInjector(new TextMiningTestModule(config.getConfig()));
        service = injector.getInstance(TextMiningService.class);
    }

    @After
    public void tearDown() throws Exception {
        injector = null;
    }

    @Test
    public void testDescription() throws Exception {
        assertNotNull ( service.description());
    }

    @Test
    public void testSuggestCategorization() throws Exception {

        Document doc = new Document(TEXT, TextMiningUtil.POLOPOLY_PLAN);

        Annotation a = service.suggestCategorization(doc);

        assertNotNull(a);

        assertNotNull(a.getTaxonomyId());
        assertNotNull(a.getTaxonomyName());

        assertNotNull(a.getHits());

        assertTrue(a.getHits().size() > 0);

        assertTrue(a.getTaxonomyId().equals("opencalais"));
        assertTrue(a.getTaxonomyName().equals("Open Calais"));

        for (Hit h : a.getHits()) {
            assertNotNull(h.getDimension());
        }

    }

    @Test
    public void testSuggestCategorizationText() throws Exception {

        Document doc = new Document("this is Text", TextMiningUtil.POLOPOLY_PLAN);

        Annotation a = service.suggestCategorization(doc);

        assertNotNull(a);

        assertNotNull(a.getTaxonomyId());
        assertNotNull(a.getTaxonomyName());

        assertNotNull(a.getHits());

        assertTrue(a.getHits().size() > 0);

        assertTrue(a.getTaxonomyId().equals("opencalais"));
        assertTrue(a.getTaxonomyName().equals("Open Calais"));

        for (Hit h : a.getHits()) {
            assertNotNull(h.getDimension());
        }

    }

    @Test
    public void testLargeBody() throws Exception {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 100000 ; i++) {
            sb.append("Hourly rate\n");
            sb.append("While the new data shows fees for creches, Montessori and other forms of early childhood care increasing across the State, wages for workers remain among the lowest earned in any sector.\n" +
                    "\n" );
            sb.append("The average hourly rate for staff in the sector is just over €12.55, and the average hourly rate for an early years assistant is €11.44.\n" +
                    "\n");
            sb.append("With wages making up some 70 per cent of a childcare settings running costs, better paid staff in urban areas – where the cost of living is generally higher – may go some way to explaining the disparity between fees charged to parents in different parts of the country.\n" +
                    "\n");
            sb.append("However, according to research conducted by Pobal and published last year, statistical analysis shows that when factors such as location are accounted for there is no correlation between fees and the wages paid to staff.\n" +
                    "\n");
            sb.append("Ms Zappone is expected to target a five-hour increase in the number of subsidised hours available to parents in the NCS in the budget. Currently parents can claim for up to 40 hours per week.");
        }

        Document doc = new Document(sb.toString(), TextMiningUtil.POLOPOLY_PLAN);

        Annotation a = service.suggestCategorization(doc);

        assertNotNull(a);

        assertNotNull(a.getTaxonomyId());
        assertNotNull(a.getTaxonomyName());

        assertNotNull(a.getHits());

        assertTrue(a.getHits().size() > 0);

        assertTrue(a.getTaxonomyId().equals("opencalais"));
        assertTrue(a.getTaxonomyName().equals("Open Calais"));

        for (Hit h : a.getHits()) {
            assertNotNull(h.getDimension());
        }

    }


    @Test(expected=javax.ws.rs.WebApplicationException.class)
    public void testSuggestCategorizationNullFail() throws Exception {

        Document doc = new Document("", "");

        Annotation a = service.suggestCategorization(doc);


    }
}