package ninekothecat.catconomy.defaultImplementations

import ninekothecat.catplugincore.money.interfaces.ICurrencyPrefix

class CatPrefix : ICurrencyPrefix {
    override val shortPrefix: String
    override val longPrefix: String

    constructor(shortPrefix: String) {
        this.shortPrefix = shortPrefix
        longPrefix = shortPrefix
    }

    constructor(shortPrefix: String, longPrefix: String) {
        this.shortPrefix = shortPrefix
        this.longPrefix = longPrefix
    }
}