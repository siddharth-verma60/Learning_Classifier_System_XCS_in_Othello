package Code;

import java.io.Serializable;

public class Two_d_array_indices implements Serializable {

	private static final long serialVersionUID = -4777545456716332911L;
	int i, j;

	public Two_d_array_indices(int i, int j) {
		this.i = i;
		this.j = j;
	}

	public Two_d_array_indices() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		String retval = "Action: ( " + i + ", " + j + " )";
		return retval;
	}
}
