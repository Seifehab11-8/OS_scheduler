#include "SJFPreemptiveScheduler.hpp"
#include <iostream>
#include <algorithm>

SJFPreemptiveScheduler::SJFPreemptiveScheduler(std::vector<Process> &process) : Scheduler(process) {}
SJFPreemptiveScheduler::SJFPreemptiveScheduler() {}

int SJFPreemptiveScheduler::schedule() {
    int id = 0;
    
    // Find the process with shortest remaining time that's available to run
    auto next_process = std::min_element(process_queue.begin(), process_queue.end(), 
        [this](const Process &a, const Process &b) {
            bool a_available = (a.getArrival_time() <= current_time) && 
                             (a.getExecution_time() < a.getBurst_time());
            bool b_available = (b.getArrival_time() <= current_time) && 
                             (b.getExecution_time() < b.getBurst_time());
            
            if (!a_available) return false;
            if (!b_available) return true;
            
            // Compare remaining time (burst - execution)
            return (a.getBurst_time() - a.getExecution_time()) < 
                   (b.getBurst_time() - b.getExecution_time());
        });

    // Check if we found a valid process to run
    if (next_process != process_queue.end() && 
        next_process->getArrival_time() <= current_time &&
        next_process->getExecution_time() < next_process->getBurst_time()) {
        
        // Preempt if different from current running process
        if (running_id != next_process->getID()) {
            running_id = next_process->getID();
            running_process = next_process;
        }
        
        id = running_id;
        
        // Execute the process for one time unit
        running_process->setExecution_time(running_process->getExecution_time() + 1);
        
        // Check if process completed
        if (running_process->getExecution_time() == running_process->getBurst_time()) {
            running_process->setwaiting_time(current_time + 1 - 
                running_process->getArrival_time() - 
                running_process->getBurst_time());
            running_process->setturnaround_time(current_time + 1 - 
                running_process->getArrival_time());
            running_id = 0; // No process running now
        }
    } else {
        // No process available to run
        running_id = 0;
    }

    current_time++;
    return id;
}
