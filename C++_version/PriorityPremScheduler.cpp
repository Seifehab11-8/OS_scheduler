#include "PriorityPremScheduler.hpp"

PriorityPremScheduler::PriorityPremScheduler()
{
}

PriorityPremScheduler::PriorityPremScheduler(std::vector<Process> processes):Scheduler(processes)
{
}

int PriorityPremScheduler::schedule()
{
    int process_id;
    if(new_add_flag) {
        std::sort(process_queue.begin(), process_queue.end(), [](Process a, Process b) {
            if(a.getPriority() < b.getPriority()) {
                return true;
            }
            return false;
        });
    }
    for(auto &process: process_queue) {
        if(process.getArrival_time() <= current_time) {
            if(process.getBurst_time() != process.getExecution_time()) {
                process_id = process.getID();
                process.setExecution_time(process.getExecution_time() + 1);
                if(process.getBurst_time() == process.getExecution_time()) {
                    process.setwaiting_time(current_time - process.getArrival_time() - process.getBurst_time() + 1);
                    process.setturnaround_time(current_time - process.getArrival_time() + 1);
                }
                break;
            }
        }
    }
    current_time++;
    new_add_flag = false; // to avoid redundant sorting 
    return process_id;
}

void PriorityPremScheduler::addProcess(Process &process)
{
    new_add_flag = true;
    Scheduler::addProcess(process);
}

void PriorityPremScheduler::printProcesses() {
    std::sort(process_queue.begin(), process_queue.end(), [](Process a, Process b) {
        if(a.getID() < b.getID()) {
            return true;
        }
        return false;
    });
    Scheduler::printProcesses();
}

