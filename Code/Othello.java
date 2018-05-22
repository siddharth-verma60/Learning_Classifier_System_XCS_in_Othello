package Code;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.border.Border;

public class Othello extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	public static final int BOARD_SIZE = 6;
	public static final int INCOMPLETE = 3;
	public static final int COMPLETE = 4;
	boolean flag_for_passing_chances = false;
	private String winner = "";
	private JButton[][] buttons;
	public boolean isBlackTurn = true;


	Othello() {
		GridLayout layout = new GridLayout(BOARD_SIZE, BOARD_SIZE);

		super.setTitle("Othello");
		super.setSize(800, 800);
		super.setResizable(false);
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
		super.setLayout(layout);

		buttons = new JButton[BOARD_SIZE][BOARD_SIZE];

		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				JButton button = new JButton();
				button.setOpaque(true);
				button.setBorderPainted(true);
				Border bored = BorderFactory.createLineBorder(Color.DARK_GRAY, 2);
				button.setBorder(bored);

				if ((i == (BOARD_SIZE / 2 - 1) && j == (BOARD_SIZE / 2 - 1))
						|| (i == (BOARD_SIZE / 2) && j == (BOARD_SIZE / 2))) {
					button.setBackground(Color.WHITE);
				} else if ((i == (BOARD_SIZE / 2 - 1) && j == (BOARD_SIZE / 2))
						|| (i == (BOARD_SIZE / 2) && j == (BOARD_SIZE / 2 - 1))) {
					button.setBackground(Color.BLACK);
				} else {
					button.setBackground(Color.gray);
				}

				button.addActionListener(this);
				this.buttons[i][j] = button;
				super.add(button);
			}
		}
		// this.isBlackTurn = false;
		this.set_Valid_Moves_Color();
		// this.isBlackTurn = true;

		super.setVisible(true);
		// xcs_agent = new Main_XCS(this);
	}

	Othello(Othello game, boolean isMax) {

		GridLayout layout = new GridLayout(BOARD_SIZE, BOARD_SIZE);

		super.setTitle("Othello");
		super.setSize(800, 800);
		super.setResizable(false);
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
		super.setLayout(layout);

		buttons = new JButton[BOARD_SIZE][BOARD_SIZE];

		isBlackTurn = isMax;

		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				JButton button = new JButton();
				button.setOpaque(true);
				button.setBorderPainted(true);
				Border bored = BorderFactory.createLineBorder(Color.DARK_GRAY, 2);
				button.setBorder(bored);
				button.setBackground(game.get_buttons()[i][j].getBackground());

				button.addActionListener(this);
				this.buttons[i][j] = button;
				super.add(button);
			}
		}

		// this.set_Valid_Moves_Color();
		super.setVisible(false);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton) e.getSource();

		int gameStatus = this.get_Game_Status();

		if (gameStatus == INCOMPLETE && source.getBackground() == Color.pink) {

			this.makeMove(source);

			this.remove_Valid_Moves_Color();

			this.change_Colors(source);

			this.isBlackTurn = !this.isBlackTurn;

			this.set_Valid_Moves_Color();

			// if (!this.isBlackTurn) {
			// xcs_agent.run_experiment();
			// }
		}

		else if (gameStatus == COMPLETE) {
			this.declareWinner();
			
			// xcs_agent.give_delayed_reward();
			// if (Main_XCS.time_for_GA > Main_XCS.GA_Threshold) {
			// xcs_agent.appy_GA();
			// Main_XCS.time_for_GA = 0;
			// }
			// xcs_agent.write_population_set_to_file();
		}

		else {
			JOptionPane.showMessageDialog(null, "Invalid Move");
		}

	}

	public JButton[][] get_buttons() {
		return this.buttons;
	}

	public void StartGame() {
		this.isBlackTurn = true;
	}

	public void declareWinner() {
		if (this.get_Game_Status() == COMPLETE) {
			int wcount = 0, bcount = 0;
			for (int i = 0; i < BOARD_SIZE; i++) {
				for (int j = 0; j < BOARD_SIZE; j++) {
					
					if (this.buttons[i][j].getBackground() == Color.BLACK) {
						bcount++;
					}
					
					if (this.buttons[i][j].getBackground() == Color.WHITE) {
						wcount++;
					}
					
				}
			}
			if (wcount > bcount) {
				winner = "White";
				//JOptionPane.showMessageDialog(null, "WHITE WINS !");
			} else if (bcount > wcount) {
				winner = "Black";
				//JOptionPane.showMessageDialog(null, "BLACK WINS !");
			} else {
				winner = "Tie";
				//JOptionPane.showMessageDialog(null, "TIE !");
			}
			setVisible(false); //you can't see me!
			dispose(); //Destroy the JFrame object
		}
	}

	public String get_winner() {
		return winner;
	}

	private int get_Game_Status() {
		int answer = COMPLETE;
		boolean flag = false;

		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				if (this.buttons[i][j].getBackground() == Color.pink) {
					flag = true;
					answer = INCOMPLETE;
				}
			}
		}

		if (!flag) {
			this.isBlackTurn = !this.isBlackTurn;
			this.set_Valid_Moves_Color();
			for (int i = 0; i < BOARD_SIZE; i++) {
				for (int j = 0; j < BOARD_SIZE; j++) {
					if (this.buttons[i][j].getBackground() == Color.pink) {
						answer = INCOMPLETE;
					}
				}
			}
		}

		return answer;
	}

	public boolean is_game_complete() {
		return get_Game_Status() == COMPLETE;
	}

	private void set_Valid_Moves_Color() {
		if (!this.isBlackTurn) {
			for (int i = 0; i < BOARD_SIZE; i++) {
				for (int j = 0; j < BOARD_SIZE; j++) {
					if (this.buttons[i][j].getBackground() == Color.BLACK) {

						// North
						if (i > 0 && this.buttons[i - 1][j].getBackground() == Color.gray) {
							int k = i;
							while (k < BOARD_SIZE && buttons[k][j].getBackground() == Color.BLACK) {
								k++;
							}
							if (k < BOARD_SIZE && buttons[k][j].getBackground() == Color.WHITE) {
								buttons[i - 1][j].setBackground(Color.pink);
								// buttons[i - 1][j].setForeground(Color.pink);
								// buttons[i - 1][j].setText("" + NORTH);
							}
						}

						// North East
						if (i > 0 && j < BOARD_SIZE - 1 && this.buttons[i - 1][j + 1].getBackground() == Color.gray) {
							int k = i, l = j;
							while (k < BOARD_SIZE && l > 0 && buttons[k][l].getBackground() == Color.BLACK) {
								k++;
								l--;
							}
							if (k < BOARD_SIZE && l > 0 && buttons[k][l].getBackground() == Color.WHITE) {
								buttons[i - 1][j + 1].setBackground(Color.pink);
								// buttons[i - 1][j +
								// 1].setForeground(Color.pink);
								// buttons[i - 1][j + 1].setText("" +
								// NORTH_EAST);
							}
						}

						// East
						if (j < BOARD_SIZE - 1 && this.buttons[i][j + 1].getBackground() == Color.gray) {
							int k = j;
							while (k >= 0 && buttons[i][k].getBackground() == Color.BLACK) {
								k--;
							}
							if (k >= 0 && buttons[i][k].getBackground() == Color.WHITE) {
								buttons[i][j + 1].setBackground(Color.pink);
								// buttons[i][j + 1].setForeground(Color.pink);
								// buttons[i][j + 1].setText("" + EAST);
							}
						}

						// South East
						if (i < BOARD_SIZE - 1 && j < BOARD_SIZE - 1
								&& this.buttons[i + 1][j + 1].getBackground() == Color.gray) {
							int k = i, l = j;
							while (k >= 0 && l >= 0 && buttons[k][l].getBackground() == Color.BLACK) {
								k--;
								l--;
							}
							if (k >= 0 && l >= 0 && buttons[k][l].getBackground() == Color.WHITE) {
								buttons[i + 1][j + 1].setBackground(Color.pink);
								// buttons[i + 1][j +
								// 1].setForeground(Color.pink);
								// buttons[i + 1][j + 1].setText("" +
								// SOUTH_EAST);
							}
						}

						// South
						if (i < BOARD_SIZE - 1 && this.buttons[i + 1][j].getBackground() == Color.gray) {
							int k = i;
							while (k >= 0 && buttons[k][j].getBackground() == Color.BLACK) {
								k--;
							}
							if (k >= 0 && buttons[k][j].getBackground() == Color.WHITE) {
								buttons[i + 1][j].setBackground(Color.pink);
								// buttons[i + 1][j].setForeground(Color.pink);
								// buttons[i + 1][j].setText("" + SOUTH);
							}
						}

						// South West
						if (j > 0 && i < BOARD_SIZE - 1 && this.buttons[i + 1][j - 1].getBackground() == Color.gray) {
							int k = i, l = j;
							while (k >= 0 && l < BOARD_SIZE && buttons[k][l].getBackground() == Color.BLACK) {
								k--;
								l++;
							}
							if (k >= 0 && l < BOARD_SIZE && buttons[k][l].getBackground() == Color.WHITE) {
								buttons[i + 1][j - 1].setBackground(Color.pink);
								// buttons[i + 1][j -
								// 1].setForeground(Color.pink);
								// buttons[i + 1][j - 1].setText("" +
								// SOUTH_WEST);
							}
						}

						// West
						if (j > 0 && this.buttons[i][j - 1].getBackground() == Color.gray) {
							int k = j;
							while (k < BOARD_SIZE && buttons[i][k].getBackground() == Color.BLACK) {
								k++;
							}
							if (k < BOARD_SIZE && buttons[i][k].getBackground() == Color.WHITE) {
								buttons[i][j - 1].setBackground(Color.pink);
								// buttons[i][j - 1].setForeground(Color.pink);
								// buttons[i][j - 1].setText("" + WEST);
							}
						}

						// North West
						if (i > 0 && j > 0 && this.buttons[i - 1][j - 1].getBackground() == Color.gray) {
							int k = i, l = j;
							while (k < BOARD_SIZE && l < BOARD_SIZE && buttons[k][l].getBackground() == Color.BLACK) {
								k++;
								l++;
							}
							if (k < BOARD_SIZE && l < BOARD_SIZE && buttons[k][l].getBackground() == Color.WHITE) {
								buttons[i - 1][j - 1].setBackground(Color.pink);
								// buttons[i - 1][j -
								// 1].setForeground(Color.pink);
								// buttons[i - 1][j - 1].setText("" +
								// NORTH_WEST);
							}
						}
					}
				}
			}
		} else {
			for (int i = 0; i < BOARD_SIZE; i++) {
				for (int j = 0; j < BOARD_SIZE; j++) {
					if (this.buttons[i][j].getBackground() == Color.WHITE) {

						// North
						if (i > 0 && j < BOARD_SIZE && this.buttons[i - 1][j].getBackground() == Color.gray) {
							int k = i;
							while (k < BOARD_SIZE && buttons[k][j].getBackground() == Color.WHITE) {
								k++;
							}
							if (k < BOARD_SIZE && buttons[k][j].getBackground() == Color.BLACK) {
								buttons[i - 1][j].setBackground(Color.pink);
								// buttons[i - 1][j].setForeground(Color.pink);
								// buttons[i - 1][j].setText("" + NORTH);
							}
						}

						// North East
						if (i > 0 && j < BOARD_SIZE - 1 && this.buttons[i - 1][j + 1].getBackground() == Color.gray) {
							int k = i, l = j;
							while (k < BOARD_SIZE && l > 0 && buttons[k][l].getBackground() == Color.WHITE) {
								k++;
								l--;
							}
							if (k < BOARD_SIZE && l > 0 && buttons[k][l].getBackground() == Color.BLACK) {
								buttons[i - 1][j + 1].setBackground(Color.pink);
								// buttons[i - 1][j +
								// 1].setForeground(Color.pink);
								// buttons[i - 1][j + 1].setText("" +
								// NORTH_EAST);
							}
						}

						// East
						if (j < BOARD_SIZE - 1 && this.buttons[i][j + 1].getBackground() == Color.gray) {
							int k = j;
							while (k >= 0 && buttons[i][k].getBackground() == Color.WHITE) {
								k--;
							}
							if (k >= 0 && buttons[i][k].getBackground() == Color.BLACK) {
								buttons[i][j + 1].setBackground(Color.pink);
								// buttons[i][j + 1].setForeground(Color.pink);
								// buttons[i][j + 1].setText("" + EAST);
							}
						}

						// South East
						if (i < BOARD_SIZE - 1 && j < BOARD_SIZE - 1
								&& this.buttons[i + 1][j + 1].getBackground() == Color.gray) {
							int k = i, l = j;
							while (k >= 0 && l >= 0 && buttons[k][l].getBackground() == Color.WHITE) {
								k--;
								l--;
							}
							if (k >= 0 && l >= 0 && buttons[k][l].getBackground() == Color.BLACK) {
								buttons[i + 1][j + 1].setBackground(Color.pink);
								// buttons[i + 1][j +
								// 1].setForeground(Color.pink);
								// buttons[i + 1][j + 1].setText("" +
								// SOUTH_EAST);
							}
						}

						// South
						if (i < BOARD_SIZE - 1 && this.buttons[i + 1][j].getBackground() == Color.gray) {
							int k = i;
							while (k >= 0 && buttons[k][j].getBackground() == Color.WHITE) {
								k--;
							}
							if (k >= 0 && buttons[k][j].getBackground() == Color.BLACK) {
								buttons[i + 1][j].setBackground(Color.pink);
								// buttons[i + 1][j].setForeground(Color.pink);
								// buttons[i + 1][j].setText("" + SOUTH);
							}
						}

						// South West
						if (i < BOARD_SIZE - 1 && j > 0 && this.buttons[i + 1][j - 1].getBackground() == Color.gray) {
							int k = i, l = j;
							while (k >= 0 && l < BOARD_SIZE && buttons[k][l].getBackground() == Color.WHITE) {
								k--;
								l++;
							}
							if (k >= 0 && l < BOARD_SIZE && buttons[k][l].getBackground() == Color.BLACK) {
								buttons[i + 1][j - 1].setBackground(Color.pink);
								// buttons[i + 1][j -
								// 1].setForeground(Color.pink);
								// buttons[i + 1][j - 1].setText("" +
								// SOUTH_WEST);
							}
						}

						// West
						if (j > 0 && this.buttons[i][j - 1].getBackground() == Color.gray) {
							int k = j;
							while (k < BOARD_SIZE && buttons[i][k].getBackground() == Color.WHITE) {
								k++;
							}
							if (k < BOARD_SIZE && buttons[i][k].getBackground() == Color.BLACK) {
								buttons[i][j - 1].setBackground(Color.pink);
								// buttons[i][j - 1].setForeground(Color.pink);
								// buttons[i][j - 1].setText("" + WEST);
							}
						}

						// North West
						if (i > 0 && j > 0 && this.buttons[i - 1][j - 1].getBackground() == Color.gray) {
							int k = i, l = j;
							while (k < BOARD_SIZE && l < BOARD_SIZE && buttons[k][l].getBackground() == Color.WHITE) {
								k++;
								l++;
							}
							if (k < BOARD_SIZE && l < BOARD_SIZE && buttons[k][l].getBackground() == Color.BLACK) {
								buttons[i - 1][j - 1].setBackground(Color.pink);
								// buttons[i - 1][j -
								// 1].setForeground(Color.pink);
								// buttons[i - 1][j - 1].setText("" +
								// NORTH_WEST);
							}
						}
					}
				}
			}
		}
	}

	private void remove_Valid_Moves_Color() {
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				if (buttons[i][j].getBackground() == Color.pink) {
					buttons[i][j].setBackground(Color.gray);
				}
			}
		}
	}

	private void makeMove(JButton button) {
		if (this.isBlackTurn) {
			button.setBackground(Color.BLACK);
		} else {
			button.setBackground(Color.WHITE);
		}
	}

	private void change_Colors(JButton button) {
		int i = 0, j = 0;
		for (i = 0; i < BOARD_SIZE; i++) {
			for (j = 0; j < BOARD_SIZE; j++) {
				if (this.buttons[i][j] == button) {
					break;
				}
			}
			if (j != BOARD_SIZE) {
				break;
			}
		}

		if (!this.isBlackTurn) {

			// North
			int l;
			int k = i + 1;
			while (k < BOARD_SIZE - 1 && buttons[k][j].getBackground() == Color.BLACK) {
				k++;
			}
			if (k < BOARD_SIZE && buttons[k][j].getBackground() == Color.WHITE) {
				k = i + 1;
				while (k < BOARD_SIZE - 1 && buttons[k][j].getBackground() == Color.BLACK) {
					buttons[k][j].setBackground(Color.WHITE);
					k++;
				}
			}

			// North East
			k = i + 1;
			l = j - 1;
			while (k < BOARD_SIZE - 1 && l > 0 && buttons[k][l].getBackground() == Color.BLACK) {
				k++;
				l--;
			}
			if (k < BOARD_SIZE && l >= 0 && buttons[k][l].getBackground() == Color.WHITE) {
				k = i + 1;
				l = j - 1;
				while (k < BOARD_SIZE - 1 && l > 0 && buttons[k][l].getBackground() == Color.BLACK) {
					buttons[k][l].setBackground(Color.WHITE);
					k++;
					l--;
				}
			}

			// East
			k = j - 1;
			while (k > 0 && buttons[i][k].getBackground() == Color.BLACK) {
				k--;
			}
			if (k >= 0 && buttons[i][k].getBackground() == Color.WHITE) {
				k = j - 1;
				while (k > 0 && buttons[i][k].getBackground() == Color.BLACK) {
					buttons[i][k].setBackground(Color.WHITE);
					k--;
				}
			}

			// South East
			k = i - 1;
			l = j - 1;
			while (k > 0 && l > 0 && buttons[k][l].getBackground() == Color.BLACK) {
				k--;
				l--;
			}
			if (k >= 0 && l >= 0 && buttons[k][l].getBackground() == Color.WHITE) {
				k = i - 1;
				l = j - 1;
				while (k > 0 && l > 0 && buttons[k][l].getBackground() == Color.BLACK) {
					buttons[k][l].setBackground(Color.WHITE);
					k--;
					l--;
				}
			}

			// South
			k = i - 1;
			while (k > 0 && buttons[k][j].getBackground() == Color.BLACK) {
				k--;
			}
			if (k >= 0 && buttons[k][j].getBackground() == Color.WHITE) {
				k = i - 1;
				while (k > 0 && buttons[k][j].getBackground() == Color.BLACK) {
					buttons[k][j].setBackground(Color.WHITE);
					k--;
				}
			}

			// South West
			k = i - 1;
			l = j + 1;
			while (k > 0 && l < BOARD_SIZE - 1 && buttons[k][l].getBackground() == Color.BLACK) {
				k--;
				l++;
			}
			if (k >= 0 && l < BOARD_SIZE && buttons[k][l].getBackground() == Color.WHITE) {
				k = i - 1;
				l = j + 1;
				while (k > 0 && l < BOARD_SIZE - 1 && buttons[k][l].getBackground() == Color.BLACK) {
					buttons[k][l].setBackground(Color.WHITE);
					k--;
					l++;
				}
			}

			// West
			k = j + 1;
			while (k < BOARD_SIZE - 1 && buttons[i][k].getBackground() == Color.BLACK) {
				k++;
			}
			if (k < BOARD_SIZE && buttons[i][k].getBackground() == Color.WHITE) {
				k = j + 1;
				while (k < BOARD_SIZE - 1 && buttons[i][k].getBackground() == Color.BLACK) {
					buttons[i][k].setBackground(Color.WHITE);
					k++;
				}
			}

			// North West
			k = i + 1;
			l = j + 1;
			while (k < BOARD_SIZE - 1 && l < BOARD_SIZE - 1 && buttons[k][l].getBackground() == Color.BLACK) {
				k++;
				l++;
			}
			if (k < BOARD_SIZE && l < BOARD_SIZE && buttons[k][l].getBackground() == Color.WHITE) {
				k = i + 1;
				l = j + 1;
				while (k < BOARD_SIZE - 1 && l < BOARD_SIZE - 1 && buttons[k][l].getBackground() == Color.BLACK) {
					buttons[k][l].setBackground(Color.WHITE);
					k++;
					l++;
				}
			}
		}

		else if (this.isBlackTurn) {

			// North
			int l;
			int k = i + 1;
			while (k < BOARD_SIZE - 1 && buttons[k][j].getBackground() == Color.WHITE) {
				k++;
			}
			if (k < BOARD_SIZE && buttons[k][j].getBackground() == Color.BLACK) {
				k = i + 1;
				while (k < BOARD_SIZE - 1 && buttons[k][j].getBackground() == Color.WHITE) {
					buttons[k][j].setBackground(Color.BLACK);
					k++;
				}
			}

			// North East
			k = i + 1;
			l = j - 1;
			while (k < BOARD_SIZE - 1 && l > 0 && buttons[k][l].getBackground() == Color.WHITE) {
				k++;
				l--;
			}
			if (k < BOARD_SIZE && l >= 0 && buttons[k][l].getBackground() == Color.BLACK) {
				k = i + 1;
				l = j - 1;
				while (k < BOARD_SIZE - 1 && l > 0 && buttons[k][l].getBackground() == Color.WHITE) {
					buttons[k][l].setBackground(Color.BLACK);
					k++;
					l--;
				}
			}

			// East
			k = j - 1;
			while (k > 0 && buttons[i][k].getBackground() == Color.WHITE) {
				k--;
			}
			if (k >= 0 && buttons[i][k].getBackground() == Color.BLACK) {
				k = j - 1;
				while (k > 0 && buttons[i][k].getBackground() == Color.WHITE) {
					buttons[i][k].setBackground(Color.BLACK);
					k--;
				}
			}

			// South East
			k = i - 1;
			l = j - 1;
			while (k > 0 && l > 0 && buttons[k][l].getBackground() == Color.WHITE) {
				k--;
				l--;
			}
			if (k >= 0 && l >= 0 && buttons[k][l].getBackground() == Color.BLACK) {
				k = i - 1;
				l = j - 1;
				while (k > 0 && l > 0 && buttons[k][l].getBackground() == Color.WHITE) {
					buttons[k][l].setBackground(Color.BLACK);
					k--;
					l--;
				}
			}

			// South
			k = i - 1;
			while (k > 0 && buttons[k][j].getBackground() == Color.WHITE) {
				k--;
			}
			if (k >= 0 && buttons[k][j].getBackground() == Color.BLACK) {
				k = i - 1;
				while (k > 0 && buttons[k][j].getBackground() == Color.WHITE) {
					buttons[k][j].setBackground(Color.BLACK);
					k--;
				}
			}

			// South West
			k = i - 1;
			l = j + 1;
			while (k > 0 && l < BOARD_SIZE - 1 && buttons[k][l].getBackground() == Color.WHITE) {
				k--;
				l++;
			}
			if (k >= 0 && l < BOARD_SIZE && buttons[k][l].getBackground() == Color.BLACK) {
				k = i - 1;
				l = j + 1;
				while (k > 0 && l < BOARD_SIZE - 1 && buttons[k][l].getBackground() == Color.WHITE) {
					buttons[k][l].setBackground(Color.BLACK);
					k--;
					l++;
				}
			}

			// West
			k = j + 1;
			while (k < BOARD_SIZE - 1 && buttons[i][k].getBackground() == Color.WHITE) {
				k++;
			}
			if (k < BOARD_SIZE && buttons[i][k].getBackground() == Color.BLACK) {
				k = j + 1;
				while (k < BOARD_SIZE - 1 && buttons[i][k].getBackground() == Color.WHITE) {
					buttons[i][k].setBackground(Color.BLACK);
					k++;
				}
			}

			// North West
			k = i + 1;
			l = j + 1;
			while (k < BOARD_SIZE - 1 && l < BOARD_SIZE - 1 && buttons[k][l].getBackground() == Color.WHITE) {
				k++;
				l++;
			}
			if (k < BOARD_SIZE && l < BOARD_SIZE && buttons[k][l].getBackground() == Color.BLACK) {
				k = i + 1;
				l = j + 1;
				while (k < BOARD_SIZE - 1 && l < BOARD_SIZE - 1 && buttons[k][l].getBackground() == Color.WHITE) {
					buttons[k][l].setBackground(Color.BLACK);
					k++;
					l++;
				}
			}
		}

	}
}