package ch.ethz.matsim.courses.abmt17_template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.population.PersonUtils;
import org.matsim.core.utils.geometry.CoordUtils;

public class setCaravail {
	
	static public void set(Scenario scenario, String file){
		try (BufferedReader reader = new BufferedReader(new FileReader(file))){
			String line=reader.readLine();
			while(line!=null){
				
				String[] fields = line.split(",");
				//equals
				if (!fields[2].equals("never")&&!fields[2].equals("Car") ){
					Id<Person> personID = Id.createPersonId(fields[1]);
					PersonUtils.setCarAvail(scenario.getPopulation().getPersons().get(personID), "never");
					setPlanMode(scenario.getPopulation().getPersons().get(personID));
				}
				line=reader.readLine();
			}
		}
		catch (IOException e){
			throw new RuntimeException(e);
		}
	}

	private static void setPlanMode(Person person) {
		// TODO Auto-generated method stub
		double walkDistFactor=1.2;
		double walkSpeed=1.3888889;

				Plan plan = person.getSelectedPlan();
				List<Coord> coords = new ArrayList<Coord>();				
				
				for(PlanElement pe : plan.getPlanElements()) {
					if(pe instanceof Activity) {				
						coords.add( ((Activity)pe).getCoord() );					
					} 					
				}
				int i = 0;
				for (PlanElement pe : plan.getPlanElements()) {					
					if(pe instanceof Leg) {
						i++;
						if( ((Leg) pe).getMode().equals ("car")) 
						{ 							
							double dist = CoordUtils.calcEuclideanDistance(coords.get(i-1), coords.get(i));							
							double traveltime = dist*walkDistFactor/walkSpeed;							
							((Leg) pe).setTravelTime(traveltime);							
							((Leg) pe).setMode("walk");
						}
					}			
				}	
	}
		
}
