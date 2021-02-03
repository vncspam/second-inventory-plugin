import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	private FileConfiguration config=this.getConfig();
	@Override
	public void onEnable() {
		if (!(this.getDataFolder().exists())) {
			this.getDataFolder().mkdir();
		}
		try {
			this.config.load(this.getDataFolder().getAbsolutePath()+"/inventory.yml");
		} catch (IOException | InvalidConfigurationException e1) {
			// TODO Auto-generated catch block
		}

		try {
			this.config.save(this.getDataFolder().getAbsolutePath()+"/inventory.yml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		this.getCommand("inventory").setExecutor(new inventory(this.config));
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getLogger().info("enabled");
	}
	@EventHandler
	public void playerCloseEvent(InventoryCloseEvent e) throws IOException {
		if (e.getInventory().getName().equals("Дополнительный инвентарь")) {
			String items = "";
			for (int i = 0; i < e.getInventory().getSize(); i++) {
				ItemStack item =e.getInventory().getItem(i);
				if (item==null) {
					items+="0-1;";
				}else {
					int type =item.getTypeId();
					items+=((Integer) type).toString()+"-"+item.getAmount()+";";
				}
			}
			
			this.config.set(e.getPlayer().getName(), items);
			this.config.save(this.getDataFolder().getAbsolutePath()+"/inventory.yml");
		}
	}
}
class inventory implements CommandExecutor {
	private FileConfiguration config;
	public inventory(FileConfiguration config) {
		this.config=config;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof ConsoleCommandSender)) {
			Player player = (Player) sender;
			Inventory inv=Bukkit.createInventory(player, 27, "Дополнительный инвентарь");
			if (this.config.get(player.getPlayer().getName()) != null) {
				String[] tmp=this.config.getString(player.getPlayer().getName()).split(";");
				for (int i=0; i<tmp.length;i++) {
					Integer type=0;
					Integer amount=1;
					type = Integer.parseInt(tmp[i].split("-")[0]);
					amount=Integer.parseInt(tmp[i].split("-")[1]);
					if (type!=0) {
						inv.setItem(i, new ItemStack(type, amount));
					}
				}
			}
			player.openInventory(inv);
			return true;
		}
		
		return false;
	}
	
}