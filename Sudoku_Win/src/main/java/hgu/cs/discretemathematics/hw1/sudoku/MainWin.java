package hgu.cs.discretemathematics.hw1.sudoku;

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

public class MainWin {
	String input;
	boolean help;
	String line;
	String [][]a = new String[9][9];//[row][column]
	int i=0;
	int j=0;

	public static void main(String[] args) {
		MainWin my = new MainWin();
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
				FileWriter bw = new FileWriter(".\\formula.txt");

				int x=0;
				int y=0;

			
				for (x = 1 ; x <= 9 ; x++){
					for (y = 1 ; y <= 9 ; y++){
						bw.write("(declare-const a"+x+y+" Int)\n") ;
					}
				}

				
				Scanner scanner = new Scanner(new File(input));
				for(x=1 ; x<=9 ; x++){
					for(y=1; y<=9 ; y++){
						String s = scanner.next();
						if(!(s.equals("?"))){
							int i = Integer.parseInt(s);
							bw.write("(assert (= a"+x+y+" "+i+"))\n") ;
						}
					}
				}

		
				for (x = 1 ; x <= 9 ; x++){
					for (y = 1 ; y <= 9 ; y++){
						bw.write("(assert (and (<= a"+x+y+" 9) (<= 1 a"+x+y+")))\n") ;
					}
				}

			
				for (x = 1 ; x <= 9 ; x++){
					bw.write("(assert (distinct");
					for (y = 1 ; y <= 9 ; y++){
						bw.write(" a"+x+y) ;
					}
					bw.write("))\n");
				}

			
				for (x = 1 ; x <= 9 ; x++){
					bw.write("(assert (distinct");
					for (y = 1 ; y <= 9 ; y++){
						bw.write(" a"+y+x) ;
					}
					bw.write("))\n");
				}

			
				for(x=1 ;x<=3 ; x++){
					for(y=1 ; y<=3 ; y++){
						bw.write("(assert (distinct");
						for(int i=1 ; i<=3 ; i++){
							for(int j=1 ; j<=3 ; j++){
								bw.write(" a"+((x-1)*3+i)+((y-1)*3+j));
							}
						}
						bw.write("))\n");
					}
				}

				bw.write("(check-sat)\n(get-model)");

				scanner.close();
				bw.close();
				
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
							a[i-1][j-1] = matcher.group(5);
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
				for(int s=0; s< 9; s++) { 
					for(int z = 0; z<9; z++) {//column
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
