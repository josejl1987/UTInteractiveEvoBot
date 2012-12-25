package GUI;

import java.util.ArrayList;
import java.util.Collection;

public class JobList extends ArrayList<Thread> {

	public JobList() {
		super();
		// TODO Auto-generated constructor stub
	}

	public JobList(Collection<? extends Thread> arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public void removeDeadThreads(){
		
		for(Thread t:this){
			if(!t.isAlive()){
				this.remove(t);
			}
		}
	}
	public void killAll(){
		for(Thread t:this){
			if(t.isAlive()){
			t.interrupt();
			}
		}
	}

}
