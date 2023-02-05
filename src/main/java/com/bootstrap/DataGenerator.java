package com.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class DataGenerator implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

        String[] countries = Locale.getISOCountries();

        for (String country : countries) {

            Locale locale = new Locale("en", country);

            String countryName = locale.getDisplayCountry();
            StaticConstants.COUNTRY_LIST.add(countryName);
        }
    }
}
