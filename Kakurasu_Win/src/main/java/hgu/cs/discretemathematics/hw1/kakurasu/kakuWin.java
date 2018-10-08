package hgu.cs.discretemathematics.hw1.kakurasu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class kakuWin {
	String input;
	boolean help;
	String line;
	String [][]a = new String[9][9];//[row][column]
	int i=0;
	int j=0;
	int x, y ;
	int [] col = new int[9];
	int [] row = new int[9];

	public static void main(String[] args) {
		kakuWin my = new kakuWin();
		my.run(args);
	}

	private void run(String[] args) {
		Options options = createOptions();

		if(parseOptions(options, args)) {
			if (help){
				printHelp(options);
				return;
			}

			try{
				Scanner scanner = new Scanner(new File(input));

				for(int i = 1; i <= 8; i++) {
					String s = scanner.next();
					row[i] = Integer.parseInt(s);
				}
				for(int i = 1; i <= 8; i++) {
					String s = scanner.next();
					col[i] = Integer.parseInt(s);
				} 

				FileWriter fileWriter = new FileWriter(new File(".\\formula.txt"));

				for (y = 1 ; y <= 8 ; y++)
					for (x = 1 ; x <= 8 ; x++) 
						fileWriter.write("(declare-const a" + y + x + " Int)\n");    

				for (y = 1 ; y <= 8 ; y++)
					for (x = 1 ; x <= 8 ; x++) 
						fileWriter.write("(assert (and (<= a" + y + x + " 1) (<= 0 a" + y + x + ")))\n");


				for (y = 1 ; y <= 8 ; y++){
					fileWriter.write("(assert (= (+ ");
					for (x = 1 ; x <= 8 ; x++)  fileWriter.write("(* a" + y + x +" "+ x + ")");
					fileWriter.write(") "+ row[y] +"))\n");
				}

				for (x = 1 ; x <= 8 ; x++){
					fileWriter.write("(assert (= (+ ");
					for (y = 1 ; y <= 8 ; y++)  fileWriter.write("(* a" + y + x + " " + y + " )");
					fileWriter.write(") " + col[x] + "))\n");
				}

				fileWriter.write("(check-sat)\n(get-model)\n");

				scanner.close();
				fileWriter.close();
				
				Pattern pattern = Pattern.compile("(.+a(.)(.))(.+\\s+(.))");

				ProcessBuilder builder = new ProcessBuilder(".\\z3.exe",".\\formula.txt");
				Process p = builder.start();
				p.waitFor();
				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

				while((line = br.readLine())!=null) {
					if(line.contains("()")) {
						line = line+(br.readLine());

						Matcher matcher = pattern.matcher(line);
						while(matcher.find()) {
							i = Integer.parseInt(matcher.group(2));
							j = Integer.parseInt(matcher.group(3));
							if(Integer.parseInt(matcher.group(5)) == 1) {
								a[i-1][j-1] = "X";
							}else
								a[i-1][j-1] = "O";
						}
					}
				}
				br.close();

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			FileWriter out;
			try {
				out = new FileWriter(".\\output.txt");
				for(int s=0; s< 8; s++) { 
					for(int z = 0; z<8; z++) {//column
						out.write(a[s][z]+" ");
					}
					out.write("\n");
				}
				
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
	}

	private boolean parseOptions(Options options, String[] args) {
		CommandLineParser parser = new DefaultParser();

		try {
			CommandLine cmd = parser.parse(options, args);

			input = cmd.getOptionValue("i");

		}catch (Exception e) {
			printHelp(options);
			return false;
		}
		return true;
	}

	private void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		String header = "PuzzleSolver";
		String footer ="\nhttps://github.com/lamb0711/PuzzleSolver";
		formatter.printHelp("Sudoku, kakurasu, 3-in-row Solver", header, options, footer, true);
	}

	private Options createOptions() {
		Options options = new Options();

		options.addOption(Option.builder("i").longOpt("path")
				.desc("Save a path of input file name")
				.hasArg()
				.argName("Input file name")
				.required()
				.build());
		options.addOption(Option.builder("h").longOpt("help")
				.desc("Help")
				.build());

		return options;	
	}

}
