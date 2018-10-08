package hgu.cs.discretemathematics.hw1.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class TestProcess {
	String input;
	boolean help;
	ArrayList<String> formula = new ArrayList<String>();

	public static void main(String[] args) {
		TestProcess my = new TestProcess();
		my.run(args);
	}

	private void run(String[] args) {
		Options options = createOptions();
		String line;
//		String buffer;
		String [][]a = new String[9][9];//[row][column]
		int i=0;
		int j=0;
		

		if(parseOptions(options, args)) {
			if (help){
				printHelp(options);
				return;
			}

			try {
				
//				InputStreamReader read = new InputStreamReader(new FileInputStream(input));
//				BufferedReader b = new BufferedReader(read);
//
//				while((buffer = b.readLine()) != null) {
//					a[i] = buffer.split("\\s");
//					i++;
//				}
//				for(int s=0; s< 2; s++) { //input.txt의 row
//					for(int z = 0; z<8; z++) {//column
//						System.out.print(a[s][z]+" ");
//					}
//					System.out.println();
//				}
				
				Pattern pattern = Pattern.compile("(.+a(.)(.))(.+\\s+(.))");
			
				ProcessBuilder builder = new ProcessBuilder("./z3","./formula.txt");
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
							a[i-1][j-1] = matcher.group(5);
						}
					}
				}
				
				for(int s=0; s< 8; s++) { //input.txt의 row
					for(int z = 0; z<8; z++) {//column
						System.out.print(a[s][z]+" ");
					}
					System.out.println();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
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
