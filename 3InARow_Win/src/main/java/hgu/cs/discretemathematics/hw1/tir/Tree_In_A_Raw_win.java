package hgu.cs.discretemathematics.hw1.tir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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

public class Tree_In_A_Raw_win {
	
	String input;
	boolean help;
	String line;
	String [][]a = new String[8][8];//[row][column]
	int i=0;
	int j=0;
	int x, y ;
	String [][]numbers = new String[8][8];
	String outputPath = ".\\formula.txt";
	Scanner scanner = null;
	FileWriter fileWriter = null;

	public static void main(String[] args) {
		Tree_In_A_Raw_win my = new Tree_In_A_Raw_win();
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
				scanner = new Scanner(new File(input));
				for(y = 0 ; y < 8 ; y ++) {
					for(x = 0; x < 8; x++)
						numbers[y][x] = scanner.next();
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			try {
				fileWriter = new FileWriter(new File(outputPath));

				for(y = 1 ; y <=8 ; y ++) {
					for(x = 1; x <= 8 ; x ++) {
						fileWriter.write("(declare-const a"+ y + x+ " Int)\n");
					}
				}

				for(y = 1 ; y <= 8; y++) {
					for(x = 1 ; x <= 8; x++) {
						if (numbers[y-1][x-1].equals("?"))
							fileWriter.write("(assert (and (<= a" + y + x + " 1) (<= 0 a"+ y + x+ ")))\n");
						else if (numbers[y-1][x-1].equals("O"))
							fileWriter.write("(assert (= a" + y + x + " 1))\n");
						else if (numbers[y-1][x-1].equals("X"))
							fileWriter.write("(assert (= a" + y + x+ " 0))\n");
					}
				}

				for(y = 1; y <= 8 ; y ++){
					fileWriter.write("(assert(= (+ ");
					for(x = 1; x <= 8 ; x ++) fileWriter.write("a"+ y + x + " ");
					fileWriter.write(") 4))"+ "\n");
				}

				for(x = 1; x <= 8 ; x ++){
					fileWriter.write("(assert(= (+ ");
					for(y = 1; y <= 8 ; y ++) fileWriter.write("a"+ y+ x+ " ");
					fileWriter.write(") 4))"+ "\n");
				}

				for(y = 1; y <= 8 ; y ++)
					for(x = 1; x <= 6; x++)
						fileWriter.write("(assert (or (= (+ a"+ y+ x+ " a"+ y + (x+1)+ " a"+ y + (x+2)+ ") 1) (= (+ a"+ y+ x+ " a"+ y+ (x+1)+ " a"+ y+ (x+2)+ ") 2)))"+ "\n");

				for(x = 1; x <= 8 ; x ++)
					for(y = 1; y <= 6 ; y++)
						fileWriter.write("(assert (or (= (+ a"+ y+ x+ " a"+ (y+1)+ x+ " a"+ (y+2)+ x+ ") 1) (= (+ a"+ y+ x+ " a"+ (y+1)+ x+ " a"+ (y+2)+ x+ ") 2)))"+ "\n");

				fileWriter.write("(check-sat)\n(get-model)\n");
				fileWriter.close();
				
				
				
				
				Pattern pattern = Pattern.compile("(.+a(.)(.))(.+\\s+(.))");
				
				ProcessBuilder builder = new ProcessBuilder(".\\z3.exe","./formula.txt");
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
				for(int s=0; s< 8; s++) { //input.txt row
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
