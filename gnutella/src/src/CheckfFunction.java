import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class CheckfFunction implements Runnable {
	Hashtable<Integer, Integer> table;

	CheckfFunction(Hashtable<Integer, Integer> agentsTable) {
		table = agentsTable;
	}

	public void run() {

		while (true) {
			// System.out.println("This should print out every 10 seconds");
			Set<Integer> ids = table.keySet();
			ArrayList<Integer> arl = new ArrayList<Integer>();

			for (Integer ID : ids) {

				int timeout = table.get(ID);

				if (timeout <= (new Timestamp(System.currentTimeMillis()).getTime() / 1000L)) {
					// PRINT OUT WHO DIED
					System.out.println("NODE: " + ID + " is timeout!");

					// table.remove(ID);
					arl.add(ID);

				}
			}
			for (int i = 0; i < arl.size(); i++) {
				table.remove(arl.get(i));
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
}
