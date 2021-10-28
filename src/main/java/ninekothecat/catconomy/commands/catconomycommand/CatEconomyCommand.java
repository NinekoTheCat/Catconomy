package ninekothecat.catconomy.commands.catconomycommand;

import ninekothecat.catconomy.interfaces.ICatEconomyCommandAutoComplete;
import ninekothecat.catconomy.interfaces.ICatEconomyCommandExecutor;

public class CatEconomyCommand {
    private ICatEconomyCommandAutoComplete autoComplete = null;
    private ICatEconomyCommandExecutor executor = null;
    private String name;

    public CatEconomyCommand(String name) {
        this.name = name;
    }

    public boolean hasAutoComplete() {
        return autoComplete != null;
    }

    public boolean hasExecutor() {
        return executor != null;
    }

    public ICatEconomyCommandAutoComplete getAutoComplete() {
        return autoComplete;
    }

    public void setAutoComplete(ICatEconomyCommandAutoComplete autoComplete) {
        this.autoComplete = autoComplete;
    }

    public ICatEconomyCommandExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(ICatEconomyCommandExecutor executor) {
        this.executor = executor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
