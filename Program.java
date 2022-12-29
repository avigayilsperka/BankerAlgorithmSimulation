package program5;

import java.util.ArrayList;
import java.util.Random;

public class Program
{

	final static int NUM_PROCS = 6; // How many concurrent processes
	final static int TOTAL_RESOURCES = 30; // Total resources in the system
	final static int MAX_PROC_RESOURCES = 13; // Highest amount of resources any process could need
	final static int ITERATIONS = 30; // How long to run the program
	static int totalHeldResources = 0; // How many resources are currently being held
	static Random rand = new Random();
	
	public static void main(String[] args)
	{
		// The list of processes:
		ArrayList<Proc> processes = new ArrayList<Proc>();
		for (int i = 0; i < NUM_PROCS; i++)
			processes.add(new Proc(MAX_PROC_RESOURCES - rand.nextInt(3))); // Initialize to a new Proc, with some small range for its max
		//print initial status 
		System.out.println("\n***** STATUS *****");
		System.out.println("Total Available: " + (TOTAL_RESOURCES - totalHeldResources));
		for (int k = 0; k < processes.size(); k++)
			System.out.println("Process " + k + " holds: " + processes.get(k).getHeldResources() + ", max: " +
					processes.get(k).getMaxResources() + ", claim: " + 
					(processes.get(k).getMaxResources() - processes.get(k).getHeldResources()));
		System.out.println("***** STATUS *****\n");

		
		// Run the simulation:
		for (int i = 0; i < ITERATIONS; i++)
		{
			// loop through the processes and for each one get its request
			for (int j = 0; j < processes.size(); j++)
			{
				// Get the request- pass how many are available
				int currRequest = processes.get(j).resourceRequest(TOTAL_RESOURCES - totalHeldResources);

				//ignore processes that don't ask for resources
				if (currRequest == 0)
					continue;
				
				//if a currRequest = negative number , means the process has all resources it needs, is completing and giving back that amount
				if (currRequest<0) {
					//Readjusts total available
					totalHeldResources += currRequest;
					//Proc class readjusts the process heldResources to 0
					System.out.println("Process "+(j)+" finished, returned "+ Math.abs(currRequest));
				}
				
				//currRequest == positive number - asking if it can be granted that amount
				if(currRequest>0) {
					
					//create holder variables
					int tempTotalHeldResources = totalHeldResources;
					ArrayList<Proc> tempProcesses = new ArrayList<Proc>();
					
					//deep copy process array
					for(Proc x: processes)
						tempProcesses.add(new Proc(x));
					
					boolean safe = true;
					//simulate if granted- add resources 
					tempProcesses.get(j).addResources(currRequest);
					
					//fix total held
					tempTotalHeldResources +=currRequest;
					
					//while processes did not all complete
					while(!tempProcesses.isEmpty()) {
						boolean found=false;
						
						//go through each process
						for(int x=0; x<tempProcesses.size(); x++) {
							
							//if # of resources needed is available 
							if(tempProcesses.get(x).getMaxResources()-tempProcesses.get(x).getHeldResources()<=TOTAL_RESOURCES -tempTotalHeldResources) {
								found = true;	
								//give back resources held 
								tempTotalHeldResources -=tempProcesses.get(x).getHeldResources();
								tempProcesses.remove(x);		
							}
						}
						//no process can finish, break from while
						if(!found) {
							safe = false;
							break;
						}
					}
		
					//if safe, grant request
					if(safe) {
						System.out.println("Process "+(j)+" requested "+ currRequest +", granted.");
						//add resources
						processes.get(j).addResources(currRequest);
						totalHeldResources+=currRequest;
						}
					
					else
						//deny request
						System.out.println("Process "+(j)+" requested "+ currRequest +", denied.");
				}
			
				// At the end of each iteration, give a summary of the current status:
				System.out.println("\n***** STATUS *****");
				System.out.println("Total Available: " + (TOTAL_RESOURCES - totalHeldResources));
				for (int k = 0; k < processes.size(); k++)
					System.out.println("Process " + k + " holds: " + processes.get(k).getHeldResources() + ", max: " +
							processes.get(k).getMaxResources() + ", claim: " + 
							(processes.get(k).getMaxResources() - processes.get(k).getHeldResources()));
				System.out.println("***** STATUS *****\n");
			}
		}
	}
}
