import java.util.*;
import java.io.*;

public class MyBoggle
{
	public static void main(String[] args) throws Exception
	{
		BufferedReader boardFile = null;
		String dictType = "";
		DictionaryInterface Dictionary;
		
		//Set command line arguments 
		if(args.length == 4) 
		{
			if(args[0].equals("-b") && args[2].equals("-d"))
			{
				boardFile = new BufferedReader(new FileReader(args[1]));
				dictType = args[3];
			}
			else if(args[0].equals("-d") && args[2].equals("-b"))
			{
				boardFile = new BufferedReader(new FileReader(args[3]));
				dictType = args[1];				
			}
			else
			{
				System.out.println("Please use -b and -d to tag board and dictionary files");
				System.exit(0);
			}
		} 
		else 
		{
			System.out.println("Please enter two arguments (-d dictionary -b board.txt)" + args.length);
			System.exit(0);
		}
		if(dictType.equals("simple"))
		{	
			Dictionary = new SimpleDictionary();			
		}
		else if(dictType.equals("dlb"))
		{
			Dictionary = new DLBDictionary();
		}
		else
		{
			System.out.println("-d dictionary must be simple or dlb");
			Dictionary = new DLBDictionary();		
			System.exit(0);
		}
		
		String boardLetters = boardFile.readLine();
		boardLetters = boardLetters.toLowerCase();
		char[][] board = new char[4][4];
		int index = 0;
		for(int i=0; i<board.length; i++){
			for(int j=0; j<board[i].length; j++){
				board[i][j] = boardLetters.charAt(index++);
			}
		}
			
		//fill the dictionary	
		Scanner fileScan = new Scanner(new FileInputStream("dictionary.txt"));
		String word;
		while (fileScan.hasNext())
		{
			word = fileScan.nextLine();
			Dictionary.add(word);
		}	
		
		//Search the boggle board for all words contained
		ArrayList<String> acceptedWords = new ArrayList<String>();		
		searchWords(board,acceptedWords,Dictionary);
		Collections.sort(acceptedWords);
		acceptedWords = new ArrayList<String>(new LinkedHashSet<String>(acceptedWords));
		
		//Begin Game
		System.out.println("-Board-");//Print Game Board
		for(int i=0; i<board.length; i++){
			for(int j=0; j<board[i].length; j++){
				System.out.print(board[i][j]+" ");
			}
			System.out.println();
		}	
		System.out.println("Enter words (/Q - Quit):");
		
		ArrayList<String> alreadyGuessed = new ArrayList<String>();		
		boolean quit = false;
		while(!quit)
		{
			Scanner scan = new Scanner(System.in);//Read User Input
			String guessedWord = scan.next();				
			if(guessedWord.equals("/q") || guessedWord.equals("/Q"))//Check if game is quit
			{
				quit = true;
			}
			else if(acceptedWords.contains(guessedWord) && !alreadyGuessed.contains(guessedWord))//Check if word was guessed correctly
			{
				System.out.println("Word Accepted!");
				alreadyGuessed.add(guessedWord);
			} 
			else
			{
				System.out.println("Word Not Valid");
			}
		}
		System.out.println();
		
		System.out.println("There were " + acceptedWords.size() + " total words:");
		for (int i = 0; i < acceptedWords.size(); i++) 
		{
			System.out.println(acceptedWords.get(i));
		}
		System.out.println();
		System.out.println("You found " + alreadyGuessed.size() + " correct words out of " + acceptedWords.size() + "!:");		
		for (int i = 0; i < alreadyGuessed.size(); i++) 
		{
			System.out.println(alreadyGuessed.get(i));
		}		
		float percentScore = (float)alreadyGuessed.size()/(float)acceptedWords.size()*100;
		System.out.printf("Your score: %.1f%%",percentScore);
	}	
	
	//Search the boggle board for all words contained
	public static void searchWords(char[][] board, ArrayList<String> acceptedWords, DictionaryInterface Dictionary) throws Exception
	{
		int[][] traveled = new int[4][4];		
		for(int i=0; i<board.length; i++){
			for(int j=0; j<board[i].length; j++){
				StringBuilder currentLetter = new StringBuilder();
				traceWords(board,acceptedWords,Dictionary,traveled,currentLetter,i,j);
			}
		}	
	}	
	
	public static void traceWords(char[][] board, ArrayList<String> acceptedWords, DictionaryInterface Dictionary,int[][] traveled, StringBuilder currentWord, int row, int column)
	{
		int wildCardTracker = 0;
		char alphabet = 'a';
		//Only do once without a wildcard.  Do 26 times with a wildcard.
		do
		{
			int searchResult = 0;
			traveled[row][column] = 1;
			if(board[row][column] == '*')//Check for the wildcard character
			{
				if(wildCardTracker != 0)
				{
					alphabet++;
				}
				wildCardTracker++;
				currentWord.append(alphabet);					
			}
			else
			{
				wildCardTracker = 27;
				currentWord.append(board[row][column]);					
			}

			searchResult = Dictionary.search(currentWord);
			if(searchResult == 0)
			{	
				currentWord.setLength(currentWord.length() - 1);		
			}
			else if(searchResult == 2)
			{
				if(currentWord.length()>=3)
				{
					acceptedWords.add(currentWord.toString());	
				}
				currentWord.setLength(currentWord.length() - 1);			
			}
			else
			{
				if(searchResult == 3 && currentWord.length()>=3)
				{
					acceptedWords.add(currentWord.toString());	
				}
				if(row>0&&traveled[row-1][column]!=1)//Go North
				{									
					traceWords(board,acceptedWords,Dictionary,traveled,currentWord,row-1,column);		
				}
				if(row>0&&column<3&&traveled[row-1][column+1]!=1)//Go North East
				{					
					traceWords(board,acceptedWords,Dictionary,traveled,currentWord,row-1,column+1);					
				}
				if(column<3&&traveled[row][column+1]!=1)//Go East
				{				
					traceWords(board,acceptedWords,Dictionary,traveled,currentWord,row,column+1);		
				}
				if(column<3&&row<3&&traveled[row+1][column+1]!=1)//Go South East
				{					
					traceWords(board,acceptedWords,Dictionary,traveled,currentWord,row+1,column+1);
				}
				if(row<3&&traveled[row+1][column]!=1)//Go South
				{					
					traceWords(board,acceptedWords,Dictionary,traveled,currentWord,row+1,column);			
				}
				if(row<3&&column>0&&traveled[row+1][column-1]!=1)//Go South West
				{					
					traceWords(board,acceptedWords,Dictionary,traveled,currentWord,row+1,column-1);		
				}
				if(column>0&&traveled[row][column-1]!=1)//Go West
				{					
					traceWords(board,acceptedWords,Dictionary,traveled,currentWord,row,column-1);		
				}
					if(column>0&&row>0&&traveled[row-1][column-1]!=1)//Go North West
				{					
					traceWords(board,acceptedWords,Dictionary,traveled,currentWord,row-1,column-1);		
				}
				currentWord.setLength(currentWord.length() - 1);			
			}			
			traveled[row][column] = 0;	
		}while(wildCardTracker<26);
	}
}
