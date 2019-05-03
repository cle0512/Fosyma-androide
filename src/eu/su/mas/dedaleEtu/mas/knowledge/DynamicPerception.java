/**
 * 
 */
package eu.su.mas.dedaleEtu.mas.knowledge;


import java.io.Serializable;
import java.util.Date;
/**
 * @author clemence
 *
 */
public class DynamicPerception implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6700339265651065399L;


	// [(5_1, [(Tresor,(25,11h45)), (Odeur, (,11h46))], ...]
	//private List<Couple<String,List<Couple<String,Couple<Integer,Date>>>>> information_to_share;
	
	private Date time_observe;
	private boolean	stench;
	private Treasure treasure;
	
	
	
	/**
	 * 
	 */
	public DynamicPerception() {
		this.time_observe = new Date(java.lang.System.currentTimeMillis());
		this.stench = false;
		this.treasure = new Treasure();
	}

	
	public DynamicPerception(boolean s, String type, Integer quant, Integer lp, Integer force) {

		this.time_observe = new Date(java.lang.System.currentTimeMillis());
		this.stench = s;
		this.treasure = new Treasure(type, force, lp, quant);
	}
	
	
	
	
	
	public void setTreasure(Treasure t) {
		this.treasure = t;
	}
	
	
	
	
	
	
	public Date get_time_observe() {
		
		return this.time_observe;
	}
	
	public boolean get_stench() {
		
		return this.stench;
	}

	public Treasure getTreasure() {
		
		return this.treasure;
	}
	
	
	public void set_time_observe(Date d) {
		
		 this.time_observe= d;
	}

	
	public void  set_stench(boolean b) {
		
		 this.stench=b;
	}
	
	
	public String  toString() {
		// this.time_observe + 
		//return (" -S- " + this.stench + " -T- "+ this.treasure_type + " -Q- " + this.quantities ); 
		return ( " " + this.treasure.getQuantity() ); 
	}
	
	public void  print_toString() {
		
		System.out.println(" -Date- "+ this.time_observe + " -S- " + this.stench + " -T- "+ this.treasure.getType() + " -Q- " + this.treasure.getQuantity() +" -lp- "+ this.treasure.getLockpicking() + " -F req- "+ this.treasure.getStrenght()); 
	}

	
}
