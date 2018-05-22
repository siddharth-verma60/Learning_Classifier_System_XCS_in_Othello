package Code;

import java.util.*;

public class Subsumption {

	public static boolean is_more_general(Classifier cl_gen, Classifier cl_spec) {
		// gen-general
		// spec-specific
		if (cl_gen.getCondition().length() < Othello.BOARD_SIZE * Othello.BOARD_SIZE * 2
				|| cl_spec.getCondition().length() < Othello.BOARD_SIZE * Othello.BOARD_SIZE * 2)
			return false;
		int num_dont_care_cl_gen = 0;
		int num_dont_care_cl_spec = 0;
		for (int i = 0; i < cl_gen.getCondition().length(); i++) {
			if (cl_gen.getCondition().charAt(i) == '#')
				num_dont_care_cl_gen++;
			if (cl_spec.getCondition().charAt(i) == '#')
				num_dont_care_cl_spec++;
		}
		if (num_dont_care_cl_gen <= num_dont_care_cl_spec)
			return false;
		for (int j = 0; j < cl_gen.getCondition().length(); j++) {
			if (cl_gen.getCondition().charAt(j) != '#'
					&& cl_gen.getCondition().charAt(j) != cl_spec.getCondition().charAt(j))
				return false;
		}
		return true;
	}

	public static boolean could_subsume(Classifier cl) {
		if (cl.getExperience() > Main_XCS.Subsumption_Threshold) {
			//System.out.println("prediction error :" + cl.getPrediction_error());
			if (cl.getPrediction_error() < Main_XCS.prediction_error_knot) {
				//System.out.println("Hi");
				return true;
			}
		}
		return false;
	}

	public static boolean does_subsume(Classifier cl_sub, Classifier cl_tos) { // sub-subsumer
		// tos-
		// to be
		// subsumed
		if (cl_sub.getAction().i == cl_tos.getAction().i && cl_sub.getAction().j == cl_tos.getAction().j) {
			if (could_subsume(cl_sub)) {
				if (is_more_general(cl_sub, cl_tos)) {
					return true;
				}
			}
		}
		return false;
	}

	// Action set subsumption
	public static void do_action_set_subsumption(ArrayList<Classifier> A, ArrayList<Classifier> P) {
		Classifier cl = null;
		//System.out.println("In subsumption");
		for (Classifier c : A) {
			if (could_subsume(c)) {
				if (cl == null || is_more_general(c, cl)) {
					cl = c;
				}
			}
		}
		if (cl != null) {
			ArrayList<Classifier> classifier_to_be_removed = new ArrayList<Classifier>();
			//System.out.println("if part of subsumption");
			for (Classifier c : A) {
				if (is_more_general(cl, c)) {
					cl.setNumerosity(cl.getNumerosity() + c.getNumerosity());
					classifier_to_be_removed.add(c);
				}
			}
			System.out.println("Classifiers deleted: " + classifier_to_be_removed);
			A.removeAll(classifier_to_be_removed);
			P.removeAll(classifier_to_be_removed);
		}
	}
}
