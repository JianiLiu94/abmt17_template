package ch.ethz.matsim.courses.abmt17_template;

import org.matsim.contrib.carsharing.events.StartRentalEvent;

import ch.ethz.matsim.courses.abmt17_template.events.MyStartRentalEventHandler;
import ch.ethz.matsim.mode_choice.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.mnl.ModeChoiceAlternative;
import ch.ethz.matsim.mode_choice.mnl.prediction.EmptyPredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.PredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPrediction;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPredictor;

public class CarSharingModeChoiceAlternative implements ModeChoiceAlternative{
	final private CarSharingChoiceParameters params;
	final private TripPredictor tripPredictor;
	final private PredictionCache cache;
	//final private MyStartRentalEventHandler eventHandler;

	public CarSharingModeChoiceAlternative(CarSharingChoiceParameters params, TripPredictor tripPredictor,
			PredictionCache cache) {
		this.params = params;
		this.tripPredictor = tripPredictor;
		this.cache = cache;
	}

//	public CarSharingChoiceAlternative(CarSharingChoiceParameters params, TripPredictor tripPredictor) {
//		this(params, tripPredictor, new EmptyPredictionCache());
//	}
	
	//??tells me this constructor must come before.... but this is not the case in in BasicModeChoice Alternative?

	@Override
	public double estimateUtility(ModeChoiceTrip trip) {
		TripPrediction prediction = cache.get(trip);

		if (prediction == null) {
			prediction = tripPredictor.predictTrip(trip);
			cache.put(trip, prediction);
		}
		
//		StartRentalEvent event = new StartRentalEvent();
		MyStartRentalEventHandler eventHandler;
		double[] ffCarDistances = eventHandler.getEsti();
		
		return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
				+ params.getBetaDistance() * prediction.getPredictedTravelDistance() 
				+ params.getBetaDistanceToCar() * ffCarDistances[(int)event.getTime()];
	}

	@Override
	public boolean isChainMode() {
		return params.isChainBased();
	}
}
