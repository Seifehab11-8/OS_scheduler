#include "FCFS.hpp"
#include <iostream>

FCFS::FCFS(std::vector<Process> &process) : Scheduler(process) {};
FCFS::FCFS() {};

int FCFS::schedule(){
	int first = 9999;
	int i;
	for(i=0;i<process_queue.size();i++){
		if(process_queue[i]<9999)
			first=process_queue[i].getID();
	}
	running_id = first;
	process_queue[i].setID(process_queue[i].getBurst_time()-1);
	if(process_queue[i].getBurst_time() == 0)
		process_queue.remove(i);
}
