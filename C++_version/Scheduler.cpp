#include "Scheduler.hpp"

Scheduler::Scheduler(std::vector<Process> &process) : process_queue(process), current_time(0), running_id(0) {}
Scheduler::Scheduler() {}
Scheduler::~Scheduler()
{
}
void Scheduler::addProcess(Process &process)
{
    process_queue.push_back(process);
}
bool Scheduler::finished()
{
    return std::all_of(process_queue.begin(), process_queue.end(), [](const Process &a)
<<<<<<< HEAD
                        {
                            return a.getBurst_time() == a.getExecution_time(); // All processes must have burst_time == 0
                        });
}
void Scheduler::printProcesses()
{
=======
                       {
                           return a.getBurst_time() == a.getExecution_time(); // All processes must have burst_time == execution time of process to be finished
                       });
}
void Scheduler::printProcesses()
{
    std::cout << "Processes :\n";
>>>>>>> e1ff5c65ad538945d5afbe6313047a24c1742524
    std::cout << "ID\tArrival Time\tBurst Time\tWaiting Time\tTurnaround Time\n";
    for (const auto &process : process_queue)
    {
        std::cout << process.getID() << "\t"
                    << process.getArrival_time() << "\t\t"
                    << process.getBurst_time() << "\t\t"
                    << process.getwaiting_time() << "\t\t"
                    << process.getturnaround_time() << "\n";
    }
}
