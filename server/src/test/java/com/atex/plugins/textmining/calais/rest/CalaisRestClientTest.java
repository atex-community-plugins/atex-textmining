package com.atex.plugins.textmining.calais.rest;

import java.io.IOException;
import java.util.function.Consumer;

import org.junit.Test;

public class CalaisRestClientTest {

    @Test
    public void testAnalyze () throws IOException {
        CalaisRestClient client = new CalaisRestClient("");

        CalaisResponse analyze = client.analyze("Coronavirus: Swansea City's Steve Cooper hopes season can finish\n" +
                "Swansea City boss Steve Cooper hopes football's authorities will find a way to complete the season for the integrity of the game.\n" + "\n" +
                "All elite football in Britain has been suspended until at least 30 April because of the coronavirus pandemic.\n" + "\n" +
                "As clubs wait for news on when they will return to action, Cooper says Swansea are ready to adapt.\n" + "\n" +
                "All games in England's Premier League, EFL, Women's Super League and Women's Championship, and all fixtures in Scotland, Wales and Northern Ireland, are currently postponed.\n" + "\n" +
                "The Football Association has agreed that the current season can be extended indefinitely beyond the original end date of 1 June.\n");

        analyze.getEntities().forEach(new Consumer<CalaisObject>() {
            @Override
            public void accept(CalaisObject calaisObject) {
                if (calaisObject.getField("name") == null) {
                    System.out.println(calaisObject.toString());
                }
            }
        });


    }

}