import java.util.*;


public class DLBDictionary implements DictionaryInterface {
	//Store Strings in an Node
	
	public DLBNode firstNode;	
	
	public class DLBNode 
	{
	
		public char value;
		public DLBNode nextValue;
		public DLBNode nextLetter;
		
		public DLBNode(char value){
			this.value = value;
			this.nextValue = null;
			this.nextLetter = null;			
		}	
	}

	public DLBDictionary() {
		this.firstNode = new DLBNode('/');	
	}

	// Add new String to end of list.  If String should come before
	// previous last string (i.e. it is out of order) sort the list.
	// We are keeping the data sorted in this implementation of
	// DictionaryInterface to make searches a bit faster.
	public boolean add(String s) {
		int charIndex = 0;	
		DLBNode currentNode = firstNode;	
		while(charIndex<s.length())
		{
			char currentChar = s.charAt(charIndex);						
			boolean added = false;
			while(!added)
			{
				if(currentNode.value == '/')
				{
					currentNode.value = currentChar;	
					DLBNode tempNode = new DLBNode('/'); 
					currentNode.nextValue = tempNode;
					DLBNode tempNode2 = new DLBNode('/'); 
					currentNode.nextLetter = tempNode2;		
					currentNode = currentNode.nextLetter;
					added = true;
					charIndex++;
				}
				else if(currentNode.value == currentChar)
				{						
					currentNode = currentNode.nextLetter;	
					added = true;
					charIndex++;
				}
				else
				{							
					currentNode = currentNode.nextValue;				
				}
			}				
		}
		currentNode.value = '^';		
		DLBNode tempNode = new DLBNode('/'); 
		currentNode.nextValue = tempNode;
		DLBNode tempNode2 = new DLBNode('/'); 
		currentNode.nextLetter = tempNode2;				
		return true;
	}

	// Implement the search method as described in the DictInterface
	
	
	public int search(StringBuilder s){
		int stringLength = s.length();
		int index = 0;
		boolean isWord = false;
		boolean isPrefix = false;		
		DLBNode currentNode = firstNode;
		while(index < stringLength)
		{	
		char currentChar = s.charAt(index);		
			if(index+1<stringLength)//if this char is not the last in the word
			{
				if(currentNode.value == '/')
				{
					return 0;
				}
				else if(currentNode.value != currentChar)//Not equal - check the next letter in the list
				{
					currentNode = currentNode.nextValue;
				}
				else//equal - go to the next letter in the string
				{
					currentNode = currentNode.nextLetter;
					index++;
				}
			}
			else//this is the last letter of the word.  Find if it is a prefix, word or not
			{
			boolean found = false;
				while(!found){
					if(currentNode.value == '/')
					{
						return 0;
					}
					if(currentNode.value == currentChar)
					{
						currentNode = currentNode.nextLetter;
						while(currentNode.value != '/')
						{	
							if(currentNode.value == '^')
							{
								isWord = true;
								currentNode = currentNode.nextValue;
							}
							else
							{
								isPrefix = true;
								currentNode = currentNode.nextValue;
							}
						}
						if(isWord && !isPrefix)
						{
							return 2;
						}
						if(isPrefix && !isWord)
						{
							return 1;
						}
						if(isPrefix && isWord)
						{
							return 3;
						}
						
					}		
					else
					{
						currentNode = currentNode.nextValue;
					}						
				}
			}
		}
		return 0;
	}
}
