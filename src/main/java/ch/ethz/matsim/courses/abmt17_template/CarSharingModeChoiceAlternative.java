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
	final private MyStartRentalEventHandler eventHandler;

	public CarSharingModeChoiceAlternative(CarSharingChoiceParameters params, TripPredictor tripPredictor,
			PredictionCache cache, MyStartRentalEventHandler eventHandler) {
		this.params = params;
		this.tripPredictor = tripPredictor;
		this.cache = cache;
		this.eventHandler = eventHandler;
	}
	
	public CarSharingModeChoiceAlternative(CarSharingChoiceParameters params, TripPredictor tripPredictor, MyStartRentalEventHandler eventHandler) {
		this(params, tripPredictor, new EmptyPredictionCache(), eventHandler);
	}

	@Override
	public double estimateUtility(ModeChoiceTrip trip) {
		TripPrediction prediction = cache.get(trip);

		if (prediction == null) {
			prediction = tripPredictor.predictTrip(trip);
			cache.put(trip, prediction);
		}
		
		if( eventHandler.getIteration() == 0) {
			int avgDist = 100;
			
			return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
					+ params.getBetaDistance() * prediction.getPredictedTravelDistance() 
					+ params.getBetaDistanceToCar() * avgDist;
			
		}else {
			if (eventHandler.getEsti()[(int)(trip.getDepartureTime()/3600)]==0)
			{
				return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
						+ params.getBetaDistance() * prediction.getPredictedTravelDistance() 
						+ params.getBetaDistanceToCar() * 100;
			}
			else 
				return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
					+ params.getBetaDistance() * prediction.getPredictedTravelDistance() 
					+ params.getBetaDistanceToCar() * eventHandler.getEsti()[(int)(trip.getDepartureTime()/3600)];
		}
	}

	@Override
	public boolean isChainMode() {
		return params.isChainBased();
	}
}
