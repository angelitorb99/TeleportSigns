package de.zh32.teleportsigns;

import de.zh32.teleportsigns.ping.ServerInfo;
import lombok.Data;
import org.bukkit.ChatColor;

/**
 *
 * @author zh32
 */
@Data
class TeleportSignLayout implements SignLayout {
    private final String name;    
    private final String online;
    private final String offline;
    private final String[] layout;
    private final boolean teleport;
    private final String offlineInteger;

    @Override
    public String[] parseLayout(ServerInfo sinfo) {
        String[] laa = new String[layout.length];
        String getMotd = sinfo.getMotd() == null ? "" : sinfo.getMotd();
        String motd[] = getMotd.split("\n",1);
        String finalMotd;
        int motdType = 0;
        if (motd[0].length() > 7 && motd[0].charAt(0) == '\u00A7' && motd[0].charAt(2) == '\u00BB' && motd[0].charAt(motd[0].length() - 4) == '\u00A7' && motd[0].charAt(motd[0].length() - 1)== '\u00AB'){
            motdType = 1;
            if (motd[0].charAt(1) == 'a')motdType = 2;
            if (motd[0].charAt(1) == '6')motdType = 3;
            if (motd[0].charAt(1) == 'b')motdType = 4;
            finalMotd = motd[0].substring(6,motd[0].length() - 4);
        } else {
            finalMotd = getMotd;
        }
        int nextMotd = 0;
        if (motdType != 0 && finalMotd.length() <= 15){
             nextMotd = 1;
        }
        int motdCount = 0;
        String tempMotd = finalMotd;
        String[] splitMotd = tempMotd.split("(?<=\\G.{15})");
        for (int i = 0; i < layout.length; i++) {
            String line = layout[i];
            line = line.replace("%displayname%", sinfo.getDisplayname());
            if (sinfo.isOnline()) {
                line = line.replace("%isonline%", online);
                line = line.replace("%numpl%", String.valueOf(sinfo.getPlayersOnline()));
                line = line.replace("%maxpl%", String.valueOf(sinfo.getMaxPlayers()));
                if (motdType == 0){
                    if (line.contains("%motd%")) {
                        if (motdCount < splitMotd.length) {
                            String splittedMotd = splitMotd[motdCount];
                            if (splittedMotd != null) {
                                line = line.replace("%motd%", splittedMotd);
                            }
                            motdCount++;
                        } else {
                            line = line.replace("%motd%", "");
                        }
                    }
                } else {
                    if (line.contains("%motd%")) {
                        if (nextMotd == 0){
                            String motdWords[] = tempMotd.split(" ");
                            int actWord = 0;
                            int totWords = motdWords.length - 1;
                            int linelen;
                            String getline = "";
                            while (actWord <= totWords) {
                                if (motdWords[actWord].length() > 15){
                                    motdWords[actWord] = motdWords[actWord].substring(0,14);
                                }
                                linelen = getline.length() + motdWords[actWord].length();
                                if (linelen > 15) {
                                    if (motdType == 1) line = line.replace("%motd%", ChatColor.BLACK + getline.trim());
                                    if (motdType == 2) line = line.replace("%motd%", ChatColor.DARK_GREEN + getline.trim());
                                    if (motdType == 3) line = line.replace("%motd%", ChatColor.DARK_BLUE + getline.trim());
                                    if (motdType == 4) line = line.replace("%motd%", ChatColor.DARK_RED + getline.trim());
                                    tempMotd = "";
                                    while (actWord <= totWords) {
                                       tempMotd += motdWords[actWord] + " ";
                                       actWord++;
                                    }
                                } else if (actWord == totWords){
                                    getline += motdWords[actWord] + " ";
                                    if (motdType == 1) line = line.replace("%motd%", ChatColor.BLACK + getline.trim());
                                    if (motdType == 2) line = line.replace("%motd%", ChatColor.DARK_GREEN + getline.trim());
                                    if (motdType == 3) line = line.replace("%motd%", ChatColor.DARK_BLUE + getline.trim());
                                    if (motdType == 4) line = line.replace("%motd%", ChatColor.DARK_RED + getline.trim());
                                    tempMotd = "";
                                } else {
                                    getline += motdWords[actWord] + " ";
                                }
                                actWord++;
                            }
                        } else {
                            line = line.replace("%motd%", "");
                            nextMotd--;
                        }
                    }
                }
            }
            else {
                line = line.replace("%isonline%", offline);
                line = line.replace("%numpl%", offlineInteger);
                line = line.replace("%maxpl%", offlineInteger);
                line = line.replace("%motd%", "");
            }
            laa[i] = ChatColor.translateAlternateColorCodes('&', line);
        }
        return laa;
    }
}
