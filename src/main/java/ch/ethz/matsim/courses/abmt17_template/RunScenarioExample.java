package ch.ethz.matsim.courses.abmt17_template;

import java.util.List;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.Population;
import org.matsim.contrib.carsharing.runExample.RunCarsharing;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.events.handler.EventHandler;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.population.PersonUtils;
import org.matsim.core.router.costcalculators.OnlyTimeDependentTravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.ScenarioUtils;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import abmt17.pt.ABMTPTModule;
import abmt17.scoring.ABMTScoringModule;
import ch.ethz.matsim.baseline_scenario.analysis.simulation.ModeShareListenerModule;
import ch.ethz.matsim.courses.abmt17_template.events.MyStartRentalEventHandler;
import ch.ethz.matsim.courses.abmt17_template.utils.MembershipScript;
import ch.ethz.matsim.courses.abmt17_template.utils.SupplyScript;
import ch.ethz.matsim.mode_choice.ModeChoiceModel;
import ch.ethz.matsim.mode_choice.alternatives.ChainAlternatives;
import ch.ethz.matsim.mode_choice.alternatives.TripChainAlternatives;
import ch.ethz.matsim.mode_choice.mnl.BasicModeChoiceAlternative;
import ch.ethz.matsim.mode_choice.mnl.BasicModeChoiceParameters;
import ch.ethz.matsim.mode_choice.mnl.ModeChoiceMNL;
import ch.ethz.matsim.mode_choice.mnl.prediction.CrowflyDistancePredictor;
import ch.ethz.matsim.mode_choice.mnl.prediction.FixedSpeedPredictor;
import ch.ethz.matsim.mode_choice.mnl.prediction.HashPredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.NetworkPathPredictor;
import ch.ethz.matsim.mode_choice.mnl.prediction.PredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.PredictionCacheCleaner;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPredictor;
import ch.ethz.matsim.mode_choice.replanning.ModeChoiceStrategy;
import ch.ethz.matsim.mode_choice.run.MNLConfigGroup;
import ch.ethz.matsim.mode_choice.run.RemoveLongPlans;
import ch.ethz.matsim.mode_choice.selectors.OldPlanForRemovalSelector;
import ch.ethz.matsim.mode_choice.utils.QueueBasedThreadSafeDijkstra;


/**
 * This is the template for the 2017 ABMT course at ETHZ
 * 
 * With this example you can run the scenario. The only argument
 * to the script is the path to the config file (abmt_config.xml).
 * 
 * In Eclipse, for instance, you can right click on the "main"
 * method and choose "Run As ..." -> "Java Application". At
 * first you will get an error, because no command line argument
 * has been provided. In the run menu "Green Arrow" under the menu
 * bar you can now click on "Run configurations ..." where you
 * can choose "RunScenarioExample". Click on "Arguments" and 
 * set the command line arguments to "abmt_config.xml".
 * 
 * Furthermore, add the following to VM arguments:
 * -Xmx10G 
 * It will tell Java to use up to 10GB of RAM for the simulation.
 */
public class RunScenarioExample {
	static public void main(String[] args) {
		Config config = ConfigUtils.loadConfig(args[0], new MNLConfigGroup()); // Load the config file (command line argument)

		Scenario scenario = ScenarioUtils.loadScenario(config); // Load scenario

		setCaravail.set(scenario, args[1]);

		Controler controler = new Controler(scenario); // Set up simulation controller
		RunCarsharing.installCarSharing(controler);
		
		config.strategy().setMaxAgentPlanMemorySize(1);
		
		
		
		
		MyStartRentalEventHandler myHandler = new MyStartRentalEventHandler(config.controler().getOutputDirectory() + "/startRental.csv", scenario.getNetwork());
		controler.getEvents().addHandler(myHandler);
		controler.addControlerListener(myHandler);
		
		
		
		

		new RemoveLongPlans(10).run(scenario.getPopulation());

		// Set up MNL

		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				addControlerListenerBinding().to(PredictionCacheCleaner.class);
			}
			
			@Singleton @Provides
			public PredictionCacheCleaner providePredictionCacheCleaner(PredictionCache cache) {
				return new PredictionCacheCleaner(cache);
			}
			
			@Singleton @Provides
			public PredictionCache providePredictionCache() {
				return new HashPredictionCache();
			}
			
			@Singleton
			@Provides
			public ModeChoiceModel provideModeChoiceModel(Network network, @Named("car") TravelTime travelTime,
					MNLConfigGroup mnlConfig, PredictionCache cache){ChainAlternatives chainAlternatives = new TripChainAlternatives(false);
				ModeChoiceMNL model = new ModeChoiceMNL(MatsimRandom.getRandom(), chainAlternatives,
						scenario.getNetwork(), mnlConfig.getMode());			
				
				BasicModeChoiceParameters ptParameters = new BasicModeChoiceParameters(-2.897, -0.26 / 1000.0,
						-11.58 / 3600.0, false);
				BasicModeChoiceParameters walkParameters = new BasicModeChoiceParameters(0.0, 0.0, -14.799 / 3600.0,
						false);
				BasicModeChoiceParameters bikeParameters = new BasicModeChoiceParameters(-1.662, 0.0, -16.277 / 3600.0,
					    true);
				CarSharingChoiceParameters freefloatingParameters = new CarSharingChoiceParameters(-0.314, -0.7718/1000.0, -36.5225 / 3600.0,
						-0.244, false);
				
				TripPredictor carPredictor = null;

				switch (mnlConfig.getCarUtility()) {
				case NETWORK:
					carPredictor = new NetworkPathPredictor(
							new QueueBasedThreadSafeDijkstra(mnlConfig.getNumberOfThreads(), network,
									new OnlyTimeDependentTravelDisutility(travelTime), travelTime));
					break;
				case CROWFLY:
					carPredictor = new FixedSpeedPredictor(30.0 * 1000.0 / 3600.0, new CrowflyDistancePredictor());
					break;
				default:
					throw new IllegalStateException();
				}
				
				model.addModeAlternative("freefloating", new CarSharingModeChoiceAlternative(freefloatingParameters, carPredictor, cache, myHandler));
				model.addModeAlternative("pt", new BasicModeChoiceAlternative(ptParameters,
						new FixedSpeedPredictor(11.11111111111111, new CrowflyDistancePredictor())));
				model.addModeAlternative("walk", new BasicModeChoiceAlternative(walkParameters,
						new FixedSpeedPredictor(1.3888888888888888, new CrowflyDistancePredictor())));
				model.addModeAlternative("bike", new BasicModeChoiceAlternative(bikeParameters, 
						new FixedSpeedPredictor(3.611111111111111, new CrowflyDistancePredictor())));
				
				
				return model;
			}
		});
		
		
		//MNLModel.setUpModelWithRoutedPT(controler);

		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				this.bindPlanSelectorForRemoval().to(OldPlanForRemovalSelector.class);
				this.addPlanStrategyBinding("ModeChoiceStrategy").toProvider(ModeChoiceStrategy.class);
			}

		});
		

		// Some additional modules to create a more realistic simulation
		controler.addOverridingModule(new ABMTScoringModule()); // Required if scoring of activities is used
		controler.addOverridingModule(new ABMTPTModule()); // More realistic "teleportation" of public transport trips
		controler.addOverridingModule(new ModeShareListenerModule()); // Writes correct mode shares in every iteration


		controler.run();
		
		
		myHandler.close();
	}

}
