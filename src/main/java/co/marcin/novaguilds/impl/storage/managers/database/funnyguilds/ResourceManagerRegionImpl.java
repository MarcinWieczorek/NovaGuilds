/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2018 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.impl.storage.managers.database.funnyguilds;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.enums.PreparedStatements;
import co.marcin.novaguilds.impl.basic.NovaRegionImpl;
import co.marcin.novaguilds.impl.storage.funnyguilds.MySQLStorageImpl;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResourceManagerRegionImpl extends AbstractDatabaseResourceManager<NovaRegion> {
    /**
     * The constructor
     *
     * @param storage the storage
     */
    public ResourceManagerRegionImpl(Storage storage) {
        super(storage, NovaRegion.class, "regions");
    }

    @Override
    public List<NovaRegion> load() {
        getStorage().connect();
        final List<NovaRegion> list = new ArrayList<>();

        try {
            PreparedStatement statement = getStorage().getPreparedStatement(PreparedStatements.REGIONS_SELECT);

            ResultSet res = statement.executeQuery();
            while(res.next()) {
                String guildString = res.getString("name");
                String[] homeSplit = org.apache.commons.lang.StringUtils.split(res.getString("center"), ',');
                String worldString = homeSplit[0];
                World world = plugin.getServer().getWorld(worldString);

                if(world == null) {
                    LoggerUtils.info("Failed loading region for guild " + guildString + ", world does not exist.");
                    continue;
                }

                int x = Integer.parseInt(homeSplit[1]);
                int y = Integer.parseInt(homeSplit[2]);
                int z = Integer.parseInt(homeSplit[3]);
                Location center = new Location(world, x, y, z);

                //Find the guild
                NovaGuild guild = ((MySQLStorageImpl) getStorage()).guildMap.get(guildString);

                if(guild == null) {
                    LoggerUtils.error("There's no guild matching region " + guildString);
                    continue;
                }

                NovaRegion region = new NovaRegionImpl(UUID.randomUUID());

                int size = res.getInt("size");
                Location corner1 = center.clone().add(size, 0, size);
                Location corner2 = center.clone().subtract(size, 0, size);

                region.setAdded();
                region.setCorner(0, corner1);
                region.setCorner(1, corner2);
                region.setWorld(center.getWorld());

                guild.addRegion(region);
                region.setUnchanged();
                list.add(region);
            }
        }
        catch(SQLException e) {
            LoggerUtils.exception(e);
        }

        return list;
    }

    @Override
    public boolean save(NovaRegion region) {
        throw new IllegalArgumentException("Not supported");
    }

    @Override
    public void add(NovaRegion region) {
        throw new IllegalArgumentException("Not supported");
    }

    @Override
    public boolean remove(NovaRegion region) {
        throw new IllegalArgumentException("Not supported");
    }
}
