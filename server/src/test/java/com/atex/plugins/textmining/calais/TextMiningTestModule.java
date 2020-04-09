package com.atex.plugins.textmining.calais;

import com.atex.plugins.textmining.TextMiningConfig;
import com.atex.plugins.textmining.TextMiningConfigBean;
import org.junit.Ignore;

import com.google.inject.AbstractModule;

@Ignore
public class TextMiningTestModule extends AbstractModule {

    private TextMiningConfigBean config;

    public TextMiningTestModule(TextMiningConfigBean config)
    {
        this.config = config;
    }

    @Override
    protected void configure()
    {
       bind(TextMiningConfigBean.class).toInstance(config);
    }

}