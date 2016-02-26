package me.xCopyright.antiportaltrap;

import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.Location;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiPortalTrap extends JavaPlugin implements Listener {

    Map<Player, HashMap<String, List<Double>>> portalloc = new HashMap<>();
    Map<Player, Location> startloc = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPortalEvent(PlayerPortalEvent portal) {
        final Player p = portal.getPlayer();
        getServer().getScheduler().scheduleSyncDelayedTask(this, new TrapCheck(p), 1L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerMoveEvent(PlayerMoveEvent e) {
        Material material = e.getPlayer().getLocation().getBlock().getType();
        if (material == Material.PORTAL || material == Material.ENDER_PORTAL) {
            final Player p = e.getPlayer();
            getServer().getScheduler().scheduleSyncDelayedTask(this, new TrapCheck(p), 1L);
        }
    }

    private class TrapCheck implements Runnable {

        Player p;

        public TrapCheck(final Player p) {
            this.p = p;
        }

        @Override
        public void run() {
            if (p == null || !p.isOnline()) {
                return;
            }
            Location l = p.getLocation();
            Location nblock = p.getLocation().add(0, 0, -1);
            Location midnblock = p.getLocation().add(0, 1, -1);
            Location topnblock = p.getLocation().add(0, 2, -1);
            Location sblock = p.getLocation().add(0, 0, 1);
            Location midsblock = p.getLocation().add(0, 1, 1);
            Location topsblock = p.getLocation().add(0, 2, 1);
            Location eblock = p.getLocation().add(1, 0, 0);
            Location mideblock = p.getLocation().add(1, 1, 0);
            Location topeblock = p.getLocation().add(1, 2, 0);
            Location wblock = p.getLocation().add(-1, 0, 0);
            Location midwblock = p.getLocation().add(-1, 1, 0);
            Location topwblock = p.getLocation().add(-1, 2, 0);

            while (nblock.getBlock().getType() == Material.PORTAL || nblock.getBlock().getType() == Material.ENDER_PORTAL) {
                nblock.setZ(nblock.getZ() - 1.0);
                midnblock.setZ(nblock.getZ());
                topnblock.setZ(nblock.getZ());
            }
            while (sblock.getBlock().getType() == Material.PORTAL || sblock.getBlock().getType() == Material.ENDER_PORTAL) {
                sblock.setZ(sblock.getZ() + 1.0);
                midsblock.setZ(sblock.getZ());
                topsblock.setZ(sblock.getZ());
            }
            while (eblock.getBlock().getType() == Material.PORTAL || eblock.getBlock().getType() == Material.ENDER_PORTAL) {
                eblock.setX(eblock.getX() + 1.0);
                mideblock.setX(eblock.getX());
                topeblock.setX(eblock.getX());
            }
            while (wblock.getBlock().getType() == Material.PORTAL || wblock.getBlock().getType() == Material.ENDER_PORTAL) {
                wblock.setX(wblock.getX() - 1.0);
                midwblock.setX(wblock.getX());
                topwblock.setX(wblock.getX());
            }
            if ((!getConfig().getIntegerList("Allowed Blocks.Bottom").contains(nblock.getBlock().getTypeId()) || !getConfig().getIntegerList("Allowed Blocks.Top").contains(midnblock.getBlock().getTypeId())) && (!getConfig().getIntegerList("Allowed Blocks.Bottom").contains(sblock.getBlock().getTypeId()) || !getConfig().getIntegerList("Allowed Blocks.Top").contains(midsblock.getBlock().getTypeId())) && (!getConfig().getIntegerList("Allowed Blocks.Bottom").contains(eblock.getBlock().getTypeId()) || !getConfig().getIntegerList("Allowed Blocks.Top").contains(mideblock.getBlock().getTypeId())) && (!getConfig().getIntegerList("Allowed Blocks.Bottom").contains(wblock.getBlock().getTypeId()) || !getConfig().getIntegerList("Allowed Blocks.Top").contains(midwblock.getBlock().getTypeId())) && (!getConfig().getIntegerList("Allowed Blocks.Bottom").contains(midnblock.getBlock().getTypeId()) || !getConfig().getIntegerList("Allowed Blocks.Top").contains(topnblock.getBlock().getTypeId())) && (!getConfig().getIntegerList("Allowed Blocks.Bottom").contains(midsblock.getBlock().getTypeId()) || !getConfig().getIntegerList("Allowed Blocks.Top").contains(topsblock.getBlock().getTypeId())) && (!getConfig().getIntegerList("Allowed Blocks.Bottom").contains(mideblock.getBlock().getTypeId()) || !getConfig().getIntegerList("Allowed Blocks.Top").contains(topeblock.getBlock().getTypeId())) && (!getConfig().getIntegerList("Allowed Blocks.Bottom").contains(midwblock.getBlock().getTypeId()) || !getConfig().getIntegerList("Allowed Blocks.Top").contains(topwblock.getBlock().getTypeId()))) {
                HashMap<String, List<Double>> coordloc = new HashMap<>();
                List<Double> xloc = new ArrayList<>();
                List<Double> zloc = new ArrayList<>();
                startloc.put(p, l);
                xloc.add(l.getX() - 2.0);
                xloc.add(l.getX() - 1.0);
                xloc.add(l.getX());
                xloc.add(l.getX() + 1.0);
                xloc.add(l.getX() + 2.0);
                coordloc.put("x: ", xloc);
                zloc.add(l.getZ() - 2.0);
                zloc.add(l.getZ() - 1.0);
                zloc.add(l.getZ());
                zloc.add(l.getZ() + 1.0);
                zloc.add(l.getZ() + 2.0);
                coordloc.put("z: ", zloc);
                portalloc.put(p, coordloc);
                Block block = p.getLocation().getWorld().getBlockAt(p.getLocation());
                block.setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void onPortalTpLeave(PlayerTeleportEvent portaltpleave) {
        Player p = portaltpleave.getPlayer();
        if (!portalloc.containsKey(p)) {
            return;
        }
        Map<String, List<Double>> coordloc = portalloc.get(p);
        List<Double> xloc = coordloc.get("x: ");
        List<Double> zloc = coordloc.get("z: ");
        if (!xloc.contains(portaltpleave.getTo().getX()) && !zloc.contains(portaltpleave.getTo().getZ())) {
            Location l = startloc.get(p);
            Block block = l.getWorld().getBlockAt(l);
            block.setType(Material.FIRE);
            coordloc.clear();
            portalloc.remove(p);
            startloc.remove(p);
        }
    }

    @EventHandler
    public void onPortalLeave(PlayerMoveEvent portalleave) {
        Player p = portalleave.getPlayer();
        if (!portalloc.containsKey(p)) {
            return;
        }
        HashMap<String, List<Double>> coordloc = portalloc.get(p);
        List<Double> xloc = coordloc.get("x: ");
        List<Double> zloc = coordloc.get("z: ");
        if (xloc.get(0) > portalleave.getTo().getX() || xloc.get(4) < portalleave.getTo().getX() || zloc.get(0) > portalleave.getTo().getZ() || zloc.get(4) < portalleave.getTo().getZ()) {
            Location l = startloc.get(p);
            Block block = l.getWorld().getBlockAt(l);
            block.setType(Material.FIRE);
            coordloc.clear();
            portalloc.remove(p);
            startloc.remove(p);
        }
    }
}
