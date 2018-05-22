package Code;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JButton;

public class Environment {

	// Data Members
	private HashMap<String, Boolean> pink_array;
	// Integer[][] value = { { 100, 3, 3, 100 }, { 3, 5, 5, 3 }, { 3, 5, 5, 3 },
	// { 100, 3, 3, 100 } };
	int[][] value = { { 50, -20, 5, 5, -20, 50 }, { -20, -50, -2, -2, -50, -20 }, { 5, -2, -1, -1, -2, 5 },
			{ 5, -2, -1, -1, -2, 5 }, { -20, -50, -2, -2, -50, -20 }, { 50, -20, 5, 5, -20, 50 } };
	
	double value_sum = 620.0; // sum of all the above values.

	public Environment() {
		// TODO Auto-generated constructor stub
		this.pink_array = new HashMap<>();
	}

	public String get_input_state(Othello game) {
		JButton[][] buttons = game.get_buttons();
		StringBuilder input_state = new StringBuilder();

		for (int i = 0; i < buttons.length; i++) {
			for (int j = 0; j < buttons.length; j++) {
				if (buttons[i][j].getBackground() == Color.BLACK) {
					input_state.append("00");
				}
				if (buttons[i][j].getBackground() == Color.WHITE) {
					input_state.append("01");
				}
				if (buttons[i][j].getBackground() == Color.PINK) {
					pink_array.put("" + i + j, true);
					input_state.append("10");
				}
				if (buttons[i][j].getBackground() == Color.gray) {
					input_state.append("11");
				}
			}
		}
		return input_state.toString();

	}

	public boolean is_game_complete(Othello game) {
		return game.is_game_complete();
	}

	public HashMap<String, Boolean> get_pink() {
		return this.pink_array;
	}

	public ArrayList<Two_d_array_indices> get_pink_minimax(Othello game) {

		ArrayList<Two_d_array_indices> pink_list = new ArrayList<Two_d_array_indices>();

		JButton[][] buttons = game.get_buttons();
		for (int i = 0; i < buttons.length; i++) {
			for (int j = 0; j < buttons.length; j++) {
				if (buttons[i][j].getBackground() == Color.PINK)
					pink_list.add(new Two_d_array_indices(i, j));
			}
		}
		return pink_list;
	}

	public boolean take_action(Othello game, Two_d_array_indices action_to_execute) {
		JButton[][] buttons = game.get_buttons();
		JButton button = buttons[action_to_execute.i][action_to_execute.j];

		if (button.getBackground() != Color.pink) {
			return false;
		}

		button.doClick();
		return true;
	}
}
