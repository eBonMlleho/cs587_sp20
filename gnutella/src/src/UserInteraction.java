import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class UserInteraction implements Runnable {
	List<String> AvailableFileLists;

	UserInteraction(List<String> fileLists) {
		AvailableFileLists = fileLists;
	}

	public void run() {
//		List<String> AvailableFileLists = new ArrayList<String>();
//		AvailableFileLists.add("host.txt");

		Scanner in = new Scanner(System.in);
		while (true) {
			System.out.println(
					"\n     Enter \n\t1 To print out a file\n \t2 To see available files\n \t3 quit interaction");

			int s = Integer.parseInt(in.nextLine());
			if (s == 1) {
				System.out.println("Entering file name: ");
				String fileName = in.nextLine();
				printFile(fileName);

			} else if (s == 2) {
				System.out.println(Arrays.toString(AvailableFileLists.toArray()));
			} else if (s == 3) {
				break;
			}
		}

	}

	public static void printFile(String Filename) {
		try {
			File myObj = new File(Filename);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				System.out.println(data);
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}
