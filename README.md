# Catconomy
a simple and modular economy plugin with database addon support


## Modularity

This is purely an Economy plugin, It manages and secures player transactions and thats it. If you want it to do more than that
 I suggest you download a plugin that either supports CC or Vault API.

## FAQ:
### Q: How do I make an addon for an unsupported database?
A: implement the `IDatabase` interface in a class and then set Catconomy.database to an instance of the class.
please note that it already needs to be set up.
