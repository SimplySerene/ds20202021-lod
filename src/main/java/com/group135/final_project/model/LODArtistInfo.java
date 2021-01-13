package com.group135.final_project.model;

import java.util.Set;

public class LODArtistInfo {
    private Set<String> countryCodes;

    public LODArtistInfo(Set<String> countryCodes) {
        this.countryCodes = countryCodes;
    }

    public Set<String> getCountryCodes() {
        return countryCodes;
    }

}
