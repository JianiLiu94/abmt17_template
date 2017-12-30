package ch.ethz.matsim.courses.abmt17_template.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

public class FfAreaScript extends MatsimXmlWriter {
	private Scenario scenario;
	List<String> area = new ArrayList<String>();

	
	public FfAreaScript(Scenario scenario) {	
		this.scenario = scenario;
	}
	
	private void writeCoords(String file) {
		
		int i=0;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))){
			String line=reader.readLine();
			while(line!=null){
				
				if (i == 0) { //skip first line
					line=reader.readLine();
					i++;
				}
					
				String[] fields = line.split(",");
				area.add(fields[3]);
				writeCoord(i, fields[2], fields[3]);
				//System.out.println("id "+i+ " , X: "+fields[1]+" , Y: "+fields[2]);
				line=reader.readLine();
				i++;
			}
		}
		catch (IOException e){
			throw new RuntimeException(e);
		}
		System.out.println("done");
	}
	
	private void writeCoord( int id, String X, String Y) {
		
		List<Tuple<String, String>> attsA = new ArrayList<Tuple<String, String>>();
		
		attsA.add(new Tuple<>("id",  Integer.toString(id)));
		attsA.add(new Tuple<>("x", X));
		attsA.add(new Tuple<>("y", Y));	
		
		writeStartTag("node", attsA, true);
	}
	
	public static void main(String[] args) {

		Config config = ConfigUtils.createConfig();
		//you need network as an input
		config.network().setInputFile(args[0]);
        Scenario scenario = ScenarioUtils.loadScenario(config);

		FfAreaScript area = new FfAreaScript(scenario);
		//output file
		area.write(args[1]);		
	}
	
	private void write(String file) {
		
		openFile(file);
		
		writeXmlHead();
		List<Tuple<String, String>> attsA = new ArrayList<Tuple<String, String>>();
		List<Tuple<String, String>> attsB = new ArrayList<Tuple<String, String>>();
		List<Tuple<String, String>> attsC = new ArrayList<Tuple<String, String>>();


		attsA.add(new Tuple<>("name", "Catchacar"));
		attsB.add(new Tuple<>("id", "Zurich"));
		attsC.add(new Tuple<>("id", "Zurich_buffer5"));
		
		writeStartTag("areas", null);
		writeStartTag("company", attsA);
		writeStartTag("area", attsB);

			writeCoords("/Users/jessicaweibel/Documents/School/ETH/HS17/ABMT/03_Project/scenario_2/Zurichcoords_buffer.csv");
		
		writeEndTag("area");
			
		writeStartTag("area", attsC);
			
			writeCoords("/Users/jessicaweibel/Documents/School/ETH/HS17/ABMT/03_Project/scenario_2/Zurichcoords_buffer.csv");

		writeEndTag("area");
		writeEndTag("company");
		writeEndTag("areas");
		
		close();
	}
	

}
