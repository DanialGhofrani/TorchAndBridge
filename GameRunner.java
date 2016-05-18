
import java.util.*;
public class GameRunner 
{
	    public static void main(String[] args) 
	    {
	    List<Integer> crossingTimes=new ArrayList<Integer>();
	    crossingTimes.add(1);
	    crossingTimes.add(2);
	    crossingTimes.add(3);
	    crossingTimes.add(9);
	    riverGame t = riverGame.getGame(crossingTimes);
	    t.PrintSolution();
	    }
}
	
class riverGame
{
	private static final String[]x = {"A","B","C","D","E","F", "G", "H","I","J"};
	private List<Person> leftSide;
	private List<Person> rightSide;
	private boolean torchIsLeft;
	private int timeRequired;
	private int costOfLastStep; //the time taken to get to this step from the last step
	
	public static riverGame getGame(List<Integer> crossingTimes)
	{
		if(crossingTimes.size()>10)	
		return null;
		else
		{
			List<Person> left= new ArrayList<Person>();
			Iterator<Integer> it= crossingTimes.iterator();
			for (int i=0; i<crossingTimes.size(); i++)
			{
				Person temp= new Person(x[i],crossingTimes.get(i));
				left.add(temp);
			}
			List<Person> right= new ArrayList<Person>();//empty list
			riverGame instance= new riverGame(left,right,true,0);
			return instance;
		}
	}
	
	private riverGame (List<Person> left, List<Person> right, boolean isTorchLeft,int costOfLastStep)
	{
		this.leftSide=left; //these are passed as mutable copies, no worries
		this.rightSide=right;
		torchIsLeft=isTorchLeft;
		this.costOfLastStep=costOfLastStep;
		
	}
	public void PrintSolution()
	{
		Stack<riverGame> solutionPath= this.getBestPath();
		
		Iterator<riverGame> it = solutionPath.iterator();
		while(it.hasNext())
		{
			System.out.println(it.next());
		}
	}
	private Stack<riverGame> getBestPath()
	{
		List<riverGame> successors=this.getSuccessors();
		//base case: we are already done
		if(successors==null) 
		{
			Stack<riverGame> output= new Stack<riverGame>();
			output.push(this);
			//timeRequired is 0 by default
			return output;
		}
		//recursive case: for all successors, get their best path, then select the one that has the cheapest path+ cost to get there
		List<Stack<riverGame>> allStacks= new ArrayList<Stack<riverGame>>();
		for(riverGame rg : successors)
		{
			allStacks.add(rg.getBestPath());
		}
		
		Stack<riverGame> myStack;
		Stack<riverGame> bestSuccessor=null;
		int bestCost=1000000; //arbitrary very high number.
		for(Stack<riverGame> x : allStacks)
		{
			riverGame topOfStack= x.peek();
			if ( (topOfStack.costOfLastStep+topOfStack.timeRequired) < bestCost)
			{
					bestSuccessor=x;
					bestCost=(topOfStack.costOfLastStep+topOfStack.timeRequired);
			}
		}
		//now we need to copy the best successor stack to our own stack:
		//but before that, we need to update the minimum required time to get to goal state, which is 
		//the best cost
		this.timeRequired=bestCost;
		myStack=(Stack<riverGame>) bestSuccessor.clone(); 
		myStack.push(this);
		return myStack;
		
	}
	private List<riverGame> getSuccessors()
	{
		//this function returns a list of game states that are successors to the current state.
		List<riverGame> output=new ArrayList<riverGame> ();
		if(leftSide.size()==0) return null;
		if(torchIsLeft)
		{
			//we need to get all 2 combinations of people on the left hand side and move them to right hand side. 
			List<Pair<Integer>> indexCombinations= getIndexCombos(leftSide.size());
			for(Pair<Integer> combo: indexCombinations)
			{
				//time to cross for a pair will be the maximum time between the two. 
				int extraTime=java.lang.Math.max(leftSide.get(combo.getFirst()).getCrossingTime(),leftSide.get(combo.getSecond()).getCrossingTime());
				
				List<Person> successorLeft= new ArrayList<Person> ();
					for(int i=0; i<leftSide.size(); i++)
					{
						if(i!=combo.getFirst() && i!= combo.getSecond())
						successorLeft.add(leftSide.get(i)); //we want the reference to the same person, not a clone. 
						
					}
				List<Person> successorRight= new ArrayList<Person>();
					for(int i=0; i<rightSide.size(); i++)
					{
						successorRight.add(rightSide.get(i));
					}
				successorRight.add(leftSide.get(combo.getFirst()));
				successorRight.add(leftSide.get(combo.getSecond()));
				
				riverGame successor= new riverGame(successorLeft,successorRight,false,extraTime);
				output.add(successor);
			 }
			
		}
		else //the torch is on the right hand side. 
		{
			//we should never have only one person on the right hand side with the torch.
			//but we only want to send one person back with the torch. 
			List<riverGame> successors= new ArrayList<riverGame> ();
			for(int i=0; i<rightSide.size(); i++)
			{
				int extraTime= rightSide.get(i).getCrossingTime();
				
				List<Person> successorLeft= new ArrayList<Person> ();
				for(Person p:leftSide)
				{
					successorLeft.add(p);//add the same person
				}
				successorLeft.add(rightSide.get(i)); //add the person who is crossing.
				
				List<Person> successorRight= new ArrayList<Person>();
				for(int j=0; j<rightSide.size(); j++)
				{
					//don't add the guy who is moving
					if(j!=i) successorRight.add(rightSide.get(j));
				}
				
				riverGame successor = new riverGame(successorLeft,successorRight,true,extraTime);
				output.add(successor);
			}
		}
		return output;
	}
	public String toString()
	{
		String output="";
		if(torchIsLeft) output+="-t- ";
		for( Person p : leftSide)
		{
			output+=p.getName();
		}
		output+="====";
		
		for( Person p: rightSide)
		{
			output+=p.getName();
		}
		if(!torchIsLeft) output+=" -t-";
		
		output+= "   cost of last step: "+ this.costOfLastStep;
		output+= "   Total Time required: "+ this.timeRequired;
		return output;
	}
	
	private  List<Pair<Integer>> getIndexCombos(int size)
	{
		List<Pair<Integer>> output=new ArrayList<Pair<Integer>>();
		for(int i=0; i<size; i++)
		{
			for(int j=i+1; j<size; j++)
			{
				Pair<Integer> temp = new Pair<Integer> (i,j);
				output.add(temp);
			}
		}
		return output;
	}

}

class Pair<T>
{
	private T first;
	private T second;
	public Pair(T first, T second)
	{
		this.first=first;
		this.second=second;
	}
	public T getFirst()
	{
		return this.first;
	}
	public T getSecond()
	{
		return this.second;
	}
}

class Person
{
	private String name;
	private int timeToCross;
	public Person(String name, int timeToCross)
	{
		this.name=name;
		this.timeToCross=timeToCross;
	}
	public String getName()
	{
		return this.name;
	}
	public int getCrossingTime()
	{
		return this.timeToCross;
	}
	public String toString()
	{
		return this.name + "("+this.getCrossingTime()+")";
	}
}
