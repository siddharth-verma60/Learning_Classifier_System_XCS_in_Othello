package Code;

import java.awt.Toolkit;
import java.util.*;

public class Run_GA {

	public static boolean do_GA_Subsumption = true;
	static int GA_count = 0;

	public static boolean condition_for_running_GA(ArrayList<Classifier> A, int actual_time) {
		int sum_num = 0;
		int sum_tsc_num = 0;
		for (Classifier c : A) {
			sum_tsc_num += c.getTime_stamp() * c.getNumerosity();
			sum_num += c.getNumerosity();
		}

		double current_value = (double) sum_tsc_num / sum_num;
		//System.out.println("Current value: " + current_value);
		current_value = (double) actual_time - current_value;
		//System.out.println(" actual time " + actual_time );
		
		if (current_value > Main_XCS.GA_Threshold)
			return true;
		else
			return false;
	}

	public static void running_GA(ArrayList<Classifier> A, ArrayList<Classifier> P, String input_state,
			int actual_time) {

		if (condition_for_running_GA(A, actual_time)) {

			// Run_GA.GA_count++;

			for (Classifier c : A) {
				c.setTime_stamp(actual_time);
			}

			Classifier parent1 = select_offspring(A);
			Classifier parent2 = select_offspring(A);
			try {
				Classifier child1 = (Classifier) parent1.clone();
				Classifier child2 = (Classifier) parent2.clone();

				child1.setNumerosity(1);
				child1.setTime_stamp(0);
				child2.setNumerosity(1);
				child2.setTime_stamp(0);
				child1.setExperience(0);
				child2.setExperience(0);

				if (Math.random() < Main_XCS.prob_Crossover) {
					Classifier[] cl = crossover(child1, child2);
					if (cl[0].getCondition().length() == Othello.BOARD_SIZE * Othello.BOARD_SIZE * 2
							&& cl[1].getCondition().length() == Othello.BOARD_SIZE * Othello.BOARD_SIZE * 2) {

						child1 = cl[0];
						child2 = cl[1];

						child1.setPrediction((parent1.getPrediction() + parent2.getPrediction()) / 2);
						child1.setPrediction_error(
								0.25 * (parent1.getPrediction_error() + parent2.getPrediction_error()) / 2);
						child1.setFitness(0.1 * (parent1.getFitness() + parent2.getFitness()) / 2);

						child2.setPrediction(child1.getPrediction());
						child2.setPrediction_error(child1.getPrediction_error());
						child2.setFitness(child1.getFitness());

						child1 = mutation(child1, input_state);
						child2 = mutation(child2, input_state);

						if (do_GA_Subsumption) {
							if (Subsumption.does_subsume(parent1, child1))
								parent1.setNumerosity(parent1.getNumerosity() + 1);

							else if (Subsumption.does_subsume(parent2, child1))
								parent2.setNumerosity(parent2.getNumerosity() + 1);
							else
								P = InsertionDeletion.insertion(child1, P);

							if (Subsumption.does_subsume(parent1, child2)) {
								parent1.setNumerosity(parent1.getNumerosity() + 1);
							}

							else if (Subsumption.does_subsume(parent2, child2))
								parent2.setNumerosity(parent2.getNumerosity() + 1);
							else
								P = InsertionDeletion.insertion(child2, P);

							P = InsertionDeletion.delete_from_population(P);
						}

					}
				}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}

		}
	}

	public static Classifier[] crossover(Classifier c1, Classifier c2) {
		// System.out.println("Crossover in progress");
		// System.out.println(c1.getCondition().toString() + "\n" +
		// c2.getCondition().toString());

		double x = Math.random() * (c1.getCondition().length() + 1);
		double y = Math.random() * (c2.getCondition().length() + 1);
		if (x > y) {
			double q = x;
			x = y;
			y = q;
		}

		StringBuilder s1 = c1.getCondition();
		c1.setCondition(c1.getCondition().replace((int) x, (int) y, c2.getCondition().substring((int) x, (int) y)));
		c2.setCondition(c2.getCondition().replace((int) x, (int) y, s1.substring((int) x, (int) y)));
		Classifier[] cl = new Classifier[2];

		// just a defensive mechanism though I dont think it was necessary
		if (c1.getCondition().length() == Othello.BOARD_SIZE * Othello.BOARD_SIZE * 2) {

			cl[0] = c1;
			cl[1] = c2;
		}
		return cl;// while integrating check that the classifier isnt empty
	}

	private static Classifier select_offspring(ArrayList<Classifier> A) {
		int fitness_sum = 0;
		for (Classifier c : A) {
			fitness_sum += c.getFitness();
		}
		double choice_point = Math.random() * fitness_sum;
		fitness_sum = 0;
		int flag = 0;
		for (int i = 0; i < A.size(); i++) {
			Classifier c = A.get(i);
			fitness_sum += c.getFitness();
			if (fitness_sum > choice_point) {
				flag = i;
				break;
			}
		}
		return (Classifier) A.get(flag);
	}

	private static int[] random_action_generator() {
		int col = (int) (Math.random() * 6);
		int row = (int) (Math.random() * 6);
		int flag = 0;
		if ((col == 2 && (row == 2 || row == 3)) || (col == 3 && (row == 2 || row == 3)))
			flag = 0;
		else
			flag = 1;
		int[] action_data = new int[3];
		action_data[0] = col;
		action_data[1] = row;
		action_data[2] = flag;
		return action_data;
	}

	private static Classifier mutation(Classifier cl, String input_state) {
		// System.out.println("mutation in progress");
		// System.out.println(cl.getCondition().toString());

		for (int i = 0; i < cl.getCondition().length(); i++) {
			if (Math.random() < Main_XCS.prob_Mutating) {
				if (cl.getCondition().charAt(i) == '#')
					cl.setCondition(cl.getCondition().replace(i, i + 1, Character.toString(input_state.charAt(i))));
				else {
					cl.setCondition(cl.getCondition().replace(i, i + 1, "#"));
				}
			}
			if (Math.random() < Main_XCS.prob_Mutating) {
				// generating random actions
				int count = 0;
				int[] action_data = random_action_generator();
				if (action_data[2] == 0 && count < 10) {
					count++;
					action_data = random_action_generator();
				}
				if (action_data[2] == 1) {
					Two_d_array_indices new_action = new Two_d_array_indices(action_data[0], action_data[1]);
					cl.setAction(new_action);
				}
			}
		}
		// System.out.println(cl.getCondition().toString()+"\t"+cl.getAction().toString());
		boolean flag = true;
		for (int i = 0; i < cl.getCondition().length(); ++i) {
			if (cl.getCondition().charAt(i) == '0' || cl.getCondition().charAt(i) == '1') {
				flag = false;
				break;
			}
		}
		if (flag) {
			Toolkit.getDefaultToolkit().beep();
			System.out.println("All hashes? " + flag);
		}
		return cl;
	}
}
