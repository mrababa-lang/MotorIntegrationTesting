package com.insurance.automation.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Static registry of all Shory insurance company profiles.
 * Maps insuranceCompanyProfileId → display name and CDN logo URL.
 */
public final class InsuranceCompanyRegistry {

    /** Immutable profile record. */
    public static class CompanyProfile {
        private final int id;
        private final String name;
        private final String logoUrl;

        public CompanyProfile(final int id, final String name, final String logoUrl) {
            this.id = id;
            this.name = name;
            this.logoUrl = logoUrl;
        }

        public int getId()       { return id; }
        public String getName()  { return name; }
        public String getLogoUrl() { return logoUrl; }
    }

    private static final Map<Integer, CompanyProfile> REGISTRY;

    static {
        final Map<Integer, CompanyProfile> map = new HashMap<>();
        map.put( 1, new CompanyProfile( 1, "Islamic Arab Insurance Company SALAMA",          "https://cdn.shory.com/static/insurance/logo/Salama.png"));
        map.put( 2, new CompanyProfile( 2, "Dubai Insurance",                                "https://cdn.shory.com/static/insurance/logo/DubaiInsurance.png"));
        map.put( 3, new CompanyProfile( 3, "Sukoon Insurance Company",                       "https://cdn.shory.com/static/insurance/logo/SukoonInsurance.png"));
        map.put( 4, new CompanyProfile( 4, "Al Fujairah National Insurance Co P.J.S.C.",     "https://cdn.shory.com/static/insurance/logo/AFNIC.png"));
        map.put( 5, new CompanyProfile( 5, "Watania Takaful General P.J.S.C",                "https://cdn.shory.com/static/insurance/logo/Watania.png"));
        map.put( 6, new CompanyProfile( 6, "Abu Dhabi National Insurance Company PJSC (ADNIC)", "https://cdn.shory.com/static/insurance/logo/ADNIC.png"));
        map.put( 7, new CompanyProfile( 7, "Abu Dhabi National Takaful Co.",                 "https://cdn.shory.com/static/insurance/logo/ADNTC.png"));
        map.put( 8, new CompanyProfile( 8, "Qatar Insurance Company",                        "https://cdn.shory.com/static/insurance/logo/QIC.png"));
        map.put( 9, new CompanyProfile( 9, "Insurance House",                                "https://cdn.shory.com/static/insurance/logo/InsuranceHouse.png"));
        map.put(10, new CompanyProfile(10, "Yas Takaful P.J.S.C.",                           "https://cdn.shory.com/static/insurance/logo/YASTakaful.png"));
        map.put(11, new CompanyProfile(11, "Al Ain Ahlia Insurance Company",                 "https://cdn.shory.com/static/insurance/logo/AlAinAhlia.png"));
        map.put(12, new CompanyProfile(12, "Al Dhafra",                                      "https://cdn.shory.com/static/insurance/logo/AlDhafra.png"));
        map.put(13, new CompanyProfile(13, "National General Insurance Company",              "https://cdn.shory.com/static/insurance/logo/NGI.png"));
        map.put(14, new CompanyProfile(14, "Al Wathba National Insurance",                   "https://cdn.shory.com/static/insurance/logo/AWNIC.png"));
        map.put(15, new CompanyProfile(15, "Orient",                                         "https://cdn.shory.com/static/insurance/logo/ORIENT.png"));
        map.put(16, new CompanyProfile(16, "Ras Al Khaimah National Insurance Company",      "https://cdn.shory.com/static/insurance/logo/RAK.png"));
        map.put(18, new CompanyProfile(18, "Adamjee Insurance Company LTD",                  "https://cdn.shory.com/static/insurance/logo/Adamjee.png"));
        map.put(19, new CompanyProfile(19, "United Fidelity Insurance Company- PSC",         "https://cdn.shory.com/static/insurance/logo/Fidelity.png"));
        map.put(20, new CompanyProfile(20, "Methaq Takaful Insurance Company",               "https://cdn.shory.com/static/insurance/logo/MethaqTakaful.png"));
        map.put(21, new CompanyProfile(21, "Union Insurance Company P.J.S.C",               "https://cdn.shory.com/static/insurance/logo/union.jpg"));
        map.put(22, new CompanyProfile(22, "Orient Takaful PJSC",                            "https://cdn.shory.com/static/insurance/logo/OrientTakaful.png"));
        REGISTRY = Collections.unmodifiableMap(map);
    }

    private InsuranceCompanyRegistry() {}

    /**
     * Returns the profile for the given ID, or a fallback "Unknown" profile if not found.
     */
    public static CompanyProfile get(final int id) {
        return REGISTRY.getOrDefault(id,
            new CompanyProfile(id, "Insurance Company #" + id, ""));
    }
}
