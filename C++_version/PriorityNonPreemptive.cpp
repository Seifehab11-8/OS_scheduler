#include "PriorityNonPreemptive.hpp"
#include <iostream>
#include <algorithm>

PriorityNonPreemptive::PriorityNonPreemptive(std::vector<Process> &process) : Scheduler(process) {}
PriorityNonPreemptive::PriorityNonPreemptive() {}

int PriorityNonPreemptive::schedule()
{
    int id;

    // Only search for a new process if the current one finished
    if (running_id == 0)
    {
        Process* best_process = nullptr;

        for (auto& proc : process_queue)
        {
            bool has_arrived = proc.getArrival_time() <= current_time;
            bool not_finished = proc.getExecution_time() < proc.getBurst_time();

            if (has_arrived && not_finished)
            {
                if (best_process == nullptr || proc.getPriority() < best_process->getPriority())
                {
                    best_process = &proc;
                }
            }
        }

        if (best_process != nullptr)
        {
            running_process = std::find_if(process_queue.begin(), process_queue.end(),
                                           [best_process](const Process& p) { return p.getID() == best_process->getID(); });

            running_id = best_process->getID();
        }
    }

    id = running_id;

    if (running_id != 0 && running_process != process_queue.end())
    {
        running_process->setExecution_time(running_process->getExecution_time() + 1);

        if (running_process->getExecution_time() == running_process->getBurst_time())
        {
            int finish_time = current_time + 1;
            int arrival = running_process->getArrival_time();
            int burst = running_process->getBurst_time();

            running_process->setwaiting_time(finish_time - arrival - burst);
            running_process->setturnaround_time(finish_time - arrival);
            running_id = 0;
        }
    }

    current_time++;
    return id;
}
