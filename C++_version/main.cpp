#include "Process.hpp"
#include "Scheduler.hpp"
#include "SchedulerFactory.hpp"
#include <iostream>
#include <vector>
#include <thread>  // For sleep_for
#include <chrono>  // For chrono::seconds
#include <iomanip> // For formatting
#include <string>

int main()
{
    std::vector<Process> processes;
    int numProcesses, temp_id;
    std::string schedulerType;

    std::cout << "Enter the type of scheduler (e.g., SJFNonPreemptive): ";
    std::cin >> schedulerType;

    std::cout << "Enter the number of processes: ";
    std::cin >> numProcesses;

    for (int i = 0; i < numProcesses; ++i)
    {
        int arrivalTime, burstTime;
        std::cout << "Enter Process Arrival Time and Burst Time for Process " << i + 1 << ": ";
        std::cin >> arrivalTime >> burstTime;
        processes.emplace_back(arrivalTime, burstTime);
    }

    // Use the factory to create the scheduler
    Scheduler *scheduler = nullptr;
    try
    {
        scheduler = SchedulerFactory::createScheduler(schedulerType, processes);
    }
    catch (const std::invalid_argument &e)
    {
        std::cerr << e.what() << std::endl;
        return 1;
    }

    std::string topBorder = "+";
    std::string middleRow = "|";
    std::string bottomBorder = "+";
    std::cout << "\nSimulating Gantt Chart:\n";

    while (!scheduler->finished())
    {
        temp_id = scheduler->schedule();
        if (temp_id == 0)
        {
            middleRow += "    -    |";
        }
        else
        {
            middleRow += "    P" + std::to_string(temp_id) + "    |";
        }
        topBorder += "-----------+";
        bottomBorder += "-----------+";
        std::cout << "\n"
                  << topBorder << "\n";
        std::cout << middleRow << "\n";
        std::cout << bottomBorder << "\n";
        std::this_thread::sleep_for(std::chrono::seconds(1));
    }

    std::cout << topBorder << "\n";
    std::cout << middleRow << "\n";
    std::cout << bottomBorder << "\n";

    scheduler->printProcesses();
    std::cin >> temp_id; // just to prevent termination
    delete scheduler;
    return 0;
}
