package Code;


import java.io.Serializable;

public class Classifier implements Cloneable, Serializable{

	private static final long serialVersionUID = 7301543616319317221L;
	// Data Members:
	private StringBuilder condition = new StringBuilder();
	private Two_d_array_indices action;
	private double prediction_error = 0;
	private double fitness = 0.0001;
	private double prediction = 0.0001;
	private int experience = 0;
	private int time_stamp = 0;
	private double action_set_size = 0;
	private int numerosity = 1;

	public Classifier(StringBuilder condition, int prediction_error, int fitness, int prediction, int experience,
			int time_stamp, int action_set_size, int numerosity, Two_d_array_indices action) {

		this.condition = condition;
		this.prediction_error = prediction_error;
		this.fitness = fitness;
		this.prediction = prediction;
		this.experience = experience;
		this.time_stamp = time_stamp;
		this.action_set_size = action_set_size;
		this.numerosity = numerosity;
		this.action = action;
	}
	
	public Classifier(StringBuilder condition, Two_d_array_indices action) {

		this.condition = condition;
		this.action = action;
	}

	public double getPrediction() {
		return prediction;
	}

	public void setPrediction(double prediction) {
		this.prediction = prediction;
	}

	public void setCondition(StringBuilder condition) {
		this.condition = condition;
	}

	public double getPrediction_error() {
		return prediction_error;
	}

	public void setPrediction_error(double prediction_error) {
		this.prediction_error = prediction_error;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public int getTime_stamp() {
		return time_stamp;
	}

	public void setTime_stamp(int time_stamp) {
		this.time_stamp = time_stamp;
	}

	public double getAction_set_size() {
		return action_set_size;
	}

	public void setAction_set_size(double action_set_size) {
		this.action_set_size = action_set_size;
	}

	public int getNumerosity() {
		return numerosity;
	}

	public void setNumerosity(int numerosity) {
		this.numerosity = numerosity;
	}

	public Two_d_array_indices getAction() {
		return action;
	}

	public void setAction(Two_d_array_indices action) {
		this.action = action;
	}

	public StringBuilder getCondition() {
		return condition;
	}
	
	public Object clone()throws CloneNotSupportedException{  
        return super.clone();  
    } 

}
