package ch.ethz.matsim.courses.abmt17_template.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.io.MatsimXmlWriter;

public class SupplyScript extends MatsimXmlWriter {
	
	private Scenario scenario;
	private static int counter = 0;
	public SupplyScript(Scenario scenario) {
		
		this.scenario = scenario;
	}

	public void write(String file) {
		
		openFile(file);
		
		writeXmlHead();
		List<Tuple<String, String>> attsC = new ArrayList<Tuple<String, String>>();
		
		attsC.add(new Tuple<>("name", "Catchacar"));
		writeStartTag("companies", null);
		writeStartTag("company", attsC);
		
		writeVehicles();
	
		writeEndTag("company");
		
		writeEndTag("companies");

		
		close();
	}
	

	private void writeVehicles() {

		Network network = this.scenario.getNetwork();
		//here you set up how many cars you want
		int cars = 1000;
		Object[] array = network.getLinks().values().toArray();
		
		int numberLinks = array.length;
		Random r = new Random(456);
		//this part places cars randomly on links using the whole network
		//however you might want to reduce this to only the city of Zurich
		//simplest way to do it is to just place cars on links that are less
		// than 8km from a coordinate in the center of Zurich
		// you can use these coordinates as the center of Zurich:
		// x=2683217 and y=1247300
		//CoordUtils class has a method for calculating distances 
		//between two coordinates CoordUtils.calcEuclideanDistance(coord1, coord2)
		//CoordUtils.createCoord(xx, yy) can create a coordinate object with those coordinates above.
		//link.getCoord() gives you coordinate of the link
		Coord center = CoordUtils.createCoord(2683217, 1247300);
		for (int i = 0; i < cars; i++) {
			
			Link link = (Link) array[r.nextInt(numberLinks)];
			if (CoordUtils.calcEuclideanDistance(link.getCoord(), center)<=8000){
				writeVehicle(link, counter, r);
				counter++;
			}
			else{
				i--;
			}
		}		
	}

	private void writeVehicle(Link link, int id, Random random) {

		
		List<Tuple<String, String>> attsV = new ArrayList<Tuple<String, String>>();
		
		attsV.add(new Tuple<>("id", "FF_" + Integer.toString(id)));
		attsV.add(new Tuple<>("x", Double.toString(link.getCoord().getX())));
		attsV.add(new Tuple<>("y", Double.toString(link.getCoord().getY())));
		
		attsV.add(new Tuple<>("type", "car"));		

		writeStartTag("freefloating", attsV, true);		
	}
	
	

	public static void main(String[] args) {

		Config config = ConfigUtils.createConfig();
		//you need network as an input
		config.network().setInputFile(args[0]);
        Scenario scenario = ScenarioUtils.loadScenario(config);

		SupplyScript place = new SupplyScript(scenario);
		//output file i.e.e ffvehicles.xml
		place.write(args[1]);		
	}
}
