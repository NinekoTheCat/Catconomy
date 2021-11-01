# Catconomy
[![](https://tokei.rs/b1/github/NinekoTheCat/Catconomy?category=files)](https://github.com/NinekoTheCat/Catconomy) [![](https://tokei.rs/b1/github/NinekoTheCat/Catconomy?category=code)](https://github.com/NinekoTheCat/Catconomy)

a simple and modular economy plugin with database addon support


## Modularity

This is purely an Economy plugin, It manages and secures player transactions and that's it. If you want it to do more than that
 I suggest you download a plugin that either supports CC or Vault API.

## FAQ:
### Q: How do I make an addon for an unsupported database?
A: implement the `IDatabase` interface in a class and then register it as a service in your `onLoad()` function.
with a higher priority than LOW.

spigot page: https://www.spigotmc.org/resources/catconomy.97210/ .

curseforge/bukkit page: https://dev.bukkit.org/projects/catconomy .
