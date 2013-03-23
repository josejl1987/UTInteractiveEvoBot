/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synchro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author jose
 */
public class JobList extends TreeMap<Integer, Job> {
    
    
 public void resetWaitingIDJobs(){
         
     for(Job job:this.values()){
         if(job.status.equals(Job.Estado.WaitingID)){
             job.status=Job.Estado.Init;
         }
     }
 }   
 
  public   List<Integer> getJobsbyStatus(Job.Estado status){
        
       CopyOnWriteArrayList<Job> jobArray=new CopyOnWriteArrayList(this.values());
       ArrayList<Integer> remainingList=new ArrayList<Integer>();
       for(Job job:jobArray){
        if(job.getStatus().equals(status))
                {
            remainingList.add(job.getId());
        }
       
       }
       
        return remainingList;
    }
 public   List<Integer> getRemainingJobs(){
        
       CopyOnWriteArrayList<Job> jobArray=new CopyOnWriteArrayList(this.values());
       ArrayList<Integer> remainingList=new ArrayList<Integer>();
       for(Job job:jobArray){
        if(job.getStatus()==Job.Estado.Init||job.getStatus().equals(Job.Estado.Error)){
            remainingList.add(job.getId());
        }
       
       }
       
        return remainingList;
    }
    
   public     List<Integer> getFinishedJobs(){
        
       CopyOnWriteArrayList<Job> jobArray=new CopyOnWriteArrayList(this.values());
       ArrayList<Integer> remainingList=new ArrayList<Integer>();
       for(Job job:jobArray){
        if(job.getStatus()==Job.Estado.Finished){
            remainingList.add(job.getId());
        }
       
       }
       
        return remainingList;
    }
    
  public     Integer getWaitingIdJob(){
        
       CopyOnWriteArrayList<Job> jobArray=new CopyOnWriteArrayList(this.values());
       ArrayList<Integer> remainingList=new ArrayList<Integer>();
       boolean find=false;
       int id=-1;
       for(Job job:jobArray){
        if(job.getStatus()==Job.Estado.WaitingID&&!find){
           id=job.getId();
        }
       
       }
       
        return id;
    }
    
}
