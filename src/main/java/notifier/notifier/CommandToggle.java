package notifier.notifier;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandToggle implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.isOp() && sender instanceof Player){
            Notifier.getInstance().toggleStatus();
            sender.sendMessage("The server " + (Notifier.getInstance().disabled? "will not shutdown":"will shutdown"));
        }
        return true;
    }
}
