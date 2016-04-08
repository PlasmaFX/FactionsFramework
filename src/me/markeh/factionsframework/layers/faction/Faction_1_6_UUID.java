package me.markeh.factionsframework.layers.faction;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Location;

import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;

import me.markeh.factionsframework.FactionsFramework;
import me.markeh.factionsframework.Util;
import me.markeh.factionsframework.entities.FPlayer;
import me.markeh.factionsframework.entities.FPlayers;
import me.markeh.factionsframework.entities.Faction;
import me.markeh.factionsframework.entities.Factions;
import me.markeh.factionsframework.entities.Messenger;
import me.markeh.factionsframework.enums.Rel;
import me.markeh.factionsframework.layers.factions.Factions_1_6;

public class Faction_1_6_UUID extends Messenger implements Faction {
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public Faction_1_6_UUID(String id) {
		this.faction = this.getInstance().getFactionById(id);
	}

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private com.massivecraft.factions.Faction faction;
	
	// -------------------------------------------------- //
	// OVERRIDE METHODS
	// -------------------------------------------------- //
		
	@Override
	public String getId() {
		return this.faction.getId();
	}

	@Override
	public String getName() {
		return this.faction.getTag();
	}

	@Override
	public String getDescription() {
		return this.faction.getDescription();
	}

	@Override
	public Set<FPlayer> getMembers() {
		Set<FPlayer> members = new TreeSet<FPlayer>();
		
		for (com.massivecraft.factions.FPlayer oplayer : this.faction.getFPlayers()) {
			members.add(FPlayers.getById(oplayer.getId()));
		}
		
		return members;
	}
	
	@Override
	public Set<FPlayer> getMembersExcept(Rel... rels) {
		Set<FPlayer> members = new TreeSet<FPlayer>();
		
		ArrayList<Rel> relsList = new ArrayList<Rel>();
		for (Rel rel : rels) relsList.add(rel);
		
		for (com.massivecraft.factions.FPlayer oplayer : this.faction.getFPlayers()) {
			FPlayer fplayer = FPlayers.getById(oplayer.getId());
			
			if (relsList.contains(fplayer.getRank())) members.add(fplayer);
		}
		
		return members;

	}


	@Override
	public Set<FPlayer> getOfficers() {
		Set<FPlayer> officers = new TreeSet<FPlayer>();
		
		for (com.massivecraft.factions.FPlayer oplayer : this.faction.getFPlayersWhereRole(Role.MODERATOR)) {
			officers.add(FPlayers.getById(oplayer.getId()));
		}
		
		return officers;
	}

	@Override
	public FPlayer getLeader() {
		return FPlayers.getById(this.faction.getFPlayerAdmin().getId());
	}

	@Override
	public Location getHome() {
		return this.faction.getHome();
	}

	@Override
	public Set<Faction> getRelationsWith(Rel rel) {
		Set<Faction> factions = new TreeSet<Faction>();
		
		for (com.massivecraft.factions.Faction afaction : this.getInstance().getAllFactions()) {
			Rel newRel = Factions_1_6.convertRelationship(afaction.getRelationTo(this.faction));
			if (newRel != rel) continue;
			
			factions.add(Factions.getById(afaction.getId()));
		}
		
		return factions;
	}

	@Override
	public Rel getRelationTo(Object comparing) {
		// Convert our FactionsFramework Factions object to their object
		if (comparing instanceof Faction) {
			Faction faction = (Faction) comparing;
			
			comparing = this.getInstance().getFactionById(faction.getId());
		}
		
		// Convert our FactionsFramework FPlayer object to their object
		if (comparing instanceof FPlayer) {
			FPlayer fplayer = (FPlayer) comparing;
			
			comparing = com.massivecraft.factions.FPlayers.getInstance().getById(fplayer.getId());
		}
		
		// Cast to RelationParticipator if we can
		if (comparing instanceof RelationParticipator) {
			RelationParticipator rp = (RelationParticipator) comparing;
			
			Relation factionsRelation = this.faction.getRelationTo(rp);
						
			return Factions_1_6.convertRelationship(factionsRelation);
		}
		
		// We couldn't do it, so return null
		return null;
	}

	@Override
	public int getLandCount() {
		return this.faction.getLandRounded();
	}

	@Override
	public double getPower() {
		return this.faction.getPower();
	}

	@Override
	public boolean isPermanentFaction() {
		return this.faction.isPermanent();
	}

	@Override
	public boolean isNone() {
		return this.faction.isWilderness();
	}

	@Override
	public void msg(String msg) {
		this.faction.sendMessage(Util.colourse(msg));		
	}
	
	@Override
	public Boolean quiteDisband() {
		this.faction.remove();
		return true;
	}
	
	@Override
	public Boolean addMember(FPlayer fplayer) {
		return this.faction.addFPlayer(com.massivecraft.factions.FPlayers.getInstance().getById(fplayer.getId()));
	}
	
	@Override
	public Boolean isValid() {
		if (this.faction == null) return false;
		
		return true;
	}

	
	// -------------------------------------------------- //
	// UTILS
	// -------------------------------------------------- //
	
	// We store a cache of the instance to reduce the use of reflection
	private com.massivecraft.factions.Factions factions_1_6_instance; 
	public com.massivecraft.factions.Factions getInstance() {
		if (this.factions_1_6_instance == null) {
			try {
				// Use reflection as compiling will be a bit difficult otherwise
				return (com.massivecraft.factions.Factions) com.massivecraft.factions.Factions.class.getMethod("getInstance").invoke(this);
			} catch (Exception e) {
				FactionsFramework.get().logError(e);
				return null;
			}
		}
		
		return this.factions_1_6_instance;
	}
	
}
