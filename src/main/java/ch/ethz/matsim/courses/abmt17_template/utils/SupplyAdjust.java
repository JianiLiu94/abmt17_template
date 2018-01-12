package ch.ethz.matsim.courses.abmt17_template.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PersonUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.io.MatsimXmlWriter;

public class SupplyAdjust extends MatsimXmlWriter{
	private Scenario scenario;
	private static int counter = 0;
	List<String> old = new ArrayList<String>();
	
	public SupplyAdjust(Scenario scenario) {
		
		this.scenario = scenario;
	}

	public void keepOld(String file){
		int i=0;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))){
			String line=reader.readLine();
			while(line!=null&&i<500){
				
				String[] fields = line.split(",");
                System.out.println(i);
				old.add(fields[4]);
				writeOldVehicle(i, fields[4], fields[5]);
				line=reader.readLine();
				i++;
			}
		}
		catch (IOException e){
			throw new RuntimeException(e);
		}
//		System.out.println("done");
	}
	
	private void writeOldVehicle(int id, String X, String Y) {

		
		List<Tuple<String, String>> attsV = new ArrayList<Tuple<String, String>>();
		
		attsV.add(new Tuple<>("id", "FF_" + Integer.toString(id)));
		attsV.add(new Tuple<>("x", X));
		attsV.add(new Tuple<>("y", Y));
		
		attsV.add(new Tuple<>("type", "car"));		

		writeStartTag("freefloating", attsV, true);		
	}
	
	private void writeVehicle(Link link, int id) {

		
		List<Tuple<String, String>> attsV = new ArrayList<Tuple<String, String>>();
		
		attsV.add(new Tuple<>("id", "FF_" + Integer.toString(id)));
		attsV.add(new Tuple<>("x", Double.toString(link.getCoord().getX())));
		attsV.add(new Tuple<>("y", Double.toString(link.getCoord().getY())));
		
		attsV.add(new Tuple<>("type", "car"));		

		writeStartTag("freefloating", attsV, true);		
	}
	
	private void writeVehicles() {

		Network network = this.scenario.getNetwork();
		//here you set up how many cars you want
		int cars = 500;
		keepOld("E:/ETH Semester 3/JAVA/abmt17_template/outputs/startRental4.csv");
		if (old.size()<cars) {
		System.out.println("need to add more random vehicles");
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
		for (int i = old.size(); i < cars; i++) {
			
			Link link = (Link) array[r.nextInt(numberLinks)];
			if (CoordUtils.calcEuclideanDistance(link.getCoord(), center)<=8000 && !old.contains(Double.toString(link.getCoord().getX()))){
				System.out.println(i);
				writeVehicle(link, i);
			}
			else{
				i--;
			}
		}
		}
	}

	
	public static void main(String[] args) {

		Config config = ConfigUtils.createConfig();
		//you need network as an input
		config.network().setInputFile(args[0]);
        Scenario scenario = ScenarioUtils.loadScenario(config);

		SupplyAdjust place = new SupplyAdjust(scenario);
		//output file i.e.e ffvehicles.xml
		place.write(args[1]);		
	}
	

	private void write(String file) {
		
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

}
