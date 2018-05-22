package Code;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JButton;

public class Main_XCS {

	// Data Members
	public static final int size_Of_Population = 50000;
	public static final int GA_Threshold = 50;
	public static final int Deletion_Threshold = 25;
	public static final int Subsumption_Threshold = 20;
	public static final double prob_Crossover = 0.8;
	public static final double prob_Mutating = 0.4;
	public static final double fitness_Threshold = 0.1;
	public static final double minimum_num_of_actions_Threshold = 32;
	public static final double dont_care_Threshold = 0.4;
	public static final double aphsylant = 0.75; // For action selection
	public static final double prediction_error_knot = 0.01;
	public static final double alpha = 0.1;
	public static final double power_factor_nu = 5;
	public static final double beta = 0.1;
	public static final double gamma = 1;
	public static int actual_time = 0;

	ArrayList<Classifier> population_Set;
	ArrayList<Classifier> match_Set;
	ArrayList<Classifier> action_Set;
	ArrayList<Classifier> previous_action_set;
	static LinkedList<pair> list_action_sets;
	double[][] prediction_array;

	// Data members for csv:
	public double sum_fitness = 0;
	public double sum_prediction = 0;
	public double sum_prediction_error = 0;
	public int sum_numerosity = 0;
	public double reward = 0;

	Othello game;
	Environment env;
	JButton[][] buttons;

	// Constructor:
	public Main_XCS(Othello game) {
		this.game = game;
		buttons = game.get_buttons();
		population_Set = new ArrayList<Classifier>(size_Of_Population);
		list_action_sets = new LinkedList<>();

		// Generate population-set:
		this.generate_Population_Set();

	}

	// Input state and actions set pair class (Used in GA):
	private class pair {
		String input;
		ArrayList<Classifier> actions_Set;

		public pair(String input, ArrayList<Classifier> actions_Set) {
			this.input = input;
			this.actions_Set = actions_Set;
		}
	}

	// Member functions:
	public void run_experiment() {
		// Get the incoming state from environment:
		env = new Environment();
		String input_situation = env.get_input_state(game);
		// System.out.println("Input State:" + input_situation);

//		this.aphsylant = aphsylant;

		// Generate match-set:
		this.generate_Match_Set(input_situation, env);

		// Generate prediction array:
		this.generate_Prediction_Array();

		// Select Action:
		Two_d_array_indices action_to_execute = this.select_action(env);

		// Generate action set:
		this.generate_action_set(action_to_execute);

		// Execute action:
		env.take_action(game, action_to_execute);

		// Reward Assignment
		if (previous_action_set == null) {
			previous_action_set = action_Set;
		} else {
			QRewardUpdate(previous_action_set, this.reward, env);
			previous_action_set = action_Set;
		}
		this.reward = calculate_reward(env, game);

		// Subsumption.do_action_set_subsumption(action_Set, population_Set);

		ADD_action_set(env);

	}

	// Member Functions
	public void generate_Population_Set() {
		ObjectInputStream os = null;
		try {

			FileInputStream file_stream = new FileInputStream("Classifiers.ser");
			File time_file = new File("actual_time.txt");
			FileReader file_reader = new FileReader(time_file);
			BufferedReader bf = new BufferedReader(file_reader);

			String num = null;
			while ((num = bf.readLine()) != null) {
				actual_time = Integer.parseInt(num) + 1;
			}
			bf.close();
			os = new ObjectInputStream(file_stream);
			while (true) {
				Classifier cl;
				cl = (Classifier) os.readObject();

				this.sum_fitness += cl.getFitness();
				this.sum_prediction += cl.getPrediction();
				this.sum_prediction_error += cl.getPrediction_error();
				this.sum_numerosity += cl.getNumerosity();

				population_Set.add(cl);
			}
		} catch (FileNotFoundException ex) {
			return;
		} catch (IOException ex) {
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {

			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void generate_Match_Set(String input_condition, Environment env) {
		match_Set = new ArrayList<>();

		if (population_Set.size() == 0) {
			generate_Covering_Classifer(input_condition, new HashMap<String, Boolean>());
		}

		for (int i = 0; i < population_Set.size(); i++) {
			String population_Condition = population_Set.get(i).getCondition().toString();
			boolean bool=false;
			for (int j = 0; j < population_Condition.length(); j++) {
				if ((input_condition.charAt(j) == '1' && population_Condition.charAt(j) == '0')
						|| (input_condition.charAt(j) == '0' && population_Condition.charAt(j) == '1')) {
					bool=true; break;
				}
			}
			if(bool){
			match_Set.add(population_Set.get(i));
			}
		}

		HashMap<String, Boolean> map = new HashMap<>();
		for (int i = 0; i < match_Set.size(); i++) {
			String str = "" + match_Set.get(i).getAction().i + match_Set.get(i).getAction().j;
			map.put(str, true);
		}

		// When to instantiate covering:
		HashMap<String, Boolean> valid_move = env.get_pink();
		Set<String> ls = map.keySet();

		if (ls.size() == 0) {
			generate_Covering_Classifer(input_condition, map);
			InsertionDeletion.delete_from_population(population_Set);
			return;
		}

		for (String action : ls) {
			if (!valid_move.containsKey(action)) {
				generate_Covering_Classifer(input_condition, map);
				InsertionDeletion.delete_from_population(population_Set);
				break;
			}
		}
	}

	public void generate_Covering_Classifer(String input_condition, HashMap<String, Boolean> hashMap) {
		StringBuilder new_rule_condition = new StringBuilder();
		for (int i = 0; i < input_condition.length(); i++) {
			double rand_number = Math.random();
			if (rand_number < dont_care_Threshold) {
				new_rule_condition.append("#");
			} else {
				new_rule_condition.append(input_condition.charAt(i));
			}
		}
		for (int i = 0; i < buttons.length; i++) {
			for (int j = 0; j < buttons[i].length; j++) {
				if ((i == (Othello.BOARD_SIZE / 2 - 1) && j == (Othello.BOARD_SIZE / 2 - 1))
						|| (i == (Othello.BOARD_SIZE / 2) && j == (Othello.BOARD_SIZE / 2))
						|| (i == (Othello.BOARD_SIZE / 2 - 1) && j == (Othello.BOARD_SIZE / 2))
						|| (i == (Othello.BOARD_SIZE / 2) && j == (Othello.BOARD_SIZE / 2 - 1))) {
					continue;
				}
				if (hashMap.containsKey("" + i + j)) {
					continue;
				}
				Classifier new_classifier = new Classifier(new_rule_condition, new Two_d_array_indices(i, j));
				population_Set.add(new_classifier);
				match_Set.add(new_classifier);
			}
		}

	}

	public void generate_Prediction_Array() {
		prediction_array = new double[Othello.BOARD_SIZE][Othello.BOARD_SIZE];
		double[][] fitness_sum_array = new double[Othello.BOARD_SIZE][Othello.BOARD_SIZE];

		for (Classifier cl : match_Set) {
			int i = cl.getAction().i;
			int j = cl.getAction().j;
			prediction_array[i][j] += cl.getPrediction() * cl.getFitness();
			fitness_sum_array[i][j] += cl.getFitness();
		}

		for (int i = 0; i < fitness_sum_array.length; i++) {
			for (int j = 0; j < fitness_sum_array.length; j++) {
				if (fitness_sum_array[i][j] != 0) {
					prediction_array[i][j] /= fitness_sum_array[i][j];
				}
			}
		}
	}

	public Two_d_array_indices select_action(Environment env) {
		HashMap<String, Boolean> valid_move = env.get_pink();
		double rand_num = Math.random();
		int retvali = 0, retvalj = 0;

		// Exploitation
		if (rand_num < this.aphsylant) {
			double max = Integer.MIN_VALUE;
			for (int i = 0; i < prediction_array.length; i++) {
				for (int j = 0; j < prediction_array[i].length; j++) {
					if (prediction_array[i][j] >= max && valid_move.containsKey("" + i + j)) {
						max = prediction_array[i][j];
						retvali = i;
						retvalj = j;
					}
				}
			}
		}

		// Exploration
		else {
			Set<Entry<String, Boolean>> entry = valid_move.entrySet();
			ArrayList<String> list_of_actions = new ArrayList<>();
			for (Entry<String, Boolean> ent : entry) {
				list_of_actions.add(ent.getKey());
			}
			do {
				int rand_index = (int) (Math.random() * list_of_actions.size());
				retvali = list_of_actions.get(rand_index).charAt(0) - '0';
				retvalj = list_of_actions.get(rand_index).charAt(1) - '0';
			} while (prediction_array[retvali][retvalj] == 0);
		}

		return new Two_d_array_indices(retvali, retvalj);
	}

	public void generate_action_set(Two_d_array_indices action) {
		
		// getClass().System.out.println(action.i+",, "+action.j);
		action_Set = new ArrayList<Classifier>();
		for (Classifier cl : match_Set) {
			// System.out.print(cl.getAction().i + ","+cl.getAction().j+" ");
			if (cl.getAction().i == action.i && cl.getAction().j == action.j) {
				action_Set.add(cl);
			}
		}
		// System.out.println();
	}

	public double calculate_reward(Environment env, Othello game) {
		int[][] Board_values = env.value;
		JButton[][] buttons = game.get_buttons();
		double retval = 0;

		boolean end_phase = false;
		int occupied_cells = 0, num_blacks = 0, num_whites = 0;
		for (int i = 0; i < Board_values.length; ++i) {
			for (int j = 0; j < Board_values[i].length; ++j) {
				if (buttons[i][j].getBackground() == Color.BLACK) {
					num_blacks++;
					occupied_cells++;
				} else if (buttons[i][j].getBackground() == Color.WHITE) {
					num_whites++;
					occupied_cells++;
				}
			}
		}

		if (occupied_cells > (0.8 * buttons.length * buttons[0].length)) {
			end_phase = true;
		}

		if (!end_phase) {
			for (int i = 0; i < Board_values.length; ++i) {
				for (int j = 0; j < Board_values[i].length; ++j) {
					int w = 0;
					if (buttons[i][j].getBackground() == Color.BLACK) {
						w = 1;
					} else if (buttons[i][j].getBackground() == Color.WHITE) {
						w = -1;
					}

					retval += w * Board_values[i][j];
				}
			}
			retval /= env.value_sum;
		}

		else {
			retval = ((num_blacks - num_whites) * 0.1) / 36;
		}

		return retval;
	}

	public void QRewardUpdate(ArrayList<Classifier> ActionSet, double previous_reward, Environment env) {
		HashMap<String, Boolean> valid_move = env.get_pink();

		double sigma_numerosity = 0;
		for (Classifier cl : ActionSet) {
			sigma_numerosity += cl.getNumerosity();
		}

		double max_prediction = Integer.MIN_VALUE;
		for (int i = 0; i < prediction_array.length; i++) {
			for (int j = 0; j < prediction_array[i].length; j++) {
				if (prediction_array[i][j] >= max_prediction && valid_move.containsKey("" + i + j)) {
					max_prediction = prediction_array[i][j];
				}
			}
		}
		double P1 = (previous_reward + gamma * max_prediction);
		for (Classifier cl : ActionSet) {
			cl.setExperience(cl.getExperience() + 1);

			// Update prediction
			double p = cl.getPrediction();
			if (cl.getExperience() < (1 / beta)) {
				p = p + (P1 - p) / cl.getExperience();
			} else {
				p = p + (P1 - p) * beta;
			}
			cl.setPrediction(p);

			// Update prediction error
			double err = cl.getPrediction_error();
			if (cl.getExperience() < (1 / beta)) {
				err = err + (Math.abs(P1 - p) - err) / cl.getExperience();
			} else {
				err = err + (Math.abs(P1 - p) - err) * beta;
			}
			cl.setPrediction_error(err);

			// Update action set size estimate AScl
			double AScl = cl.getAction_set_size();
			if (cl.getExperience() < 1 / beta) {
				AScl += (sigma_numerosity - AScl) / cl.getExperience();
			} else {
				AScl += beta * (sigma_numerosity - AScl);
			}
			cl.setAction_set_size(AScl);
		}
		this.update_fitness(ActionSet);

		/**
		 * Q (st-1, at-1) <- (1-alpha) (Qst-1, at-1) + alpha*(rt + gamma* maxa
		 * Q(st, a))
		 **/
	}

	public void update_fitness(ArrayList<Classifier> Set) {
		ArrayList<Double> accuracy = new ArrayList<>();
		double accuracy_sum = 0;

		for (Classifier cl : Set) {
			double pred_error = cl.getPrediction_error();
			if (pred_error < prediction_error_knot) {
				accuracy.add(1.0);
			} else {
				accuracy.add(alpha * (Math.pow(pred_error / prediction_error_knot, -power_factor_nu)));
			}
			accuracy_sum += accuracy.get(accuracy.size() - 1) * cl.getNumerosity();
		}

		int k = 0;
		for (Classifier cl : Set) {
			double fitness = cl.getFitness();
			fitness = fitness + beta * (accuracy.get(k++) * cl.getNumerosity() / accuracy_sum - fitness);
			cl.setFitness(fitness);
		}
	}

	public void END_reward() {
		int reward = 0;
		// Winning the game
		if (game.get_winner() == "White") {
			reward = -1;
		}
		// Losing the game
		else if (game.get_winner() == "Black") {
			reward = +1;
		}

		this.END_update_set(reward);
	}

	private void END_update_set(int reward) {

		double P1 = reward;
		for (pair P : list_action_sets) {
			ArrayList<Classifier> ActionSet = P.actions_Set;
			for (Classifier cl : ActionSet) {
				// Update prediction:
				double p = cl.getPrediction();
				if (cl.getExperience() < 1 / beta) {
					p = p + (P1 - p) / cl.getExperience();
					cl.setPrediction(p);
				}
				else{
					p = p + (P1 - p) *beta;
					cl.setPrediction(p);
				}

				// Update prediction error:
				double err = cl.getPrediction_error();
				if (cl.getExperience() < (1 / beta)) {
					err = err + (Math.abs(P1 - p) - err) / cl.getExperience();
				} else {
					err = err + (Math.abs(P1 - p) - err) * beta;
				}
				cl.setPrediction_error(err);
			}
		}

		this.End_update_fitness();
	}

	public void End_update_fitness() {
		ArrayList<Double> accuracy = new ArrayList<>();
		double accuracy_sum = 0;

		for (pair P : list_action_sets) {
			ArrayList<Classifier> ActionSet = P.actions_Set;
			for (Classifier cl : ActionSet) {
				double pred_error = cl.getPrediction_error();
				if (pred_error < prediction_error_knot) {
					accuracy.add(1.0);
				} else {
					accuracy.add(alpha * (Math.pow(pred_error / prediction_error_knot, -power_factor_nu)));
				}
				accuracy_sum += accuracy.get(accuracy.size() - 1) * cl.getNumerosity();
			}
		}

		int k = 0;
		for (pair P : list_action_sets) {
			ArrayList<Classifier> ActionSet = P.actions_Set;
			for (Classifier cl : ActionSet) {
				double fitness = cl.getFitness();
				fitness = fitness + beta * (accuracy.get(k++) * cl.getNumerosity() / accuracy_sum - fitness);
				cl.setFitness(fitness);
			}
		}
	}

	public void write_population_set_to_file() {
		try {
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("Classifiers.ser", false));
			for (Classifier cl : this.population_Set) {
				os.writeObject(cl);
			}

			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			FileWriter out_actual_time = new FileWriter("actual_time.txt");
			out_actual_time.write("" + actual_time);
			out_actual_time.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void appy_GA() {
		for (pair p : list_action_sets) {
			Run_GA.running_GA(p.actions_Set, population_Set, p.input, actual_time);
		}
	}

	public void ADD_action_set(Environment env) {
		ArrayList<Classifier> action_Set_to_store = new ArrayList<>(action_Set);
		list_action_sets.add(new pair(env.get_input_state(game), action_Set_to_store));
	}

	public Parameters get_parameters() {
		Parameters p = new Parameters();
		p.fitness = this.sum_fitness / population_Set.size();
		p.numerosity = this.sum_numerosity;
		p.prediction = this.sum_prediction / population_Set.size();
		p.prediction_error = this.sum_prediction_error / population_Set.size();
		// System.out.println(this.sum_prediction_error);
		return p;
	}
}
