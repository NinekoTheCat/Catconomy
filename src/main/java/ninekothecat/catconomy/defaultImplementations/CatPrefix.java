package ninekothecat.catconomy.defaultImplementations;

import ninekothecat.catconomy.interfaces.ICurrencyPrefix;

@SuppressWarnings({"unused"})
public class CatPrefix implements ICurrencyPrefix {
    private final String shortPrefix;
    private final String longPrefix;

    public CatPrefix(String shortPrefix) {
        this.shortPrefix = shortPrefix;
        longPrefix = shortPrefix;
    }

    public CatPrefix(String shortPrefix, String longPrefix) {
        this.shortPrefix = shortPrefix;
        this.longPrefix = longPrefix;
    }

    @Override
    public String getShortPrefix() {
        return shortPrefix;
    }

    @Override
    public String getLongPrefix() {
        return longPrefix;
    }
}
